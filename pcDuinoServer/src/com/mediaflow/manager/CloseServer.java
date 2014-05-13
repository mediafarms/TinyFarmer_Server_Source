package com.mediaflow.manager;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;


/**
 * 프로그램 종료 메세지를 받기 위한 소켓 서버
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class CloseServer extends Thread{

	private Logger logger = Logger.getLogger(this.getClass());

	ServerSocket server = null;
	public static DataInputStream inFromServer = null;
	public static int socket_port = 9901;


	public CloseServer(String _port) {
		try
		{
			socket_port =Integer.parseInt(_port);
		}
		catch(Exception exception)
		{
			logger.warn("CloseServer exception: "+ exception.getMessage());
		}
	}
	

	@Override
	public void run() {
		byte c= 0;
		String readValue = "";
		Socket connection = null;
		try {
			server = new ServerSocket(socket_port);
			connection =  server.accept();
			inFromServer = new DataInputStream(connection.getInputStream());


			while(true)
			{
//				logger.info("start close server!!");
				c = inFromServer.readByte();
				readValue += new String(new byte[]{c});

				if(readValue.contains("close")) // close 메세지를 받을 경우 프로그램 종료
				{
					logger.info("Close mediafarm program");
					connection.close();
					server.close();
					System.exit(0);
				}
			}

		} catch (IOException e) {
			logger.warn("CloseServer run exception: "+  e.getMessage());
		}
	}

}
