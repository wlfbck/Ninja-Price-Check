package wlfbck;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SpringLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PriceNotification extends JFrame {

	private JPanel contentPane;
	
	private int width = 258;
	private int height = 96;
	private int taskbarPadding = 60;

	/**
	 * Create the frame.
	 */
	public PriceNotification(LinkedList<Double> price, Item item) {
		setUndecorated(true);
		setType(Type.POPUP);
		setAlwaysOnTop(true);
		setAutoRequestFocus(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 10, 10));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int)(screenSize.getWidth()-width), (int)(screenSize.getHeight()-height-taskbarPadding), width, height);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0,0,0));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JLabel lblItemAndPrice = new JLabel("price");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblItemAndPrice, 28, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblItemAndPrice, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblItemAndPrice, height, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblItemAndPrice, width, SpringLayout.WEST, contentPane);
		lblItemAndPrice.setHorizontalAlignment(SwingConstants.CENTER);
		lblItemAndPrice.setFont(new Font(lblItemAndPrice.getFont().getName(), Font.BOLD, 16));
		contentPane.add(lblItemAndPrice);
		
		JPanel panel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, panel, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, panel, 0, SpringLayout.NORTH, lblItemAndPrice);
		sl_contentPane.putConstraint(SpringLayout.EAST, panel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JButton btnClose = new JButton("X");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(btnClose, BorderLayout.EAST);
		
		JLabel lblHeader = new JLabel("Current price(s) for...");
		lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblHeader, BorderLayout.CENTER);
		
		//setting the content
		lblItemAndPrice.setText("<html>"+item.name + ": <br>");
		if(price.size() > 1) {
			for(double d : price) {
				lblItemAndPrice.setText(lblItemAndPrice.getText()+d+"c/");
			}
		} else {
			lblItemAndPrice.setText(lblItemAndPrice.getText()+price + "c");
		}
		lblItemAndPrice.setText(lblItemAndPrice.getText()+"</html>");
	}
}
