package cn.lomis.rem.view.monitor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * 扫描设备的按钮
 * @author lomis
 *
 */
public class ScanSerialButton extends JButton implements ActionListener {

	private static final long serialVersionUID = 1L;

	public ScanSerialButton() {
		super();
		this.addActionListener(this);
		this.setText("扫描");
		this.setBackground(Color.GREEN);
		this.setFont(new Font("微软雅黑", Font.BOLD, 20));
		this.setForeground(Color.darkGray);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Scan Serial butto on click");
		
	}
	
}
