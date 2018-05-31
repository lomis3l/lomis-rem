package cn.lomis.rem.view.show;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;

import cn.lomis.loop.Handler;
import cn.lomis.loop.Message;
import cn.lomis.rem.constants.WhatConstant;
import gnu.io.SerialPort;

/**
 * 打开文件的按钮
 * @author lomis
 *
 */
public class OpenFileButton extends JButton implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	Handler handler;
	
	private String commName;
	private String commPort;
	private SerialPort serialPort;
	JTextField fileTextField;

	public OpenFileButton(Handler handler, JTextField fileTextField) {
		super();
		this.handler = handler;
		this.fileTextField = fileTextField;
		this.setText("确定");
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String context = e.getActionCommand();
		if ("确定".equals(context)) {
			String filePath = fileTextField.getText();
			try {
				@SuppressWarnings("unchecked")
				List<String> list = FileUtils.readLines(new File(filePath), "utf-8");
				if (list != null && list.size() > 0) {
					Message msg = new Message();
					msg.what = WhatConstant.MSG_WHAT_BUTTON_OPEN_FILE;
					msg.obj = list;
					handler.sendMessage(msg);
				} else {
					JOptionPane.showMessageDialog(null, null, "文件错误", JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, e1, "错误", JOptionPane.INFORMATION_MESSAGE);
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
