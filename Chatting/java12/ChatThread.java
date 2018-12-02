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
	//추가
	String userName = "유저" + ((int)(Math.random()*100)+1); //클라 접속시 대화명 생성
	
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
			myServer.listBroadcast(myServer.userList); // 추가

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		try {
			out = new PrintWriter(new OutputStreamWriter(mySocket.getOutputStream(),"KSC5601"),true);// 여기서 입출력 피피티 띠워주고
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream(),"KSC5601"),1024);
			
			while(true) {
				String inLine = in.readLine();
				if(!inLine.equals("")&&!inLine.equals(null)) {
					messageProcess(inLine);		//이쪽 설명은 나중에 입출력 받을때 하기			
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
			System.out.println("접속 " + mySocket);
			try {
				myServer.listBroadcast(myServer.userList); // 추가
				myServer.broadcast("현재 접속자수 " + myServer.clientNum + "명");
				sendName(userName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if (command.equals("LOGOUT")) {
			try {
				myServer.clientNum--;
				myServer.broadcast("접속종료  - "+userName); 
				myServer.broadcast("현재 접속자수 " + myServer.clientNum +"명"); 
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
					myServer.broadcast("대화명변경 "+userName +" -> "+talk +" 변경");
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
				} // 추가
			
		}
	}
}
