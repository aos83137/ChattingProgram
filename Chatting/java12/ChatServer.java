package java12;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

public class ChatServer {
	Vector clientVector = new Vector();
	int clientNum=0;
	
	
	// �߰�
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
			userSort();	 // �߰�
			clientVector.removeElement(client);
			client = null;
			System.gc();
		}
	}
	
	public void addClient(ChatThread client) {
		synchronized(clientVector){
			clientVector.addElement(client);
			userSort(); // �߰�
		}
	}
	
	public static void main(String[] args) {
		ServerSocket myServerSocket = null;
		
		ChatServer myServer = new ChatServer();
		try {
			myServerSocket = new ServerSocket(2486);
		} catch (IOException e) { //������ �������� �� ����� ������ �߻����� ���.
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("���� ��� ���� " + myServerSocket);
		
			try {
				while(true) {
				ChatThread client = new ChatThread(myServer, myServerSocket.accept());
				client.start();
				myServer.addClient(client);
				
				System.out.println("Test\n" + myServer.userList);  // test��
				
				myServer.clientNum++;
				System.out.println("���� �����ڼ� " + myServer.clientNum + "��"); 
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}
}
