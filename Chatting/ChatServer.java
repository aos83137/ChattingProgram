package Chatting;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Calendar;
import java.util.Vector;

public class ChatServer {
	Vector clientVector = new Vector();
	int clientNum=0;
	
	
	// 추가
	String userList = "";
	//
	
	//시간 표시용 클래스
	class ChatTime{
       Calendar now = Calendar.getInstance();
       int hour = now.get(Calendar.HOUR);
       int minute= now.get(Calendar.MINUTE);
       int amPm= now.get(Calendar.AM_PM);

       public String getTime() {
           String strAmPm = null;
           if(amPm == Calendar.AM) 
        	   strAmPm = "AM";
           else 
        	   strAmPm = "PM";
         
    	   return ("["+strAmPm+" "+hour+":"+minute+"] ");
        }
     }
    //
	
	
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
	
	public void listBroadcast(String msg) throws IOException{
		synchronized(clientVector){
			for(int i=0;i<clientVector.size();i++) {
				ChatThread client = (ChatThread) clientVector.elementAt(i);
				synchronized(client) {
					client.sendMessage("LIST|"+msg);
				}
			}
		}
	}
	
	public void broadcast(String msg) throws IOException{
		ChatTime time = new ChatTime();
		synchronized(clientVector){
			for(int i=0;i<clientVector.size();i++) {
				ChatThread client = (ChatThread) clientVector.elementAt(i);
				synchronized(client) {
					client.sendMessage("TALK|"+time.getTime()+msg);				}
			}
		}
	}
	
	public void removeClient(ChatThread client)
	{
		synchronized(clientVector) {
			clientVector.removeElement(client);
			client = null;
			userSort();	 // 추가
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
				
//				System.out.println("Test\n" + myServer.userList);  // test용
				
				myServer.clientNum++;
				System.out.println("현재 접속자수 " + myServer.clientNum + "명"); 
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}
}
