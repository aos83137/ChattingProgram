package java12;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.StringTokenizer;

public class GameJava12 extends Applet 
implements ActionListener, Runnable,ItemListener
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
	
	
	//추가
	Choice myChoice;
    Image myImage;
    String change;
    URL url1;
    AudioClip talkAudio;
	//시간 표시용 클래스
    class ChatTime{
       Calendar now = Calendar.getInstance();
       int hour = now.get(Calendar.HOUR);
       int minute= now.get(Calendar.MINUTE);
       int seconds= now.get(Calendar.SECOND);
     
       public String getTime() {
       return ("		["+hour+"시"+minute+"분"+seconds+"초]");
        }
     }
    //
    
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
		/*try {
			url1 = new URL("D:\\Jeonyongseok\\java\\java12\\bin\\java12\\katalk.au");
			AudioClip talkAudio = Applet.newAudioClip(url1);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		//GUI
		setLayout(new BorderLayout());
		myPanel =  new Panel();

		//
        myChoice=new Choice();
        myChoice.addItem("^_^");
        myChoice.addItem("^오^");
        myChoice.addItem("ㅇㅅㅇ");
        myChoice.addItem("♡");
        myChoice.addItemListener(this);

		//
		
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
					
//		            String[] fuck = {"김종율"};//추가
					String data = input.getText();
//					for(int i=0;i<fuck.length;i++)
//					data = change.replaceAll(fuck[i],"교수님 사랑합니다..");
					input.setText("");
								
					out.println("TALK|"+data); 
					out.flush();
				}
			}
		});
		myPanel.add(input);
        myPanel.add(myChoice);//초이스 추가
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
//						talkAudio.play();
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

	}
	//아이템헨들러 추가
	 public void itemStateChanged(ItemEvent e) {
         // TODO Auto-generated method stub
         if(e.getSource()==myChoice) {
            out.println("TALK|"+myChoice.getSelectedItem()+"\n");
         }
      }
	//
	
	public void destroy() {
		if(clock !=null &&clock.isAlive()) {
			clock=null;
		}
		out.println("LOGOUT|"+name.getText());
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
