package cn.lomis.rem.view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import cn.lomis.app.swing.Frame;
import cn.lomis.loop.Intent;
import cn.lomis.rem.utils.SysUtil;
import cn.lomis.rem.view.monitor.MonitorView;
import cn.lomis.rem.view.show.ShowView;

/**
 * 主页面
 * @author Lomis
 *
 */

public class MainView extends Frame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void onCreate() {
		System.out.println("main view:" + Thread.currentThread().getId());
		SysUtil.InitGlobalFont();
		this.setTitle("脑波监测系统1.0,  By:Lomis");
		
		//设置window的icon
		this.setIconImage(SysUtil.getIcon(this));
		
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = screensize.width;
		int h = screensize.height;
		this.setBounds(new Rectangle(new Point(w / 2 - 250, h / 2 - 200)));
		this.setSize(500, 400);
		
		JLabel sysLabel = new JLabel("欢迎使用脑波监测系统");
		sysLabel.setBounds(150, 10, 300, 60);
		this.add(sysLabel);
		
		JButton monitorButton = new JButton("监测");
		monitorButton.addActionListener(this);
		monitorButton.setBounds(100, 150, 100, 100);
		this.add(monitorButton);
		
		JButton viewButton = new JButton("回放");
		viewButton.addActionListener(this);
		viewButton.setBounds(300, 150, 100, 100);
		this.add(viewButton);
		
		this.setLayout(null);
		this.setResizable(false);	
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("MainView.actionPerformed:" + Thread.currentThread().getId());
		this.dispose();
		String command = e.getActionCommand();
		Intent intent = new Intent(this, null);
		if ("监测".equals(command)) {
			intent.setFrameClass(MonitorView.class);
		} else if ("回放".equals(command)) {
			intent.setFrameClass(ShowView.class);
		}
		startFrame(intent);
	}


}
