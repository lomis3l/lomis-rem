package cn.lomis.rem.listener;

import javax.swing.JOptionPane;

import cn.lomis.loop.Handler;
import cn.lomis.loop.Message;
import cn.lomis.rem.constants.WhatConstant;
import cn.lomis.rem.exception.ReadDataFromSerialPortFailure;
import cn.lomis.rem.exception.SerialPortInputStreamCloseFailure;
import cn.lomis.rem.utils.SerialTool;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialListener implements SerialPortEventListener {

	private SerialPort serialPort = null; // 保存串口对象
	Handler handler;
	
	public SerialListener(Handler handler) {
		this.handler = handler;
	}

	/**
	 * 处理监控到的串口事件
	 */
	public void serialEvent(SerialPortEvent serialPortEvent) {
		switch (serialPortEvent.getEventType()) {
		case SerialPortEvent.BI: // 10 通讯中断
			JOptionPane.showMessageDialog(null, "与串口设备通讯中断", "错误", JOptionPane.INFORMATION_MESSAGE);
			break;
		case SerialPortEvent.OE: 	// 7 溢位（溢出）错误
		case SerialPortEvent.FE: 	// 9 帧错误
		case SerialPortEvent.PE: 	// 8 奇偶校验错误
		case SerialPortEvent.CD: 	// 6 载波检测
		case SerialPortEvent.CTS: 	// 3 清除待发送数据
		case SerialPortEvent.DSR: 	// 4 待发送数据准备好了
		case SerialPortEvent.RI: 	// 5 振铃指示
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
			break;
		case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
			byte[] data = null;
			try {
				if (serialPort == null) {
					JOptionPane.showMessageDialog(null, "串口对象为空！监听失败！", "错误", JOptionPane.INFORMATION_MESSAGE);
				} else {
					data = SerialTool.readFromPort(serialPort); // 读取数据，存入字节数组
					if (data != null && data.length > 0) {
						StringBuilder stringBuilder = new StringBuilder(data.length);
		                for(byte byteChar : data) {
		                	stringBuilder.append(String.format("%02X ", byteChar));
		                }
		                Message msg = new Message();
		                msg.what = WhatConstant.MSG_WHAT_READ_DATA;
		                msg.obj = stringBuilder.toString();
		                handler.sendMessage(msg);
					}
					
				}

			} catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
				JOptionPane.showMessageDialog(null, e, "错误", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0); // 发生读取错误时显示错误信息后退出系统
			}
			break;

		}

	}
	
	public void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

}
