package cn.lomis.app.swing.thread;

import cn.lomis.app.swing.Frame;
import cn.lomis.app.swing.FrameManager;
import cn.lomis.loop.Handler;
import cn.lomis.loop.Intent;
import cn.lomis.loop.Message;
import cn.lomis.rem.view.MainView;

public class FrameThread {
	final H h = new H();

	public void attach(boolean b) {
		Intent intent = new Intent(null, MainView.class);
		Message msg = new Message();
		msg.what = H.START_FRAME;
		msg.obj = intent;
		h.sendMessage(msg);
		
	}

	private class H extends Handler {
		public static final int START_FRAME 	= 100;
		public static final int CLOSE_FRAME 	= 101;
		public static final int HID_FRAME 		= 102;
		public static final int DISPLAY_FRAME 	= 103;
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_FRAME:
				Intent intent = (Intent)msg.obj;
				Class<?> clz = intent.getFrameClass();
				Frame newFrame = FrameManager.getFrame(getMThread(), clz); 
				newFrame.setParent((Frame) intent.getParent());
				newFrame.setIntent(intent);
				if (newFrame.getParent() != null) {
					newFrame.getParent().goHide();
				}
				newFrame.goDisplay();
				
				break;
				
			case CLOSE_FRAME:
				Frame closeFrame = (Frame) msg.obj;
				Frame closeParent = closeFrame.getParent();
				closeFrame.goClose();
				if (closeParent != null) {
					closeFrame.getParent().goDisplay();
				}
				
				break;
				
			case HID_FRAME:
				Frame hidFrame = (Frame) msg.obj;
				Frame hidParent = hidFrame.getParent();
				hidFrame.goHide();
				if (hidParent != null) {
					hidFrame.getParent().goDisplay();
				}
				
				break;
				
			case DISPLAY_FRAME:
				Frame displayFrame = (Frame) msg.obj;
				Frame displayParent = displayFrame.getParent();
				if (displayParent != null) {
					displayFrame.getParent().goHide();
				}
				displayFrame.goDisplay();
				
				break;

			default:
				break;
			}
		}

	}
	
	private FrameThread getMThread() {
		return this;
	}

	public Handler getHandler() {
		return h;
	}

	public void sendFrameResult(Intent intent) {
		sendMessage(H.START_FRAME, intent);
		
	}
	
	private void sendMessage(int what, Object obj) {
		sendMessage(what, obj, 0, 0, false);
	}

	private void sendMessage(int what, Object obj, int arg1) {
		sendMessage(what, obj, arg1, 0, false);
	}
	
	private void sendMessage(int what, Object obj, int arg1, int arg2) {
        sendMessage(what, obj, arg1, arg2, false);
    }
	
	private void sendMessage(int what, Object obj, int arg1, int arg2, boolean async) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        if (async) {
            msg.setAsynchronous(true);
        }
        h.sendMessage(msg);
    }

}
