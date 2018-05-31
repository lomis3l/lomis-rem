package cn.lomis.rem.view.monitor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import cn.lomis.loop.Handler;
import cn.lomis.loop.Message;
import cn.lomis.rem.constants.WhatConstant;

/**
 * 波特率组件
 * @author lomis
 *
 */
public class BpsChoice extends JComboBox<String> implements ItemListener {

	private static final long serialVersionUID = 1L;
	
	Handler handler;
	
	public BpsChoice(Handler handler) {
		super();
		this.setSize(200, 60);
		this.addItem("1200");
		this.addItem("2400");
		this.addItem("4800");
		this.addItem("9600");
		this.addItem("14400");
		this.addItem("19200");
		this.addItem("38400");
		this.addItem("57600");
		this.addItem("115200");
		this.addItemListener(this);
		this.handler = handler;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Message msg = new Message();
			msg.what = WhatConstant.MSG_WHAT_CHOICE_SELECT_PORT;
			msg.obj = e.getItem().toString();
			handler.sendMessage(msg);
		}
	}

}
