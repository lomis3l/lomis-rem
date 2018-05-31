package cn.lomis.loop;

import java.util.ArrayList;

public final class MessageQueue {
	private final boolean mQuitAllowed;

	private long mPtr; // used by native code

	Message mMessages;
	private final ArrayList<IdleHandler> mIdleHandlers = new ArrayList<IdleHandler>();
	private IdleHandler[] mPendingIdleHandlers;
	private boolean mQuitting;

	private boolean mBlocked;

	private int mNextBarrierToken;

	MessageQueue(boolean quitAllowed) {
		mQuitAllowed = quitAllowed;
		mPtr = 1;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			dispose();
		} finally {
			super.finalize();
		}
	}

	private void dispose() {
		if (mPtr != 0) {
			mPtr = 0;
		}
	}

	public boolean isIdle() {
		synchronized (this) {
			final long now = System.currentTimeMillis();
			return mMessages == null || now < mMessages.when;
		}
	}

	public void addIdleHandler(IdleHandler handler) {
		if (handler == null) {
			throw new NullPointerException("Can't add a null IdleHandler");
		}
		synchronized (this) {
			mIdleHandlers.add(handler);
		}
	}

	public void removeIdleHandler(IdleHandler handler) {
		synchronized (this) {
			mIdleHandlers.remove(handler);
		}
	}

	Message next() {
		final long ptr = mPtr;
		if (ptr == 0) {
			return null;
		}

		int pendingIdleHandlerCount = -1; // -1 only during first iteration
		int nextPollTimeoutMillis = 0;
		for (;;) {

			synchronized (this) {
				// Try to retrieve the next message. Return if found.
				final long now = System.currentTimeMillis();
				Message prevMsg = null;
				Message msg = mMessages;
				if (msg != null && msg.target == null) {
					// Stalled by a barrier. Find the next asynchronous message in the queue.
					do {
						prevMsg = msg;
						msg = msg.next;
					} while (msg != null && !msg.isAsynchronous());
				}
				if (msg != null) {
					if (now < msg.when) {
						// Next message is not ready. Set a timeout to wake up when it is ready.
						nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
					} else {
						// Got a message.
						mBlocked = false;
						if (prevMsg != null) {
							prevMsg.next = msg.next;
						} else {
							mMessages = msg.next;
						}
						msg.next = null;
						msg.markInUse();
						return msg;
					}
				} else {
					// No more messages.
					nextPollTimeoutMillis = -1;
				}

				if (mQuitting) {
					dispose();
					return null;
				}

				if (pendingIdleHandlerCount < 0 && (mMessages == null || now < mMessages.when)) {
					pendingIdleHandlerCount = mIdleHandlers.size();
				}
				if (pendingIdleHandlerCount <= 0) {
					// No idle handlers to run. Loop and wait some more.
					mBlocked = true;
					continue;
				}

				if (mPendingIdleHandlers == null) {
					mPendingIdleHandlers = new IdleHandler[Math.max(pendingIdleHandlerCount, 4)];
				}
				mPendingIdleHandlers = mIdleHandlers.toArray(mPendingIdleHandlers);
			}

			for (int i = 0; i < pendingIdleHandlerCount; i++) {
				final IdleHandler idler = mPendingIdleHandlers[i];
				mPendingIdleHandlers[i] = null; // release the reference to the handler

				boolean keep = false;
				try {
					keep = idler.queueIdle();
				} catch (Throwable t) {
				}

				if (!keep) {
					synchronized (this) {
						mIdleHandlers.remove(idler);
					}
				}
			}

			pendingIdleHandlerCount = 0;

			nextPollTimeoutMillis = 0;
		}
	}

	void quit(boolean safe) {
		if (!mQuitAllowed) {
			throw new IllegalStateException("Main thread not allowed to quit.");
		}

		synchronized (this) {
			if (mQuitting) {
				return;
			}
			mQuitting = true;

			if (safe) {
				removeAllFutureMessagesLocked();
			} else {
				removeAllMessagesLocked();
			}

		}
	}

	private int postSyncBarrier(long when) {
		synchronized (this) {
			final int token = mNextBarrierToken++;
			final Message msg = Message.obtain();
			msg.markInUse();
			msg.when = when;
			msg.arg1 = token;

			Message prev = null;
			Message p = mMessages;
			if (when != 0) {
				while (p != null && p.when <= when) {
					prev = p;
					p = p.next;
				}
			}
			if (prev != null) { // invariant: p == prev.next
				msg.next = p;
				prev.next = msg;
			} else {
				msg.next = p;
				mMessages = msg;
			}
			return token;
		}
	}

	public void removeSyncBarrier(int token) {
		synchronized (this) {
			Message prev = null;
			Message p = mMessages;
			while (p != null && (p.target != null || p.arg1 != token)) {
				prev = p;
				p = p.next;
			}
			if (p == null) {
				throw new IllegalStateException("The specified message queue synchronization "
						+ " barrier token has not been posted or has already been removed.");
			}
			final boolean needWake;
			if (prev != null) {
				prev.next = p.next;
				needWake = false;
			} else {
				mMessages = p.next;
				needWake = mMessages == null || mMessages.target != null;
			}
			p.recycleUnchecked();

		}
	}

	boolean enqueueMessage(Message msg, long when) {
		if (msg.target == null) {
			throw new IllegalArgumentException("Message must have a target.");
		}
		if (msg.isInUse()) {
			throw new IllegalStateException(msg + " This message is already in use.");
		}

		synchronized (this) {
			if (mQuitting) {
				msg.recycle();
				return false;
			}

			msg.markInUse();
			msg.when = when;
			Message p = mMessages;
			boolean needWake;
			if (p == null || when == 0 || when < p.when) {
				// New head, wake up the event queue if blocked.
				msg.next = p;
				mMessages = msg;
				needWake = mBlocked;
			} else {
				// Inserted within the middle of the queue. Usually we don't have to wake
				// up the event queue unless there is a barrier at the head of the queue
				// and the message is the earliest asynchronous message in the queue.
				needWake = mBlocked && p.target == null && msg.isAsynchronous();
				Message prev;
				for (;;) {
					prev = p;
					p = p.next;
					if (p == null || when < p.when) {
						break;
					}
					if (needWake && p.isAsynchronous()) {
						needWake = false;
					}
				}
				msg.next = p; // invariant: p == prev.next
				prev.next = msg;
			}

		}
		return true;
	}

	boolean hasMessages(Handler h, int what, Object object) {
		if (h == null) {
			return false;
		}

		synchronized (this) {
			Message p = mMessages;
			while (p != null) {
				if (p.target == h && p.what == what && (object == null || p.obj == object)) {
					return true;
				}
				p = p.next;
			}
			return false;
		}
	}

	boolean hasMessages(Handler h, Runnable r, Object object) {
		if (h == null) {
			return false;
		}

		synchronized (this) {
			Message p = mMessages;
			while (p != null) {
				if (p.target == h && p.callback == r && (object == null || p.obj == object)) {
					return true;
				}
				p = p.next;
			}
			return false;
		}
	}

	void removeMessages(Handler h, int what, Object object) {
		if (h == null) {
			return;
		}

		synchronized (this) {
			Message p = mMessages;

			// Remove all messages at front.
			while (p != null && p.target == h && p.what == what && (object == null || p.obj == object)) {
				Message n = p.next;
				mMessages = n;
				p.recycleUnchecked();
				p = n;
			}

			// Remove all messages after front.
			while (p != null) {
				Message n = p.next;
				if (n != null) {
					if (n.target == h && n.what == what && (object == null || n.obj == object)) {
						Message nn = n.next;
						n.recycleUnchecked();
						p.next = nn;
						continue;
					}
				}
				p = n;
			}
		}
	}

	void removeMessages(Handler h, Runnable r, Object object) {
		if (h == null || r == null) {
			return;
		}

		synchronized (this) {
			Message p = mMessages;

			// Remove all messages at front.
			while (p != null && p.target == h && p.callback == r && (object == null || p.obj == object)) {
				Message n = p.next;
				mMessages = n;
				p.recycleUnchecked();
				p = n;
			}

			// Remove all messages after front.
			while (p != null) {
				Message n = p.next;
				if (n != null) {
					if (n.target == h && n.callback == r && (object == null || n.obj == object)) {
						Message nn = n.next;
						n.recycleUnchecked();
						p.next = nn;
						continue;
					}
				}
				p = n;
			}
		}
	}

	void removeCallbacksAndMessages(Handler h, Object object) {
		if (h == null) {
			return;
		}

		synchronized (this) {
			Message p = mMessages;

			// Remove all messages at front.
			while (p != null && p.target == h && (object == null || p.obj == object)) {
				Message n = p.next;
				mMessages = n;
				p.recycleUnchecked();
				p = n;
			}

			// Remove all messages after front.
			while (p != null) {
				Message n = p.next;
				if (n != null) {
					if (n.target == h && (object == null || n.obj == object)) {
						Message nn = n.next;
						n.recycleUnchecked();
						p.next = nn;
						continue;
					}
				}
				p = n;
			}
		}
	}

	private void removeAllMessagesLocked() {
		Message p = mMessages;
		while (p != null) {
			Message n = p.next;
			p.recycleUnchecked();
			p = n;
		}
		mMessages = null;
	}

	private void removeAllFutureMessagesLocked() {
		final long now = System.currentTimeMillis();
		Message p = mMessages;
		if (p != null) {
			if (p.when > now) {
				removeAllMessagesLocked();
			} else {
				Message n;
				for (;;) {
					n = p.next;
					if (n == null) {
						return;
					}
					if (n.when > now) {
						break;
					}
					p = n;
				}
				p.next = null;
				do {
					p = n;
					n = p.next;
					p.recycleUnchecked();
				} while (n != null);
			}
		}
	}

	public static interface IdleHandler {
		boolean queueIdle();
	}

	public interface OnFileDescriptorEventListener {
		public static final int EVENT_INPUT = 1 << 0;
		public static final int EVENT_OUTPUT = 1 << 1;
		public static final int EVENT_ERROR = 1 << 2;

	}
}
