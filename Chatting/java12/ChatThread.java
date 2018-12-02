import java.net.*;
import java.io.*;
import java.util.*;

public class ChatThread extends Thread
{
  ChatServer myServer;
  Socket mySocket;

  PrintWriter out;
  BufferedReader in;

  public ChatThread(ChatServer server, Socket socket)
  {
    super("ChatThread");

    myServer= server;
    mySocket= socket;

    out= null;
    in= null;
  }

  public void sendMessage(String msg) throws IOException
  {
    out.println(msg);
    out.flush();
  }

  public void disconnect()
  {
    try{
      out.flush();
      in.close();
      out.close();
      mySocket.close();
      myServer.removeClient(this);
    }catch(IOException e){
      System.out.println(e.toString());
    }
  }

  public void run()
  {
    try{
      out= new PrintWriter(new OutputStreamWriter(mySocket.getOutputStream(), "KSC5601"), true);
      in= new BufferedReader(new InputStreamReader(mySocket.getInputStream(), "KSC5601"), 1024);

      while(true){
        String inLine= in.readLine();
        if(!inLine.equals("") && !inLine.equals(null)){
          messageProcess(inLine);
        }
      }
    }catch(Exception e){
      disconnect();
    }
  }

  public void messageProcess(String msg)
  {
    System.out.println(msg);

    StringTokenizer st= new StringTokenizer(msg, "|");
    String command= st.nextToken();
    String talk= st.nextToken();

    if(command.equals("LOGIN")){
      System.out.println("[접속] "+ mySocket);
      try{
        myServer.broadcast("[현재 접속자수] "+ myServer.clientNum +"명");
      }catch(IOException e){
        System.out.println(e.toString());
      }
    }else if(command.equals("LOGOUT")){
      try{
        myServer.clientNum--;
        myServer.broadcast("[접속 종료] "+ talk);
        myServer.broadcast("[현재 접속자수] "+ myServer.clientNum +"명");
      }catch(IOException e){
        System.out.println(e.toString());
      }
      disconnect();
    }else{
      try{
        myServer.broadcast(talk);
      }catch(IOException e){
        System.out.println(e.toString());
      }
    }
  }
}