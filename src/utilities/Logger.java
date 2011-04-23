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
	//Use these outside this class so that new levels can be inserted
	public enum LogLevel { NO_LOGGING, ERROR_LOGGING, WARNING_LOGGING, TIME_LOGGING,
	HIGH_LOGGING, NORM_LOGGING, LOW_LOGGING, ALL_LOGGING}
	
	private static LogLevel logLevel = LogLevel.NO_LOGGING;
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
	
	public static void log(Event event) {
		if(file == null || file.length() >= sizeLimit){
			nextLogFile();
		}
		
		//Write to file if class has a high enough priority to be logged
		LogLevel logLevel;
		if(event instanceof ErrorEvent){
			logLevel = LogLevel.ERROR_LOGGING;
		}else if(event instanceof WarningEvent){
			logLevel = LogLevel.WARNING_LOGGING;
		}else if(event instanceof TimeEvent){
			logLevel = LogLevel.TIME_LOGGING;
		}else if(event instanceof InformationHighEvent){
			logLevel = LogLevel.HIGH_LOGGING;
		}else if(event instanceof InformationNormEvent){
			logLevel = LogLevel.NORM_LOGGING;
		}else if(event instanceof InformationLowEvent){
			logLevel = LogLevel.LOW_LOGGING;
		}else{
			logLevel = LogLevel.ALL_LOGGING;
		}
		
		if(logLevel.ordinal() > Logger.logLevel.ordinal()){
			return;
		}
		
		//Setup writing to log file
		System.setErr(logErr);
		
		//Write the toString of e to a log file
		System.out.println(event.toString());
		System.err.println(event.toString());
		
		//Reset printing to the console
		System.setErr(sysErr);
	}
	
	public static void setLogLevel(LogLevel logLevel) {
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
