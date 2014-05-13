package com.mediaflow.model;


/**
 * 프로그램 기본 설정과 관련된 데이터 모델
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class ConfigModel {
	public String device = ""; // 장치 종류 pc, pcDuino
	public String server_ip = ""; // Server ip
	public String server_port = ""; // Server port
	public String serial_port = ""; // Serial port
	public String close_server_port = ""; // 프로그램 종료를 위한 내부 Server port
	public String id_file_path = ""; // Server port
	public String cpp_file_path = ""; // Serial port
	
	public int alive_time = 30000; // Alive 메세지 시간 주기(2014.04.29 추가, 기본값 30초) 
}




