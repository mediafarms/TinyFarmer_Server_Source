package com.mediaflow.manager;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.mediaflow.model.MSerialMsg;
import com.mediaflow.tools.ProtocolConverter;
import com.mediaflow.value.Values;


/**
 * RS485 Serial 통신 담당 클래스
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class SerialManager implements SerialPortEventListener{

	private Logger logger = Logger.getLogger(this.getClass());

	SerialPort serialPort;


	/** 포트에서 데이터를 읽기 위한 버퍼를 가진 input stream */
	public InputStream input;
	/** 포트를 통해 아두이노에 데이터를 전송하기 위한 output stream */
	OutputStream output;
	

	String portName = "";	
	String inputString = "";         // a string to hold incoming data

	ArrayList<Byte> valueBytes = new ArrayList<Byte>();

	/**
	 * 생성자
	 * @param _portName
	 */
	public SerialManager(String _portName) {
		portName = _portName;
		initialize();
	}


	/**
	 * Serial Port initialize
	 */
	public void initialize() {
		logger.info("serial initialize!!");
		CommPortIdentifier portId = null;


		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// 컴퓨터에서 지원하는 시리얼 포트들 중 아두이노와 연결된
		// 포트에 대한 식별자를 찾는다.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if (currPortId.getName().equals(portName)) {
				logger.info("find port id!!!");
				portId = currPortId;

				break;
			}
		}

		// 식별자를 찾지 못했을 경우 종료
		if (portId == null) {
			logger.warn("Could not find COM port.");
			return;
		}

		try {
			// 시리얼 포트 오픈, 클래스 이름을 애플리케이션을 위한 포트 식별 이름으로 사용
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					Values.SERAIL_PORT_TIME_OUT);

			// 속도등 포트의 파라메터 설정
			serialPort.setSerialPortParams(Values.SERAIL_PORT_DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// 포트를 통해 읽고 쓰기 위한 스트림 오픈
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// 아두이노로 부터 전송된 데이터를 수신하는 리스너를 등록
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * 이 메서드는 포트 사용을 중지할 때 반드시 호출해야 한다.
	 * 리눅스와 같은 플랫폼에서는 포트 잠금을 방지한다.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}


	
	/**
	 * 시리얼 통신에 대한 이벤트를 처리. 데이터를 읽고 출력한다..
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {

			try {

				byte[] readBuffer = new byte[50];

				try {
					int numBytes = 0;
					while (input.available() > 0 ) {
						 numBytes = input.read(readBuffer);
						//            						inputStream.read(arg0, arg1, arg2)
					}


					if(Values.serialReadEnabled) // Tinyfarmer가 보내는 신호가 전부 전달된 후 Receive 시작
					{
						for(int i = 0 ; i < numBytes ; i ++)
						{
							valueBytes.add(readBuffer[i]);
						}
						

						String readStr = new String(readBuffer).trim(); 
						inputString  += readStr;

						if (readStr.contains("_")) {
							valueBytes.remove(valueBytes.size()-1); // end text 제거
							MSerialMsg mSerialMsg = ProtocolConverter.msgToSerialProtocol(valueBytes);
							
							if(mSerialMsg != null)							
								Values.queueSerialMSG.add(mSerialMsg);
							else 
							{
								logger.info("mSerialMsg is null!!");
								Values.queueSerialMSG.add(mSerialMsg);
							}
							
							
							valueBytes.clear();
							inputString = "";
						}           
					}
				} 
				catch (Exception e) {
					logger.warn("SerialManager serialEvent exception1: "+e.getMessage());
				}

			} catch (Exception e) {
				logger.warn("SerialManager serialEvent exception2: "+e.getMessage());
				
			}
		}
	}



	/**
	 * Serial Disconnected
	 */
	public void SerialDisconnected()
	{
		try
		{
			output.close();
			input.close();
			serialPort.close();
		}
		catch(Exception exception)
		{    		
			logger.warn("SerialManager SerialDisconnected exception: "+exception.getMessage());
		}
	}


}
