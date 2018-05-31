package cn.lomis.rem.view.show;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import cn.lomis.rem.constants.WhatConstant;
import cn.lomis.rem.exception.NoSuchPort;
import cn.lomis.rem.exception.NotASerialPort;
import cn.lomis.rem.exception.PortInUse;
import cn.lomis.rem.exception.SerialPortParameterFailure;
import cn.lomis.rem.exception.TooManyListeners;
import cn.lomis.rem.listener.SerialListener;
import cn.lomis.rem.utils.SerialTool;
import cn.lomis.loop.Handler;
import cn.lomis.loop.Message;
import gnu.io.SerialPort;

/**
 * 打开串口的按钮
 * @author lomis
 *
 */
public class SelectFileButton extends JButton implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	Handler handler;
	
	private String commName;
	private String commPort;
	private SerialPort serialPort;

	public SelectFileButton(Handler handler) {
		super();
		this.setText("选择");
		this.addActionListener(this);
		this.handler = handler;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("选择".equals(command)) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(0);
			int status = chooser.showOpenDialog(null);
			if (status == 1) {
				return;
			} else {
				File file = chooser.getSelectedFile();			// 读取选择器选择到的文件
				String filePath = file.getAbsolutePath();
				Message msg = new Message();
				msg.what = WhatConstant.MSG_WHAT_BUTTON_SELECT_FILE;
				msg.obj = filePath;
				handler.sendMessage(msg);
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
