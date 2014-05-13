package com.mediaflow.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.mediaflow.model.ConfigModel;


/**
 * xml파일의 데이터를 읽고 해당 데이터 모델로 반환
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class XMLManger {

	static public Logger logger = Logger.getLogger("XMLManger");

	/**
	 * xml 변환 후 반환
	 * @param xml
	 * @return
	 */
	public static ConfigModel getConfigModel() {

		ConfigModel configModel = new ConfigModel();

		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		Document doc = null;

		try
		{
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();

			File file = new File("config.xml");

			doc = db.parse(file);

			Node deviceNode = doc.getElementsByTagName("device").item(0);
			Node serverIPNode = doc.getElementsByTagName("server_ip").item(0);
			Node serverPortNode = doc.getElementsByTagName("server_port").item(0);
			Node serialPortNode = doc.getElementsByTagName("serial_port").item(0);
			Node closeServerPortNode = doc.getElementsByTagName("close_server_port").item(0);			
			Node idFilePath = doc.getElementsByTagName("id_info_path").item(0);
			Node cppFilePath = doc.getElementsByTagName("cpp_file_path").item(0);
			Node aliveTime = doc.getElementsByTagName("alive_time").item(0);


			configModel.device = deviceNode.getTextContent().toString();
			configModel.server_ip = serverIPNode.getTextContent().toString();
			configModel.server_port = serverPortNode.getTextContent().toString();
			configModel.serial_port = serialPortNode.getTextContent().toString();
			configModel.close_server_port = closeServerPortNode.getTextContent().toString();			

			configModel.id_file_path = idFilePath.getTextContent().toString();
			configModel.cpp_file_path = cppFilePath.getTextContent().toString();

			configModel.alive_time = Integer.parseInt(aliveTime.getTextContent().toString());


		} catch (Exception e) {
			logger.info("parseXml exception: "+e.getMessage());
		}
		return configModel;
	}
	
	
	
	/**
	 * 관리자 프로그램을 통해 등록된 Device ID를 가져옴
	 * @return
	 */
	static public String getDeviceID(ConfigModel _config)
	{
		String deviceID = "";
		StringBuilder sb = new StringBuilder();
		try
		{
			String idPath = _config.id_file_path;									

			BufferedReader bufReader = new BufferedReader(new FileReader(idPath));

			String line = "";
			while((line = bufReader.readLine()) != null)	        
				sb.append(line);	        


			bufReader.close();


			DocumentBuilderFactory dbf = null;
			DocumentBuilder db = null;
			Document doc = null;
			InputSource is = new InputSource();

			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			is = new InputSource();
			is.setCharacterStream(new StringReader(sb.toString().trim()));
			doc = db.parse(is);
			deviceID = doc.getElementsByTagName("config").item(0).getAttributes().getNamedItem("id").getTextContent();
		}
		catch(Exception exception)
		{
			logger.warn("getDeviceID exception: "+exception.getMessage());
			return "";
		}
		
		logger.info("deviceID: "+deviceID);

		return deviceID;
	}
	
	
	
	
	

	
	
	/**
	 * 관리자 프로그램을 통해 등록된 Device ID를 가져옴
	 * @return
	 */
	static public int getCameraCount(ConfigModel _config)
	{
		int cameraCount = 0;
		StringBuilder sb = new StringBuilder();
		try
		{
			String idPath = _config.id_file_path;									

			BufferedReader bufReader = new BufferedReader(new FileReader(idPath));

			String line = "";
			while((line = bufReader.readLine()) != null)	        
				sb.append(line);	        


			bufReader.close();


			DocumentBuilderFactory dbf = null;
			DocumentBuilder db = null;
			Document doc = null;
			InputSource is = new InputSource();

			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			is = new InputSource();
			is.setCharacterStream(new StringReader(sb.toString().trim()));
			doc = db.parse(is);
			String camera1 = doc.getElementsByTagName("camera").item(0).getAttributes().getNamedItem("port1").getTextContent();
			String camera2 = doc.getElementsByTagName("camera").item(0).getAttributes().getNamedItem("port2").getTextContent();
			
			if(camera1.length() > 0)
				cameraCount++;
			
			if(camera2.length() > 0)
				cameraCount++;
			
		}
		catch(Exception exception)
		{
			logger.warn("getDeviceID exception: "+exception.getMessage());
			return 0;
		}
		
		System.out.println("cameraCount: "+cameraCount);

		return cameraCount;
	}


	


}
