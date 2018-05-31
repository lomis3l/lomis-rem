package cn.lomis.loop;

public final class Message {
	public int what;
	public int arg1;
	public int arg2;
	public Object obj;

	static final int FLAG_IN_USE = 1 << 0;
	static final int FLAG_ASYNCHRONOUS = 1 << 1;
	static final int FLAGS_TO_CLEAR_ON_COPY_FROM = FLAG_IN_USE;

	int flags;
	long when;
	Handler target;
	Runnable callback;
	Message next;

	private static final Object sPoolSync = new Object();
	private static Message sPool;
	private static int sPoolSize = 0;

	private static final int MAX_POOL_SIZE = 50;

	private static boolean gCheckRecycle = true;

	/**
	 * 从全局池返回一个新的Message实例。允许我们在许多情况下避免分配新对象。
	 */
	public static Message obtain() {
		synchronized (sPoolSync) {
			if (sPool != null) {
				Message m = sPool;
				sPool = m.next;
				m.next = null;
				m.flags = 0; // clear in-use flag
				sPoolSize--;
				return m;
			}
		}
		return new Message();
	}

	public static Message obtain(Handler h) {
		Message m = obtain();
		m.target = h;

		return m;
	}

	public static Message obtain(Handler h, Runnable callback) {
		Message m = obtain();
		m.target = h;
		m.callback = callback;

		return m;
	}

	public static Message obtain(Handler h, int what) {
		Message m = obtain();
		m.target = h;
		m.what = what;

		return m;
	}

	public static Message obtain(Handler h, int what, Object obj) {
		Message m = obtain();
		m.target = h;
		m.what = what;
		m.obj = obj;

		return m;
	}

	public static Message obtain(Handler h, int what, int arg1, int arg2) {
		Message m = obtain();
		m.target = h;
		m.what = what;
		m.arg1 = arg1;
		m.arg2 = arg2;

		return m;
	}

	public static Message obtain(Handler h, int what, int arg1, int arg2, Object obj) {
		Message m = obtain();
		m.target = h;
		m.what = what;
		m.arg1 = arg1;
		m.arg2 = arg2;
		m.obj = obj;

		return m;
	}

	/**
	 * 回收释放
	 */
	public void recycle() {
		if (isInUse()) {
			if (gCheckRecycle) {
				throw new IllegalStateException("This message cannot be recycled because it " + "is still in use.");
			}
			return;
		}
		recycleUnchecked();
	}

	/**
	 * 回收正在使用的消息.
	 */
	void recycleUnchecked() {
		// 在回收的对象池中标记该消息，并将其标记为使用。清除所有其他的细节。
		flags = FLAG_IN_USE;
		what = 0;
		arg1 = 0;
		arg2 = 0;
		obj = null;
		when = 0;
		target = null;
		callback = null;

		synchronized (sPoolSync) {
			if (sPoolSize < MAX_POOL_SIZE) {
				next = sPool;
				sPool = this;
				sPoolSize++;
			}
		}
	}

	public long getWhen() {
		return when;
	}

	public void setTarget(Handler target) {
		this.target = target;
	}

	public Handler getTarget() {
		return target;
	}

	public Runnable getCallback() {
		return callback;
	}

	public void sendToTarget() {
		target.sendMessage(this);
	}

	public boolean isAsynchronous() {
		return (flags & FLAG_ASYNCHRONOUS) != 0;
	}

	public void setAsynchronous(boolean async) {
		if (async) {
			flags |= FLAG_ASYNCHRONOUS;
		} else {
			flags &= ~FLAG_ASYNCHRONOUS;
		}
	}

	boolean isInUse() {
		return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
	}

	void markInUse() {
		flags |= FLAG_IN_USE;
	}

	public Message() {
	}

}
