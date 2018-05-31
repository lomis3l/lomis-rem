package cn.lomis.app.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import cn.lomis.app.swing.thread.FrameThread;
import cn.lomis.loop.Handler;
import cn.lomis.loop.Intent;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private Intent intent;
	private Frame parent;
	private Thread uiThread;
	FrameThread mThread;
	
	final Handler mHandler = new Handler();
	
	public Frame() {
		onCreate();
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				goClose();
				if (parent != null) {
					parent.goDisplay();
				}
			}
		});
	}
	
	/**
	 * Frame创建时调用
	 */
	protected void onCreate(){}
	
	/**
	 * 展示前调用
	 */
	protected void onDisplayBefore(){}
	
	/**
	 * 展示后调用
	 */
	protected void onDisplayAfter(){}
	
	/**
	 * 销毁时调用
	 */
	protected void onDestory(){}
	
	/**
	 * 隐藏前调用
	 */
	public void onPause (){}
	
	///////////////////////////////////////////////////////////////
	
	public void goClose() {
		onDisplayAfter();
		this.setVisible(true);
		FrameManager.removeFrame(this);
		this.dispose();
	}

	/**
	 * 展示
	 */
	public void goDisplay() {
		onDisplayBefore();
		this.setVisible(true);
		onDisplayAfter();
	}
	
	/**
	 * 隐藏
	 */
	public void goHide() {
		onPause();
		this.setVisible(false);
	}
	
	public void startFrame(Intent intent) {
		/*this.goHide();
		newFrame.goDisplay();*/
		mThread.sendFrameResult(intent);
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public Frame getParent() {
		return parent;
	}

	public void setParent(Frame parent) {
		this.parent = parent;
	}
	
	public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != uiThread) {
            mHandler.post(action);
        } else {
            action.run();
        }
    }
	
	final void attach(FrameThread fThread, Intent intent, Frame parent) {
		this.uiThread = Thread.currentThread();
		this.mThread = fThread;
		this.intent = intent;
	}
	
}
