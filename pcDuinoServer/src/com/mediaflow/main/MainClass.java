package com.mediaflow.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import org.xml.sax.InputSource;

import bridge.IBridgeServer;
import bridge.TCPClientManager;

import com.mediaflow.manager.CloseServer;
import com.mediaflow.manager.SerialManager;
import com.mediaflow.manager.SystemObserver;
import com.mediaflow.manager.XMLManger;
import com.mediaflow.model.ConfigModel;
import com.mediaflow.value.Values;



/**
 * 프로그램 실행 시 소켓, 시리얼 등의 기능을 활성화
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class MainClass   {

	static private Logger logger = Logger.getLogger("MainClass");

	static public IBridgeServer client = null;
	static public SerialManager serial = null;
	XMLManger xmlManager;

	SystemObserver systemObserver;

	static public ConfigModel config = null; // 프로그램 config 설정 값


	/**
	 * 프로그램 설정이 PC일 시 바로 MainClass를 실행하고
	 * pcduino일 시 인자를 받아 프로그램을 시작하거나 종료하는 소켓을 생성
	 * @param args
	 */
	public static void main(String[] args) {

		Logger startLogger = null;
		startLogger = Logger.getLogger("appication log");

		config = XMLManger.getConfigModel();
		logger.info("device: "+config.device);

		

		if(config.device.equals(Values.DEVICE_TYPE_PC))
		{
			MainClass main = new MainClass();	    	
		}
		else if(config.device.equals(Values.DEVICE_TYPE_PCDUINO))
		{
			Values.deviceId = XMLManger.getDeviceID(config);

			if(Values.deviceId.equals("")) // 관리자 프로그램에서 Device ID 값이 설정되지 않았을 경우 프로그램 종료
			{
				logger.warn("not found device id");
				return;
			}
			
			
			MainClass main = null;
			if(args[0].equals("start")) // 명령 인자로 start가 들어올 경우 프로그램 실행
			{
				startLogger.info("start!!");
				main = new MainClass();
			}
			else if(args[0].equals("stop")) // 명령 인자로 start가 들어올 경우 프로그램 종료
			{
				startLogger.info("stop!!");
				shutdown();
				System.exit(0);
			}
		}
	}




	/**
	 * 서버를 중지하기 위한 shutdown을 실행.
	 * 종료메세지를 전달하기 위한 소켓을 생성하고 메세지를 보낸 뒤 프로그램 종료
	 * @param host
	 *            Host Ip Address
	 * @param prot
	 *            Host Port Number
	 */
	private static void shutdown() {
		logger.info("shutdown!!");
		
		Socket socket = null;
		OutputStream os = null;
		try {								
			socket = new Socket(Values.LOCAL_IP, Integer.parseInt(config.close_server_port));
			os = socket.getOutputStream();
			os.write("close".getBytes());		
			os.flush();
			os.close();
			socket.close();
		} catch (Throwable t) {
			logger.info("shutdown Throwable exception: "+t.getMessage());
			System.exit(1);
		} finally {
			try {
				os.close();
				socket.close();				
			} catch (Exception exception) {
				logger.info("shutdown finally exception: "+exception.getMessage());
			}
		}
	}



	/**
	 * 프로그램이 시작되기 위한 클래스를 생성하고 실행
	 */
	public MainClass() {
		try
		{
			if(config.device.equals(Values.DEVICE_TYPE_PCDUINO)) // pcduino일 시 pcduino io pin output 세팅 작업 실시
			{
				Values.gpioEnabled.setModeOUTPUT();

				Values.gpioTX.setModeOUTPUT();
				Values.gpioTX.setHIGH();    	
				Values.gpioRX.setModeUART();
				Values.gpioTX.setModeUART();

				for(int i = 0 ;  i < Values.relayPins.length ; i++)
					Values.relayPins[i].setModeOUTPUT();
				
				
				// pcDuino에서 프로그램 종료를 하기 위한 소켓서버 생성
				CloseServer closeServer = new CloseServer(config.close_server_port); // 프로그램 종료 이벤트를 받기 위한 서버 소켓 생성
				closeServer.start();
			}

			logger.info("socketIP: "+config.server_ip);
			logger.info("socketPort: "+config.server_port);
			logger.info("serialPort: "+config.serial_port);
			logger.info("aliveTime: "+config.alive_time);


			// 서버와의 통신을 위한 소켓 생성
			client = new TCPClientManager();
			client.ConnectServer(config.server_ip, config.server_port, Values.deviceId, config.alive_time, XMLManger.getCameraCount(config));
			
			// 시리얼 통신 포트 오픈
			serial = new SerialManager(config.serial_port);

			
			// 메세지 관리자 실행
			systemObserver = new SystemObserver();
			systemObserver.start();

			
			// 프로그램 종료 이벤트 등록
			Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
		}
		catch(Exception exception)
		{
			logger.warn("MainClass exception: "+exception.getMessage());
		}
	}



	/**
	 * 프로그램 종료 시 실행되어 소켓 및 시리얼 통신을 종료
	 * @author Mini
	 *
	 */
	public class ShutdownHookThread extends Thread {

		public void run() {            
			logger.info("Shutdown!!!");

			try
			{
				serial.SerialDisconnected();
				client.DisConnectServer();
			}
			catch(Exception ex)
			{
				logger.warn("ShutdownHookThread run exception: "+ex.getMessage());
			}
		}
	}







	


}


