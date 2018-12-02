package java12;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class GameJava12 extends Applet 
implements ActionListener, Runnable
{
	Socket mySocket =null;
	PrintWriter out =null;
	BufferedReader in =null;
	
	Thread clock;
	TextArea memo;
	Label label;
	TextField list;
	TextField name;
	TextField input;
	Panel NorthPanel;
	Panel myPanel;
	
	
	public void init() {
		try {
			setSize(600,300);
			mySocket = new Socket("192.168.219.200",2486);
			out = new PrintWriter(new OutputStreamWriter(mySocket.getOutputStream(),"KSC5601"), true);
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream(),"KSC5601"),1024);
		}catch(UnknownHostException e) {
			System.out.println(e.toString());
		}catch(IOException e) {
			System.out.println(e.toString());
		}
		
		//GUI
		setLayout(new BorderLayout());
		label = new Label("접속 명단");
		label.setBackground(Color.GRAY);
		label.setAlignment(Label.CENTER);
		list = new TextField(60);
		NorthPanel = new Panel();
		NorthPanel.add(label);
		NorthPanel.add(list);
		add("North",NorthPanel);
		memo = new TextArea(10,55);
		add("Center",memo);
		myPanel =  new Panel();
		name =new TextField(8);
		name.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == name) {
					String data = name.getText();
					out.println("NAME|"+data);
					out.flush();
				}
			}
		});
//		name.setText("대화명");
		myPanel.add(name);
		input = new TextField(40);
		input.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == input) {
					String data = input.getText();
					input.setText("");
								
					out.println("TALK|"+data); //추가
					out.flush();
				}
			}
		});
		myPanel.add(input);
		add("South", myPanel);
		}
	
	public void start() {
		if(clock==null) {
			clock=new Thread(this);
			clock.start();
		}
	}
	public void run() {
		out.println("LOGIN|" +mySocket);
		memo.append("접속 " + getCodeBase().toString() + "\n");
		
		try {
			while(true) {
				String msg = in.readLine();

				StringTokenizer st =null;//추가	
				if(!msg.equals("") && !msg.equals(null)) {
					st = new StringTokenizer(msg, "|");
					String command = st.nextToken();
					String text = st.nextToken();
					if(command.equals("TALK")) {
						memo.append(text + "\n");
					}else if(command.equals("LIST")) {
						list.setText(text);
					}else if(command.equals("NAME")) {
						name.setText(text);
					}
				}
			}
		}catch(IOException e) {
			memo.append(e.toString());
		}
	}
	
	public void actionPerformed(ActionEvent e) {
//		if(e.getSource() == input) {
//			String data = input.getText();
//			input.setText("");
//						
//			out.println("TALK|"+data); //추가
//			out.flush();
//		}
	}
	public void destroy() {
		if(clock !=null &&clock.isAlive()) {
			clock=null;
		}
		out.println("LOGOUT|asd");
		out.flush();
		
		try {
			out.close();
			in.close();
			mySocket.close();
		}catch(IOException e) {
			memo.append(e.toString());
		}
	}
}
