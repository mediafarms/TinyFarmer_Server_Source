package com.mediaflow.model;

import java.util.ArrayList;

import com.mediaflow.value.Values;

/**
 * Serial data model
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class MSerialMsg {
	public String id = ""; // id value
	public String addr = ""; // addr
	public String mode = ""; // mode 
	public String type = ""; // type
	public String count = ""; // 아이템 개수
	public ArrayList<MItem> item = new ArrayList<MItem>();


	/**
	 * 시리얼 데이터 모델에서부터 서버로 보낼 메세지 반환
	 * @param _key
	 * @param _state
	 * @param _value
	 * @return
	 */
	public String getSendToServerMsg(String _key, String _state, String _value)
	{
		String sendMsg = "";
		if(mode.equals("0"))
		{
			sendMsg = "<data id=\""+Values.deviceId+"\" key=\""+_key+"\" addr=\""+addr+"\" mode=\""+mode+"\" type=\""+type+"\" state=\""+_state+""+"\" value=\""+_value+"\">";

			for(int i =0 ; i< item.size() ; i++)
				sendMsg += "<item no=\""+ item.get(i).itemNo +"\">"+item.get(i).itemValues+"</item>";
			
			sendMsg += "</data>";
		}
		else if(mode.equals("1"))
		{
			sendMsg = "<data id=\""+Values.deviceId+"\" key=\""+_key+"\" addr=\""+addr+"\" mode=\""+mode+"\" type=\""+type+"\" state=\""+_state+""+"\" value=\""+_value+"\"></data>";		
		}
		return sendMsg;
	}

}
