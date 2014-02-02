package se.edstrompartners.net.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import se.edstrompartners.intnet14.lab1.ChatClient;

public class MainWindow extends JFrame implements ActionListener {
	private static final int width = 700;
	private static final int height = 400;
	
	private Container pane;
	
	private JTextArea jUsers;
	private JTextArea jChat;
	private JTextField jMessage;	
	ChatClient cc;
	public MainWindow(ChatClient chatClient, String windowTitle) {
		this.cc = chatClient;
		pane = getContentPane();
		
		
		jChat = new JTextArea();
		jChat.setEditable(false);
		JScrollPane jsp2 = new JScrollPane(jChat);
		
		jMessage = new JTextField();
		//jMessage.setSize(700,30);
		jMessage.addActionListener(this);
		jMessage.setActionCommand("sendMessage");
		pane.add(jsp2,BorderLayout.CENTER);
		pane.add(jMessage,BorderLayout.PAGE_END);
		
		setTitle(windowTitle);
		setSize(width,height);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	public void pushToWindow(String text){
		jChat.append(text + "\n");
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()){
		case "sendMessage":
			cc.handleInput(jMessage.getText());
			jMessage.setText("");
		}
	}
}
