package cn.lomis.loop;

import java.lang.reflect.Modifier;

public class Handler {
	private static final boolean FIND_POTENTIAL_LEAKS = false;

	public interface Callback {
		public boolean handleMessage(Message msg);
	}

	/**
	 * 处理消息
	 * @param msg
	 */
	public void handleMessage(Message msg) {
	}

	/**
	 * 处理消息
	 * @param msg
	 */
	public void dispatchMessage(Message msg) {
		if (msg.callback != null) {
			handleCallback(msg);
		} else {
			if (mCallback != null) {
				if (mCallback.handleMessage(msg)) {
					return;
				}
			}
			handleMessage(msg);
		}
	}

	// ==============================================================
	// 构造方法
	// ==============================================================
	public Handler() {
		this(null, false);
	}

	public Handler(Callback callback) {
		this(callback, false);
	}

	public Handler(Looper looper) {
		this(looper, null, false);
	}

	public Handler(Looper looper, Callback callback) {
		this(looper, callback, false);
	}

	public Handler(boolean async) {
		this(null, async);
	}

	/**
	 * 构造方法
	 * @param callback 回调
	 * @param async 是否异步
	 */
	public Handler(Callback callback, boolean async) {
		if (FIND_POTENTIAL_LEAKS) {
			final Class<? extends Handler> klass = getClass();
			if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) && (klass.getModifiers() & Modifier.STATIC) == 0) {
				System.out.println("The following Handler class should be static or leaks might occur: " + klass.getCanonicalName());
			}
		}

		mLooper = Looper.myLooper();
		if (mLooper == null) {
			throw new RuntimeException("Can't create handler inside thread that has not called Looper.prepare()");
		}
		mQueue = mLooper.mQueue;
		mCallback = callback;
		mAsynchronous = async;
	}

	public Handler(Looper looper, Callback callback, boolean async) {
		mLooper = looper;
		mQueue = looper.mQueue;
		mCallback = callback;
		mAsynchronous = async;
	}

	/**
	 * 返回表示指定消息名称的字符串
	 * @param message
	 * @return
	 */
	public String getMessageName(Message message) {
		if (message.callback != null) {
			return message.callback.getClass().getName();
		}
		return "0x" + Integer.toHexString(message.what);
	}

	/**
	 * 获取消息
	 * @return
	 */
	public final Message obtainMessage() {
		return Message.obtain(this);
	}

	// ==============================================================
	// 获取消息
	// ==============================================================
	public final Message obtainMessage(int what) {
		return Message.obtain(this, what);
	}

	public final Message obtainMessage(int what, Object obj) {
		return Message.obtain(this, what, obj);
	}

	public final Message obtainMessage(int what, int arg1, int arg2) {
		return Message.obtain(this, what, arg1, arg2);
	}

	public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
		return Message.obtain(this, what, arg1, arg2, obj);
	}

	// ==============================================================
	// 设置消息
	// ==============================================================
	public final boolean post(Runnable r) {
		return sendMessageDelayed(getPostMessage(r), 0);
	}

	public final boolean postAtTime(Runnable r, long uptimeMillis) {
		return sendMessageAtTime(getPostMessage(r), uptimeMillis);
	}

	public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
		return sendMessageAtTime(getPostMessage(r, token), uptimeMillis);
	}

	public final boolean postDelayed(Runnable r, long delayMillis) {
		return sendMessageDelayed(getPostMessage(r), delayMillis);
	}

	public final boolean postAtFrontOfQueue(Runnable r) {
		return sendMessageAtFrontOfQueue(getPostMessage(r));
	}

	public final boolean runWithScissors(final Runnable r, long timeout) {
		if (r == null) {
			throw new IllegalArgumentException("runnable must not be null");
		}
		if (timeout < 0) {
			throw new IllegalArgumentException("timeout must be non-negative");
		}

		if (Looper.myLooper() == mLooper) {
			r.run();
			return true;
		}

		BlockingRunnable br = new BlockingRunnable(r);
		return br.postAndWait(this, timeout);
	}

	// ==============================================================
	// 移除回调
	// ==============================================================
	public final void removeCallbacks(Runnable r) {
		mQueue.removeMessages(this, r, null);
	}

	public final void removeCallbacks(Runnable r, Object token) {
		mQueue.removeMessages(this, r, token);
	}

	
	// ==============================================================
	// 发送消息
	// ==============================================================
	public final boolean sendMessage(Message msg) {
		return sendMessageDelayed(msg, 0);
	}

	public final boolean sendEmptyMessage(int what) {
		return sendEmptyMessageDelayed(what, 0);
	}

	public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
		Message msg = Message.obtain();
		msg.what = what;
		return sendMessageDelayed(msg, delayMillis);
	}

	public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
		Message msg = Message.obtain();
		msg.what = what;
		return sendMessageAtTime(msg, uptimeMillis);
	}

	/**
	 * 延迟发送消息
	 * @param msg
	 * @param delayMillis
	 * @return
	 */
	public final boolean sendMessageDelayed(Message msg, long delayMillis) {
		if (delayMillis < 0) {
			delayMillis = 0;
		}
		return sendMessageAtTime(msg, System.currentTimeMillis() + delayMillis);
	}

	/**
	 * 定时发送消息
	 * @param msg
	 * @param uptimeMillis
	 * @return
	 */
	public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
		MessageQueue queue = mQueue;
		if (queue == null) {
			// RuntimeException e = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
			// Log.w("Looper", e.getMessage(), e);
			return false;
		}
		return enqueueMessage(queue, msg, uptimeMillis);
	}

	/**
	 * 优先执行的消息
	 * @param msg
	 * @return
	 */
	public final boolean sendMessageAtFrontOfQueue(Message msg) {
		MessageQueue queue = mQueue;
		if (queue == null) {
			// RuntimeException e = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
			// Log.w("Looper", e.getMessage(), e);
			return false;
		}
		return enqueueMessage(queue, msg, 0);
	}

	/**
	 * 消息重排
	 * @param queue
	 * @param msg
	 * @param uptimeMillis
	 * @return
	 */
	private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
		msg.target = this;
		if (mAsynchronous) {
			msg.setAsynchronous(true);
		}
		return queue.enqueueMessage(msg, uptimeMillis);
	}

	
	// ==============================================================
	// 移除消息
	// ==============================================================
	public final void removeMessages(int what) {
		mQueue.removeMessages(this, what, null);
	}

	public final void removeMessages(int what, Object object) {
		mQueue.removeMessages(this, what, object);
	}

	public final void removeCallbacksAndMessages(Object token) {
		mQueue.removeCallbacksAndMessages(this, token);
	}

	
	// ==============================================================
	// 判断是否存在消息
	// ==============================================================
	public final boolean hasMessages(int what) {
		return mQueue.hasMessages(this, what, null);
	}

	public final boolean hasMessages(int what, Object object) {
		return mQueue.hasMessages(this, what, object);
	}

	public final boolean hasCallbacks(Runnable r) {
		return mQueue.hasMessages(this, r, null);
	}

	public final Looper getLooper() {
		return mLooper;
	}

	private static Message getPostMessage(Runnable r) {
		Message m = Message.obtain();
		m.callback = r;
		return m;
	}

	private static Message getPostMessage(Runnable r, Object token) {
		Message m = Message.obtain();
		m.obj = token;
		m.callback = r;
		return m;
	}

	private static void handleCallback(Message message) {
		message.callback.run();
	}

	final Looper mLooper;
	final MessageQueue mQueue;
	final Callback mCallback;
	final boolean mAsynchronous;

	private static final class BlockingRunnable implements Runnable {
		private final Runnable mTask;
		private boolean mDone;

		public BlockingRunnable(Runnable task) {
			mTask = task;
		}

		@Override
		public void run() {
			try {
				mTask.run();
			} finally {
				synchronized (this) {
					mDone = true;
					notifyAll();
				}
			}
		}

		public boolean postAndWait(Handler handler, long timeout) {
			if (!handler.post(this)) {
				return false;
			}

			synchronized (this) {
				if (timeout > 0) {
					final long expirationTime = System.currentTimeMillis() + timeout;
					while (!mDone) {
						long delay = expirationTime - System.currentTimeMillis();
						if (delay <= 0) {
							return false; // timeout
						}
						try {
							wait(delay);
						} catch (InterruptedException ex) {
						}
					}
				} else {
					while (!mDone) {
						try {
							wait();
						} catch (InterruptedException ex) {
						}
					}
				}
			}
			return true;
		}
	}
}
