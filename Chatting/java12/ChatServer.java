import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer
{
  Vector clientVector= new Vector();
  int clientNum= 0;

  public void broadcast(String msg) throws IOException
  {
    synchronized(clientVector){
      for(int i=0; i<clientVector.size(); i++){
        ChatThread client= (ChatThread) clientVector.elementAt(i);
        synchronized(client){
          client.sendMessage(msg);
        }
      }
    }
  }

  public void removeClient(ChatThread client)
  {
    synchronized(clientVector){
      clientVector.removeElement(client);
      client= null;
      System.gc();
    }
  }

  public void addClient(ChatThread client)
  {
    synchronized(clientVector){
      clientVector.addElement(client);
    }
  }

  public static void main(String[] args)
  {
    ServerSocket myServerSocket= null;

    ChatServer myServer= new ChatServer();

    try{
      myServerSocket= new ServerSocket(2587);
    }catch(IOException e){
      System.out.println(e.toString());
      System.exit(-1);
    }
    
    System.out.println("[서버 대기 상태] "+ myServerSocket);

    try{
      while(true){
        ChatThread client= new ChatThread(myServer, myServerSocket.accept());
        client.start();
        myServer.addClient(client);

        myServer.clientNum++;
        System.out.println("[현재 접속자수] "+ myServer.clientNum +"명");
      }
    }catch(IOException e){
      System.out.println(e.toString());
    }
  }
}