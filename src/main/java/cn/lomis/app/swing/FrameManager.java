package cn.lomis.app.swing;

import java.util.HashMap;
import java.util.Map;

import cn.lomis.app.swing.thread.FrameThread;

/**
 * 视图管理器
 * @author lomis
 *
 */
public class FrameManager {
	static Map<String, Frame> fs = new HashMap<>();
	
	/**
	 * 增加视图
	 * @param frame
	 */
	public static void addFrame(Frame frame) {
		String key = frame.getClass().getName();
		if (!fs.containsKey(key)) {
			fs.put(key, frame);
		}
	}
	
	/**
	 * 移除视图
	 * @param frame
	 */
	public static void removeFrame(Frame frame) {
		String key = frame.getClass().getName();
		fs.remove(key);
	}
	
	/**
	 * 移除视图
	 * @param key
	 */
	public static void removeFrame(String key) {
		fs.remove(key);
	}
	
	/**
	 * 获取视图
	 * @param key
	 * @return
	 */
	public static Frame getFrame(String key) {
		return fs.get(key);
	}

	public static Frame getFrame(FrameThread mThread, Class<?> clz) {
		String key = clz.getName();
		Frame frame = getFrame(key);
		if (frame == null) {
			try {
				frame = (Frame) clz.newInstance();
				frame.mThread = mThread;
				fs.put(key, frame);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return frame;
	}

}
