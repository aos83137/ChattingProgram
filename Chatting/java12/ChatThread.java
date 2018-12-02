package java12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;

public class ChatThread extends Thread {
	Socket mySocket;
	ChatServer myServer;
	//�߰�
	String userName = "����" + ((int)(Math.random()*100)+1); //Ŭ�� ���ӽ� ��ȭ�� ����
	
	//
	PrintWriter out;
	BufferedReader in;
	
	public ChatThread() {	}
	public ChatThread(ChatServer Server, Socket accept) {
		// TODO Auto-generated constructor stub
		
		super("ChatThread");		
		mySocket = accept;
		myServer = Server;
		out=null;
		in =null;
		
//		try {
//			sendName(userName);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void sendMessage(String msg) throws IOException{
		out.println(msg);
		out.flush();
	}
	
	public void sendName(String msg) throws IOException 
	{ 
		sendMessage("NAME|"+msg);
	}
	
	public void disconnect() {
		try {
			out.flush();
			in.close();
			out.close();
			mySocket.close();
			myServer.removeClient(this);
			myServer.listBroadcast(myServer.userList); // �߰�

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		try {
			out = new PrintWriter(new OutputStreamWriter(mySocket.getOutputStream(),"KSC5601"),true);// ���⼭ ����� ����Ƽ ����ְ�
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream(),"KSC5601"),1024);
			
			while(true) {
				String inLine = in.readLine();
				if(!inLine.equals("")&&!inLine.equals(null)) {
					messageProcess(inLine);		//���� ������ ���߿� ����� ������ �ϱ�			
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			disconnect();
		}
	}
	
	public void messageProcess(String msg) {
		System.out.println(msg);
		StringTokenizer st = new StringTokenizer(msg, "|");
		String command =st.nextToken();
		String talk = st.nextToken();
		
		if(command.equals("LOGIN")) {
			System.out.println("���� " + mySocket);
			try {
				myServer.listBroadcast(myServer.userList); // �߰�
				myServer.broadcast("���� �����ڼ� " + myServer.clientNum + "��");
				sendName(userName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if (command.equals("LOGOUT")) {
			try {
				myServer.clientNum--;
				myServer.broadcast("��������  - "+userName); 
				myServer.broadcast("���� �����ڼ� " + myServer.clientNum +"��"); 
			}catch(IOException e) {
				System.out.println(e.toString());
			}
			disconnect();
		}else if(command.equals("TALK")) {
			try {
				myServer.broadcast(userName+" : "+talk);
			}catch(IOException e) {
				System.out.println(e.toString());
			}
		}else if(command.equals("NAME")) {
			try {
					myServer.broadcast("��ȭ���� "+userName +" -> "+talk +" ����");
				} catch (IOException e) {
					System.out.println(e.toString());					
				}

				userName = talk;
				try {
					myServer.userSort();
					myServer.listBroadcast(myServer.userList);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // �߰�
			
		}
	}
}
