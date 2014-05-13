package com.mediaflow.model;

/**
 * 서버로부터 받는 메세지 프로토콜에 포함된 item 데이터 모델
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 */
public class MItem {
	public String itemType =""; 
	public String itemCode ="";
	public String itemValues =""; 
	public String itemNo ="";

	
	/**
	 *  Control Bitmoss 메세지 일시
	 * @param _itemNo
	 * @param _itemCode
	 */
	public MItem(String _itemNo , String _itemCode)
	{
		itemCode = _itemCode;	
		itemNo = _itemNo;
	}
	
	
	/**
	 *  Sensor Bitmoss 메세지 일시
	 * @param _itemNo
	 * @param _itemCode
	 */
	public MItem(String _itemType, String _itemNo, String _itemCode, String  _itemValuesLength, String  _itemValues)
	{
		itemType = _itemType;
		itemCode = _itemCode;
		itemValues = _itemValues;
		itemNo = _itemNo;
	}
}
