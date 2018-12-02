package java12;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

public class ChatServer {
	Vector clientVector = new Vector();
	int clientNum=0;
	
	
	// 추가
	String userList = "";
	
	
	public void userSort() {
		userList="";
		for(int i=0;i<clientVector.size();i++) {
			ChatThread client = (ChatThread) clientVector.elementAt(i);
			if(i!=clientVector.size()-1) {
				userList +=  client.userName + ", ";
			}else {
				userList +=  client.userName;
			}
		}
	}
	//
	
	public void broadcast(String msg) throws IOException{
		synchronized(clientVector){
			for(int i=0;i<clientVector.size();i++) {
				ChatThread client = (ChatThread) clientVector.elementAt(i);
				synchronized(client) {
					client.sendMessage(msg);
				}
			}
		}
	}
	
	public void removeClient(ChatThread client)
	{
		synchronized(clientVector) {
			userSort();	 // 추가
			clientVector.removeElement(client);
			client = null;
			System.gc();
		}
	}
	
	public void addClient(ChatThread client) {
		synchronized(clientVector){
			clientVector.addElement(client);
			userSort(); // 추가
		}
	}
	
	public static void main(String[] args) {
		ServerSocket myServerSocket = null;
		
		ChatServer myServer = new ChatServer();
		try {
			myServerSocket = new ServerSocket(2486);
		} catch (IOException e) { //소켓을 열고있을 때 입출력 에러가 발생했을 경우.
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("서버 대기 상태 " + myServerSocket);
		
			try {
				while(true) {
				ChatThread client = new ChatThread(myServer, myServerSocket.accept());
				client.start();
				myServer.addClient(client);
				
				System.out.println("Test\n" + myServer.userList);  // test용
				
				myServer.clientNum++;
				System.out.println("현재 접속자수 " + myServer.clientNum + "명"); 
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}
}
