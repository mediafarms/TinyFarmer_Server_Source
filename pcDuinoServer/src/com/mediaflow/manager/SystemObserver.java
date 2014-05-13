package com.mediaflow.manager;

import java.util.Timer;
import java.util.TimerTask;


import org.apache.log4j.Logger;

import com.mediaflow.main.MainClass;
import com.mediaflow.model.MSerialMsg;
import com.mediaflow.value.Values;
import bridge.*;


/**
 * 소켓서버와 RS485 시리얼 통신에서 요구되는 데이터 관리 및 처리
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class SystemObserver extends Thread{

	private Logger logger = Logger.getLogger(this.getClass());

	static public Timer msgTimer;
	static public MsgTask msgTask;


	static byte[] sendDatas;

	public SystemObserver() {

	}

	public class MsgTask extends TimerTask{
		@Override
		public void run() {
			logger.info("retry!!");
			Values.serialReadEnabled = false;
			writeMsg(sendDatas);
		}
	}

	void writeMsg(byte[] sendDatas)
	{
		try
		{
			Values.serialRetryCount++;
			System.out.println("serialRetryCount: "+Values.serialRetryCount);

			if(Values.serialRetryCount < Values.RETRY_COUNT) // msg write 시도
			{
				msgTask = new MsgTask();
				msgTimer = new Timer();
				msgTimer.schedule(msgTask, 10000);


				if(MainClass.config.device.equals(Values.DEVICE_TYPE_PCDUINO))		
				{
					Values.gpioEnabled.setHIGH();
				}

				Thread.sleep(200);

				//				MSocketMsg currentSocketMsg = MainClass.client.getCurrentSocketValue();


				MainClass.serial.output.write(sendDatas);
				Thread.sleep(100);
				Values.serialReadEnabled = true;
				Thread.sleep(500);


				if(MainClass.config.device.equals(Values.DEVICE_TYPE_PCDUINO))
				{
					
					Values.gpioEnabled.setLOW();
					Thread.sleep(300);
				}


			}
			else // 재시도 끝
			{

				Values.serialRetryCount = 0;
				Values.receiveCheck = true;

				logger.info("cancel timer!!");
				msgTimer.cancel();

				logger.info("Error Server MSG!!");

				MainClass.client.errorWrite(ErrorCodeDef.ARDUINO_UNAVAILABLE);
			}	
		}
		catch(Exception exception)
		{
			Values.serialRetryCount = 0;
			Values.receiveCheck = true;
			logger.warn("writeMsg exception: "+exception.getMessage());
		}
	}


	/**
	 * Server와 Serial 통신을 통해 들어온 데이터들을 처리
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			try {

				if(MainClass.client.getServerMessageQueue().size() > 0 && Values.receiveCheck) // Queue에 데이터가 쌓여있고  데이터가 리턴되면
				{
					Values.receiveCheck = false;

					Values.serialReadEnabled = false; // 내가 보내는 시리얼 메세지가 이벤트에서 발동하지 않도록 잠시 고정


					MSocketMsg currentSocketMsg = MainClass.client.getServerMessageQueue().poll();
					Values.currentSocketValue = currentSocketMsg;
					if(currentSocketMsg.board_type.equals(Values.BOARD_TYPE_PCDUINO))						
					{
						Values.serialRetryCount = 0;		
						Values.receiveCheck = true;

						if(currentSocketMsg.mode.equals(Values.MODE_SENSOR))
						{
							// command read 실행
							TerminalManager terminalManager = new TerminalManager(currentSocketMsg);
						}
						else if(currentSocketMsg.mode.equals(Values.MODE_CONTROL))
						{
							int relayLenth = Integer.parseInt(currentSocketMsg.item.get(currentSocketMsg.item.size()-1).itemNo);
							int realyItemIndex = 0;
							for(int i =0 ; i < relayLenth ; i++)
							{
								if(Integer.parseInt(currentSocketMsg.item.get(realyItemIndex).itemNo) == (i+1))
								{
									Values.relayPins[i].set(""+Integer.parseInt(currentSocketMsg.item.get(realyItemIndex).itemCode));
									realyItemIndex++;
								}
							}
							String returnMsg = "<data id=\""+Values.deviceId+"\" key=\""+currentSocketMsg.key+"\" addr=\""+currentSocketMsg.addr
									+"\" mode=\""+currentSocketMsg.mode+"\" type=\""+currentSocketMsg.type+"\" state=\""+"0"+"\" value=\"\"></data>";

							MainClass.client.msgWrite(returnMsg.getBytes());	
						}
					}
					else if(currentSocketMsg.board_type.equals(Values.BOARD_TYPE_ARDUINO))
					{
						sendDatas = currentSocketMsg.getSendToSerialMsg();
						Values.serialRetryCount = 0;

						writeMsg(sendDatas);			
					}

				}

				if(Values.queueSerialMSG.size() > 0)
				{
					msgTimer.cancel();

					MSerialMsg pollSerialMsg = Values.queueSerialMSG.poll();

					if(pollSerialMsg != null)
					{
						String sendServerMsg = pollSerialMsg.getSendToServerMsg(Values.currentSocketValue.key, "0", "");
						logger.info("sendServerMsg: "+sendServerMsg);
						MainClass.client.msgWrite(sendServerMsg.getBytes());
					}
					Thread.sleep(2000);
					Values.receiveCheck = true;

					Values.serialRetryCount = 0;

				}

				Thread.sleep(100);
			} 
			catch (Exception exception) {
				logger.warn("SystemObserver exception: "+exception.getMessage());
				Values.serialRetryCount = 0;
				Values.receiveCheck = true;
			}


		}
	}

}
