package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Logger {
	//Logging level constants
	public static final int NOLOGGING = 0;
	public static final int ERRORLOGGING = 1;
	public static final int WARNINGLOGGING = 2;
	//TODO make these more intuitive
	public static final int INFOLOGGING1 = 3;
	public static final int INFOLOGGING2 = 4;
	public static final int INFOLOGGING3 = 5;
	public static final int INFOLOGGING4 = 6;
	public static final int INFOLOGGING5 = 7;
	public static final int INFOLOGGING6 = 8;
	public static final int INFOLOGGING7 = 9;
	public static final int ALLLOGGING = 10;
	
	private static int logLevel = 1;
	private static final long startTime = System.nanoTime();
	private static long restartTime = startTime;
	private static final long sizeLimit = 10000000; //10MB
	private static final String folderName = "logs";
	private static final File folder = new File(folderName);
	private static final String fileNamePrefix = "log_";
	private static final String fileNameSuffix = ".log";
	private static final PrintStream sysErr = System.err;
	private static PrintStream logErr;
	private static File file;
	
	private static void nextLogFile() {
		if(!folder.exists()){
			folder.mkdir();
		}
		
		//Sets up the writer to a new log file
		int i = -1;
		do{
			i++;
			file = new File(folderName + "\\" + fileNamePrefix + "" + i + "" + fileNameSuffix);
		}while(file.exists());
		
		//Create log file
		try{
			file.createNewFile();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//Setup print stream to file
		try {
			logErr = new PrintStream(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static File getLog() {
		return file;
	}
	
	public static void clearLogs() {
		//Deletes every file beginning with the above prefix and ending with the above suffix
		File folder = new File(folderName + "\\");
		File[] files = folder.listFiles();
		int i = 0;
		
		for(i = 0; i < files.length; i++){
			if(files[i].getPath().startsWith(folderName + "\\" + fileNamePrefix)
				&& files[i].getPath().endsWith(fileNameSuffix)){
				files[i].delete();
			}
		}
	}
	
	public static void log(Event e) {
		if(file == null || file.length() >= sizeLimit){
			nextLogFile();
		}
		
		//Setup writing to log file
		System.setErr(logErr);
		
		//Write the toString of e to a log file
		System.out.println(e.toString());
		System.err.println(e.toString());
		
		//Reset printing to the console
		System.setErr(sysErr);
	}
	
	public static int getLogLevel() {
		return logLevel;//TODO
	}
	
	public static void setLogLevel(int logLevel) {
		Logger.logLevel = logLevel;
	}
	
	public static void restartTimer() {
		restartTime = System.nanoTime();
	}
	
	public static long getCurrentTime() {
		//If there has been one, returns the time since the last restartTimer() call,
		//else time since start of main()
		return System.nanoTime() - restartTime;
	}
	
	public static void logCurrentTime(String message) {
		//Logs time since custom point, rather than the default time since a point
		Logger.log(new TimeEvent(getCurrentTime(), message));
	}
	
	public static long getStartTime() {
		return startTime;
	}
}
