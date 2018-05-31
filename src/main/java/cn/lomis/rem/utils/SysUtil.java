package cn.lomis.rem.utils;

import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class SysUtil {

	/**
	 * 设置全局字体
	 * @param font
	 */
	public static void InitGlobalFont() {
		Font font = new Font("微软雅黑", Font.BOLD, 20);
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}

	public static Image getIcon(JFrame jFrame) {
		Toolkit toolKit = jFrame.getToolkit();
		Image icon = toolKit.getImage(SysUtil.class.getResource("icon.png"));
		return icon;
	}
}
