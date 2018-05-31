package cn.lomis.rem.view.monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import cn.lomis.rem.utils.SerialTool;
import cn.lomis.rem.utils.SysUtil;
import cn.lomis.rem.view.DataChoice;
import cn.lomis.rem.view.DataJFreeChart;
import cn.lomis.rem.view.MainView;
import gnu.io.SerialPort;

/**
 * 数据实时监测页面
 * @author lomis
 *
 */
public class MonitorView extends Frame {
	Handler handler;
	MainView mainView;
	private SerialPort serialPort = null; 		// 保存串口对象
	private JComboBox<String> commChoice; 		// 串口选择（下拉框）
	private JComboBox<String>  bpsChoice ; 		// 波特率选择
	private JButton openSerialButton;				// 连接串口按钮
	private JButton scanSerialButton;				// 扫描设备
	DataJFreeChart dataJFreeChart;

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void onCreate() {
		handler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case WhatConstant.MSG_WHAT_CHOICE_SELECT_COMM:
					((OpenSerialButton) openSerialButton).setCommName((String)msg.obj);
					break;
				case WhatConstant.MSG_WHAT_CHOICE_SELECT_PORT:
					((OpenSerialButton) openSerialButton).setCommPort((String)msg.obj);
					break;
				case WhatConstant.MSG_WHAT_COMBOBOX_SELECT:
					int val = (int) msg.obj;
					dataJFreeChart.changeDataType(val);
					break;
				case WhatConstant.MSG_WHAT_READ_DATA:
					addVal((String) msg.obj);
					break;
				}
				return false;
			}
		});
		InitGlobalFont(new Font("微软雅黑", Font.BOLD, 20));
		this.setTitle("脑波信号监测");
		
		//设置window的icon
		this.setIconImage(SysUtil.getIcon(this));
		
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = screensize.width;
		int h = screensize.height;
		this.setBounds(w / 8, h / 10, w * 3 / 4, h * 4 / 5);
		
		// 添加组件
		addSerialComponent();
		addDataChart();
			
		this.setResizable(false);
	}
	
	/**
	 * 增加数据折线图
	 */
	private void addDataChart() {
		dataJFreeChart = new DataJFreeChart(false, 5, "脑电信号监测", "时间(Time)", "值(Val)");
		ChartPanel chartPanel = new ChartPanel(dataJFreeChart.getjFreeChart());
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		this.add(chartPanel, BorderLayout.CENTER);
		
	}

	/**
	 * 增加串口控制组件
	 */
	private void addSerialComponent() {
		JPanel jPanel = new JPanel(true);
		jPanel.setSize(650, 60);
		
		JLabel serialLabel = new JLabel(" 串口选择： ");
		serialLabel.setBounds(0, 0, 50, 60);
		jPanel.add(serialLabel);
		
		commChoice = new CommChoice(handler);
		commChoice.setBounds(new Rectangle(new Point(50, 0)));
		jPanel.add(commChoice);
		
		JLabel ratLabel = new JLabel(" 波特率： ");
		ratLabel.setBounds(300, 0, 50, 60);
		jPanel.add(ratLabel);
		
		bpsChoice = new BpsChoice(handler);
		bpsChoice.setBounds(new Rectangle(new Point(300, 0)));
		jPanel.add(bpsChoice);
		
		openSerialButton = new OpenSerialButton(handler);
		openSerialButton.setBounds(550, 0, 300, 60);
		jPanel.add(openSerialButton);
		
		scanSerialButton = new ScanSerialButton();
		scanSerialButton.setBounds(950, 0, 300, 60);
		setBackground(Color.GREEN);
		jPanel.add(scanSerialButton);
		
		JComboBox<String> dataChoice = new DataChoice(handler);
		dataChoice.setBounds(1255, 0, 200, 60);
		jPanel.add(dataChoice);
		
		this.add(jPanel, BorderLayout.SOUTH);
		
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
	
	@Override
	protected void onDestory() {
		super.onDestory();
		if (serialPort != null) {
			SerialTool.closePort(serialPort); // 程序退出时关闭串口释放资源
		}
	}
	
	public void addVal(String data) {
		List<Egg> eggs = DataUtil.readData(data, true);
		if (eggs != null && eggs.size() > 0) {
			for (int j = 0; j < eggs.size(); j++) {
				Egg egg = eggs.get(j);
				egg.setTime(egg.getTime() + j);
				this.dataJFreeChart.setVal(egg);
			}
		}
	}

}
