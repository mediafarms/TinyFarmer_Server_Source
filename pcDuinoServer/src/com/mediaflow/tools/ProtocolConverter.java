package com.mediaflow.tools;


import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.mediaflow.model.MItem;
import com.mediaflow.model.MSerialMsg;
import com.mediaflow.value.Values;

/**
 * 프로토콜을 메세지에 따라 해당 데이터 모델로 변환 
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class ProtocolConverter {
	
	static private Logger logger = Logger.getLogger("ProtocolConverter");
	

	/**
	 * Bitmoss로부터 받은 메세지를 시리얼데이터 모델로 만들어 반환
	 * @param _arduinoMsgList
	 * @return
	 */
	static public MSerialMsg msgToSerialProtocol(ArrayList<Byte> _arduinoMsgList)
	{
		MSerialMsg mSerialMsg = new MSerialMsg();
		byte[] _arduinoMsg = new byte[_arduinoMsgList.size()]; 
		
		byte totalByte = 0;
		for(int i = 0 ; i < _arduinoMsg.length ; i++)
		{
			_arduinoMsg[i] = _arduinoMsgList.get(i).byteValue();

			if(i < (_arduinoMsg.length-1))
				totalByte += _arduinoMsg[i];
		}

		if(totalByte != _arduinoMsg[_arduinoMsg.length-1]) // crc check
		{
			logger.warn("crc not match");
			return null;
		}


		try
		{
			mSerialMsg.addr = _arduinoMsg[0]+"";
			mSerialMsg.mode = _arduinoMsg[1]+"";
			mSerialMsg.type = _arduinoMsg[2]+"";


			if(mSerialMsg.mode.equals(Values.MODE_CONTROL))						
				return mSerialMsg;
			else
				mSerialMsg.count = _arduinoMsg[3]+"";
			
			int beforeCount = 4; // header + length
			
			//Values.currentSocketValue;
			
			for(int i = 0 ; i < Values.currentSocketValue.item.size() ; i++)
			{				
				if(Values.currentSocketValue.item.get(i).itemValuesLength.contains(","))
				{
					String[] splitStr = Values.currentSocketValue.item.get(i).itemValuesLength.split(",");
					String addValue = "";
					for(int z = 0 ; z < splitStr.length ; z++)
					{
						byte[] valueBytes = new byte[Integer.parseInt(splitStr[z])];
	
						for(int a = 0 ; a < valueBytes.length ; a++)
						{
							
							valueBytes[a] = _arduinoMsg[beforeCount];
							beforeCount++;
						}
						
						addValue += byteToString(valueBytes);
						if(z < splitStr.length-1)
							addValue+= ",";
					}
				
					Values.currentSocketValue.item.get(i).itemValues = addValue;
				}
				else 
				{
					
					String addValue = "";
					int byteLength = Integer.parseInt(Values.currentSocketValue.item.get(i).itemValuesLength);
					
					byte[] valueBytes = new byte[byteLength];
					for(int a = 0 ; a < byteLength ; a++)
					{
						
						valueBytes[a] = _arduinoMsg[beforeCount];
						beforeCount++;
					}
					addValue = byteToString(valueBytes);
					
			
					Values.currentSocketValue.item.get(i).itemValues = addValue;
				}
			}
			

			for(int i = 0 ; i < Values.currentSocketValue.item.size() ; i++)
			{				
				mSerialMsg.item.add(new MItem(Values.currentSocketValue.item.get(i).itemType, Values.currentSocketValue.item.get(i).itemNo
						,Values.currentSocketValue.item.get(i).itemCode, 
						Values.currentSocketValue.item.get(i).itemValuesLength, Values.currentSocketValue.item.get(i).itemValues));			
			}


		} catch (Exception e) {
			logger.info("msgToSerialProtocol parseXml exception: "+e.getMessage());
			return null;
		}


		return mSerialMsg;
	}
	

	/**
	 * byte 배열을 String 형태로 변환
	 * @param _bytes
	 * @return
	 */
	static public String byteToString(byte[] _bytes)
	{
		int[] intValues = new int[_bytes.length];
		int returnValue = 0;
		int shiftValue = 8*(_bytes.length-1);
		for(int i = 0 ; i< intValues.length ; i++)
		{
			int tempValue = _bytes[i] & 0xFF;
			returnValue += (tempValue << shiftValue);
			shiftValue -= 8;
		}

		return ""+returnValue;
	}
}
