package cn.lomis.rem;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import cn.lomis.app.swing.thread.FrameThread;
import cn.lomis.loop.Handler;
import cn.lomis.loop.Looper;

public class Main {
//	final static Logger logger = LoggerFactory.getLogger(Main.class);
	static volatile Handler sMainThreadHandler;  // set once in main()
	
//	static Looper looper;
	public static void main(String[] args) {
		Looper.prepareMainLooper();
		
		FrameThread mainThread = new FrameThread();
		mainThread.attach(false);
		
		if (sMainThreadHandler == null) {
			sMainThreadHandler = mainThread.getHandler();
		}
		Looper.loop();
		System.exit(0);
	}

}
