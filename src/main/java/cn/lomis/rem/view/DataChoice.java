package cn.lomis.rem.view;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import cn.lomis.loop.Handler;
import cn.lomis.loop.Message;
import cn.lomis.rem.constants.WhatConstant;

/**
 * 显示数据选择组件
 * @author lomis
 *
 */
public class DataChoice extends JComboBox<String> implements ItemListener {

	private static final long serialVersionUID = 1L;
	
	Handler handler;
	
	public DataChoice(Handler handler) {
		super();
		this.setSize(200, 60);
		this.addItem("全部");
		this.addItem("脑波值");
		this.addItem("信号");
		this.addItem("关注度");
		this.addItem("放松度");
		this.addItemListener(this);
		this.handler = handler;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Message msg = new Message();
			msg.what = WhatConstant.MSG_WHAT_COMBOBOX_SELECT;
			msg.obj = getVal(e.getItem().toString());
			handler.sendMessage(msg);
		}
	}
	
	private int getVal(String item) {
		int val = 0;
		switch (item) {
		case "脑波值":
			 val = 1;
			break;
		case "信号":
			val = 2;
			break;
		case "关注度":
			val = 3;
			break;
		case "放松度":
			val = 4;
			break;

		}
		return val;
	}

}
