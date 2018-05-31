package cn.lomis.rem.view.monitor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.slf4j.impl.StaticLoggerBinder;

import cn.lomis.rem.exception.NoSuchPort;
import cn.lomis.rem.exception.NotASerialPort;
import cn.lomis.rem.exception.PortInUse;
import cn.lomis.rem.exception.SerialPortParameterFailure;
import cn.lomis.rem.exception.TooManyListeners;
import cn.lomis.rem.listener.SerialListener;
import cn.lomis.rem.utils.DataUtil;
import cn.lomis.rem.utils.SerialTool;
import cn.lomis.loop.Handler;
import gnu.io.SerialPort;

/**
 * 打开串口的按钮
 * @author lomis
 *
 */
public class OpenSerialButton extends JButton implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	Handler handler;
	
	private String commName;
	private String commPort;
	private SerialPort serialPort;

	public OpenSerialButton(Handler handler) {
		super();
		this.handler = handler;
		this.setText("打开");
		this.setBackground(Color.GREEN);
		this.setFont(new Font("微软雅黑", Font.BOLD, 20));
		this.setForeground(Color.darkGray);
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("打开".equals(command)) {
			this.setBackground(Color.RED);
			this.setText("关闭");
			// 检查串口名称是否获取正确
			if (!(this.commName == null || commName.trim().length() == 0)) {
				if (!(commPort == null || commPort.trim().length() == 0)) { // 检查波特率是否获取正确
					int bps = Integer.parseInt(commPort);
					try {
						serialPort = SerialTool.openPort(commName.toString(), bps);	// 获取指定端口名及波特率的串口对象
						SerialTool.addListener(serialPort, new SerialListener(handler));		// 在该串口对象上添加监听器
						// 监听成功进行提示
						StaticLoggerBinder.getSingleton().reset();
						DataUtil.setLog();
						JOptionPane.showMessageDialog(null, "监听成功，稍后将显示监测数据！", "提示", JOptionPane.INFORMATION_MESSAGE);
					} catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e1) {
						// 发生错误时使用一个Dialog提示具体的错误信息
						JOptionPane.showMessageDialog(null, e1, "错误", JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "波特率获取错误！", "错误", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "没有搜索到有效串口！", "错误", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if ("关闭".equals(command)) {
			this.setBackground(Color.GREEN);
			this.setText("打开");
			SerialTool.closePort(serialPort);
			String absPath = this.getClass().getClassLoader().getResource("").getPath();
			absPath = absPath.substring(1, absPath.lastIndexOf("/"));
			/*absPath = absPath.substring(0, absPath.lastIndexOf("/"));
			absPath = absPath.substring(0, absPath.lastIndexOf("/"));*/
			String fileName = StaticLoggerBinder.getSingleton().getFileName();
			File logFile = new File(absPath + fileName.substring(1));
			if (logFile.exists()) {
				try {
					String newFile = absPath + fileName.substring(1).replaceAll("logs", "data").replaceAll("\\.log", "_" + System.currentTimeMillis() + "\\.data");
					FileUtils.copyFile(logFile, new File(newFile));
					FileUtils.writeStringToFile(logFile, null);
					JOptionPane.showMessageDialog(null, "监测结束, 监测数据保存在：" + newFile, "提示", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}

	public String getCommName() {
		return commName;
	}

	public void setCommName(String commName) {
		this.commName = commName;
	}

	public String getCommPort() {
		return commPort;
	}

	public void setCommPort(String commPort) {
		this.commPort = commPort;
	}

	public SerialPort getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}
	
}
