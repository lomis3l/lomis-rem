package cn.lomis.rem.view.monitor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;

import cn.lomis.loop.Handler;
import cn.lomis.loop.Message;
import cn.lomis.rem.constants.WhatConstant;
import cn.lomis.rem.utils.SerialTool;

/**
 * 串口选择组件
 * @author lomis
 *
 */
public class CommChoice extends JComboBox<String> implements ItemListener {
	
	Handler handler;

	private static final long serialVersionUID = 1L;
	
	private List<String> commList;
	
	public CommChoice(Handler handler) {
		super();
		this.setSize(200, 60);
		this.addItem("请选择");
		//检查是否有可用串口，有则加入选项中
		commList = SerialTool.findPort();
		if (commList != null) {
			for (String s : commList) {
				this.addItem(s);
			}
		}
		this.addItemListener(this);
		this.handler = handler;
	}

	public List<String> getCommList() {
		return commList;
	}

	public void setCommList(List<String> commList) {
		this.commList = commList;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Message msg = new Message();
			msg.what = WhatConstant.MSG_WHAT_CHOICE_SELECT_COMM;
			msg.obj = e.getItem().toString();
			handler.sendMessage(msg);
		}
	}
	
}
