package cn.lomis.loop;

import cn.lomis.app.swing.FrameManager;

public final class Looper {

	// sThreadLocal.get() 会返回null，除非你调用 prepare().
	static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
	private static Looper sMainLooper; // guarded by Looper.class
	static FrameManager manager;

	final MessageQueue mQueue;
	final Thread mThread;

	public static void prepare() {
		prepare(true);
	}

	private static void prepare(boolean quitAllowed) {
		if (sThreadLocal.get() != null) {
			throw new RuntimeException("Only one Looper may be created per thread");
		}
		sThreadLocal.set(new Looper(quitAllowed));
	}

	public static void prepareMainLooper() {
		System.out.println(Thread.currentThread().getId());
		prepare(false);
		synchronized (Looper.class) {
			if (sMainLooper != null) {
				throw new IllegalStateException("The main Looper has already been prepared.");
			}
			sMainLooper = myLooper();
			manager = new FrameManager();
		}
	}

	public static Looper getMainLooper() {
		synchronized (Looper.class) {
			return sMainLooper;
		}
	}

	public static void loop() {
		final Looper me = myLooper();
		if (me == null) {
			throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
		}
		final MessageQueue queue = me.mQueue;
		for (;;) {
			Message msg = queue.next(); // might block
			if (msg != null) {
				// No message indicates that the message queue is quitting.
//				continue;
				msg.target.dispatchMessage(msg);
				msg.recycleUnchecked();
			}

		}
	}

	public static Looper myLooper() {
		System.out.println(Thread.currentThread().getId());
		return sThreadLocal.get();
	}

	public static MessageQueue myQueue() {
		return myLooper().mQueue;
	}

	private Looper(boolean quitAllowed) {
		mQueue = new MessageQueue(quitAllowed);
		mThread = Thread.currentThread();
	}

	public boolean isCurrentThread() {
		return Thread.currentThread() == mThread;
	}

	public void quit() {
		mQueue.quit(false);
	}

	public void quitSafely() {
		mQueue.quit(true);
	}

	public Thread getThread() {
		return mThread;
	}

	public MessageQueue getQueue() {
		return mQueue;
	}
}
