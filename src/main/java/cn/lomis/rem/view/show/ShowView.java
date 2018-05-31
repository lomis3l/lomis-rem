package cn.lomis.rem.view.show;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.jfree.chart.ChartPanel;

import cn.lomis.app.swing.Frame;
import cn.lomis.loop.Handler;
import cn.lomis.loop.Handler.Callback;
import cn.lomis.loop.Message;
import cn.lomis.rem.bo.Egg;
import cn.lomis.rem.constants.WhatConstant;
import cn.lomis.rem.utils.DataUtil;
import cn.lomis.rem.utils.SysUtil;
import cn.lomis.rem.view.DataChoice;
import cn.lomis.rem.view.DataJFreeChart;

/**
 * 数据实时监测页面
 * @author lomis
 *
 */
public class ShowView extends Frame {
	private static final long serialVersionUID = 1L;
	
	JTextField fileTextField; 			// 存放文件路径
	DataJFreeChart dataJFreeChart; 	// 数据折线图
	Handler handler;

	@Override
	protected void onCreate() {
		handler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case WhatConstant.MSG_WHAT_BUTTON_SELECT_FILE:
					fileTextField.setText((String)msg.obj);
					break;
					
				case WhatConstant.MSG_WHAT_BUTTON_OPEN_FILE:
					List<String> list = (List<String>) msg.obj;
					addVal(list);
					break;
				case WhatConstant.MSG_WHAT_COMBOBOX_SELECT:
					int val = (int) msg.obj;
					dataJFreeChart.changeDataType(val);
					break;
				}
				return false;
			}
		});
		InitGlobalFont(new Font("微软雅黑", Font.BOLD, 20));
		this.setTitle("脑波信号回播");
		
		//设置window的icon
		this.setIconImage(SysUtil.getIcon(this));
		
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = screensize.width;
		int h = screensize.height;
		this.setBounds(w / 8, h / 10, w * 3 / 4, h * 4 / 5);
		
		// 添加组件
		addFileComponent();
		addDataChart();
		
		this.setResizable(false);
	}
	
	/**
	 * 增加数据折线图
	 */
	private void addDataChart() {
		dataJFreeChart = new DataJFreeChart(true, 5, null, "时间(Time)", "值(Val)");
		ChartPanel chartPanel = new ChartPanel(dataJFreeChart.getjFreeChart());
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		this.add(chartPanel, BorderLayout.CENTER);
	}

	/**
	 * 增加串口控制组件
	 */
	private void addFileComponent() {
		JPanel jPanel = new JPanel();
		jPanel.setSize(600, 64);
		jPanel.setLayout(new FlowLayout());
		
		JLabel fileLabel = new JLabel(" 文件：");
		fileLabel.setBounds(0, 0, 50, 60);
		jPanel.add(fileLabel);
		
		fileTextField = new JTextField(30);
		fileTextField.setEditable(false);
		fileTextField.setBounds(50, 0, 400, 60);
		jPanel.add(fileTextField);
		
		JButton fileButton = new SelectFileButton(handler);
		fileButton.setBounds(450, 0, 50, 60);
		jPanel.add(fileButton);
		
		JButton comfirmButton = new OpenFileButton(handler, fileTextField);
		comfirmButton.setBounds(550, 0, 50, 60);
		jPanel.add(comfirmButton);
		
		JComboBox<String> dataChoice = new DataChoice(handler);
		dataChoice.setBounds(605, 0, 200, 60);
		jPanel.add(dataChoice);
		
		this.add(jPanel, BorderLayout.NORTH);
	}

	private void InitGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}
	
	public void addVal(List<String> list) {
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				List<Egg> eggs = DataUtil.readData(list.get(i), false);
				if (eggs != null && eggs.size() > 0) {
					for (int j = 0; j < eggs.size(); j++) {
						Egg egg = eggs.get(j);
						egg.setTime(egg.getTime() + j);
						this.dataJFreeChart.setVal(egg);
					}
				}
			}
		}
	}

}
