package com.mediaflow.value;


import java.util.LinkedList;
import java.util.Queue;

import com.mediaflow.model.MSerialMsg;

import com.mediaflow.tools.GPIO_Pin;
import bridge.*;


/**
 * 프로그램에서 사용되는 전역변수 정의
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class Values {
	
	/** Device type */
	public static String DEVICE_TYPE_PC = "pc";
	public static String DEVICE_TYPE_PCDUINO = "pcduino";
	
	/** Board type */
	public static String BOARD_TYPE_PCDUINO = "pcduino";
	public static String BOARD_TYPE_ARDUINO = "arduino";
	
	/** BitMoss type */
	public static String MODE_SENSOR = "0";
	public static String MODE_CONTROL = "1";
	
	
	/** Close server 관련 */
	public static String LOCAL_IP = "127.0.0.1";


	/** Bitmoss와의 통신 시도 회수 */
	public static int RETRY_COUNT = 4;
	
	
	
	/** 포트가 오픈되기 까지 기다리기 위한 대략적인 시간(2초) */
	public static final int SERAIL_PORT_TIME_OUT = 2000;
	
	/** 포트에 대한 기본 통신 속도, 아두이노의 Serial.begin의 속도와 일치 */
	public static final int SERAIL_PORT_DATA_RATE = 9600;
	
	
	/** 아두이노에서 보내온 메세지 큐 */
	public static Queue<MSerialMsg> queueSerialMSG = new LinkedList<>(); 
	
	/** Pins */
	static public GPIO_Pin gpioEnabled = new GPIO_Pin(12); // pcduino의 rs485 통신을 위한 enabled 핀
	static public GPIO_Pin gpioRX = new GPIO_Pin(0); // pcduino의 RX 핀
	static public GPIO_Pin gpioTX = new GPIO_Pin(1); // pcduino의 TX 핀
	static public GPIO_Pin[] relayPins = new GPIO_Pin[]{new GPIO_Pin(8), new GPIO_Pin(9), new GPIO_Pin(10), new GPIO_Pin(11)}; // pcduino에 할당된 릴레이 핀

	
	/** 서버에서 인식하는 장치 ID */
	static public String deviceId = ""; 
	
	/** 이전 서버 메세지가 처리 됐는지 여부 */
	static public boolean receiveCheck = true; 

	/** Serial read 가능 여부 */
	static public boolean serialReadEnabled = true;
	
	/** 아두이노로 보내는 신호의 재시도 회수 */
	static public int serialRetryCount = 0; 

	/** 현재 다루고 있는 서버 메세지 값 */
	static public MSocketMsg currentSocketValue = new MSocketMsg(); 
}
