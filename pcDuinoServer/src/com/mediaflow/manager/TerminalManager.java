package com.mediaflow.manager;


import java.io.IOException;
import org.apache.log4j.Logger;


import com.mediaflow.main.MainClass;

import bridge.MSocketMsg;

/**
 * pcDuino IO 포트 제어를 위한 프로그램을 실행
 * @author Mediaflow 
 * @mail mediaflow@mediaflow.kr
 *
 */
public class TerminalManager{

	private Logger logger = Logger.getLogger(this.getClass());
	

	/**
	 * cpp 프로그램을 실행
	 * @param _currentSocketMsg
	 */
	public TerminalManager(MSocketMsg _currentSocketMsg) {
		try
		{					
			String cmd = MainClass.config.cpp_file_path;
		    Runtime runtime = Runtime.getRuntime();
		    try {
				Process prc = runtime.exec(cmd);				
				new InputStreamFromConsole(prc.getInputStream(), _currentSocketMsg).start();				
			} catch (IOException e) {				// 
				logger.warn("TerminalManager IOException: "+e.getMessage());
			}
		}
		catch(Exception exception)
		{
			logger.warn("TerminalManager exception: "+ exception.getMessage());
		}
}

}
