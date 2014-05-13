package com.mediaflow.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import bridge.MSocketMsg;

import com.mediaflow.main.MainClass;
import com.mediaflow.value.Values;


/**
 * pcDuino cpp 프로그램과의 command 통신을 위한 Thread  
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class InputStreamFromConsole extends Thread {

	private Logger logger = Logger.getLogger(this.getClass());
	BufferedReader br = null;
	MSocketMsg currentSocketMsg; 
	
	public InputStreamFromConsole( InputStream is, MSocketMsg _currentSocketMsg ) {
		currentSocketMsg = _currentSocketMsg;
		this.br = new BufferedReader( new InputStreamReader(is)) ;
	}
	

	public void run() {
		String line = "";
		String[] values = null; 
		try {		
			if( (line=br.readLine()) != null ){
				line = line.trim();
				values = line.split(",");
			}

			String sendString = getSendToServerMsg(currentSocketMsg, values);
			logger.info("msgWrite string: "+sendString);	

			MainClass.client.msgWrite(sendString.getBytes());	
		}catch ( IOException e) {
			logger.warn("InputStreamFromConsole run IOException: "+e.getMessage());
		}
		catch ( Exception exception) {
			logger.warn("InputStreamFromConsole run exception: "+exception.getMessage());
		}
	}


	/**
	 * pcDuino IO 핀을 통한 센서값을 서버에 전달하기 위한 메세지 생성 후 반환
	 * @param _currentSocketMsg
	 * @param _values
	 * @return
	 */
	public String getSendToServerMsg(MSocketMsg _currentSocketMsg, String[] _values)
	{
		String sendMsg = "";
		try
		{
			DecimalFormat format = new DecimalFormat(".###"); // 소수점 반올림 처리를 위한 객체

			Queue<String> digitalValueQueue = new LinkedList<>(); // 아두이노에서 보내온 메세지 큐
			String analogValue = "";
			String i2cValue = "";


			sendMsg = "<data id=\""+Values.deviceId+"\" key=\""+_currentSocketMsg.key+"\" addr=\""+_currentSocketMsg.addr+"\" mode=\""+_currentSocketMsg.mode
					+"\" type=\""+_currentSocketMsg.type+"\" state=\""+0+""+"\" value=\""+0+"\">";

			

			for(int i =0 ; i< _values.length ; i++)
			{
				if(i <6)
				{
					//					_values[i] 
					if(_values[i].contains("0.0000"))
						digitalValueQueue.add("0,0");
					else
						digitalValueQueue.add(format.format(Double.parseDouble(_values[i]))+","+format.format(Double.parseDouble(_values[i+1])));

					i++;
				}
				else if(i == 6)
					analogValue = _values[i];
				else if(i == 7)
					i2cValue = _values[i];
			}




			for(int i =0 ; i< _currentSocketMsg.item.size() ; i++)
			{
				//				if(_currentSocketMsg.item.get(i).itemType.equals("0")) //anlaog
				int currentItemType = Integer.parseInt(_currentSocketMsg.item.get(i).itemType);
				if(currentItemType == 0) //anlaog
				{
					sendMsg += "<item no=\""+ _currentSocketMsg.item.get(i).itemNo +"\">"+analogValue+"</item>";
					analogValue = "0";
				}
				else if(currentItemType == 1) // digital
				{
					if(digitalValueQueue.size() > 0)
						sendMsg += "<item no=\""+ _currentSocketMsg.item.get(i).itemNo +"\">"+digitalValueQueue.poll()+"</item>";
					else
						sendMsg += "<item no=\""+ _currentSocketMsg.item.get(i).itemNo +"\">"+0+"</item>";
				}
				else if(currentItemType == 3 ) // i2c
				{
					sendMsg += "<item no=\""+ _currentSocketMsg.item.get(i).itemNo +"\">"+i2cValue+"</item>";
					i2cValue = "0";
				}
			}

			sendMsg += "</data>";
		}
		catch(Exception exception)
		{
			logger.warn("getSendToServerMsg exception: "+exception.getMessage());
		}

		return sendMsg;
	}
}
