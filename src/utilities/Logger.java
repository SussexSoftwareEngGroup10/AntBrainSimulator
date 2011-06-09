package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public final class Logger {
	//Singleton, only ever one instance
	private static Logger logger = new Logger();
	
	//Use these outside this class so that new levels can be inserted
	public enum LogLevel { NO_LOGGING, ERROR_LOGGING, WARNING_LOGGING, TIME_LOGGING,
	HIGH_LOGGING, NORM_LOGGING, LOW_LOGGING, ALL_LOGGING }
	
	private LogLevel logLevel = LogLevel.ALL_LOGGING;
	private final long startTime = System.nanoTime();
	private long restartTime = this.startTime;
	private final long sizeLimit = 10000000; //10MB
	private final String folderName = "logs";
	private final File folder = new File(this.folderName);
	private final String fileNamePrefix = "log_";
	private final String fileNameSuffix = ".log";
	private PrintStream logErr;
	private File file;
	
	/**
	 * 
	 */
	private Logger() {
		//No code needed
	}
	
	/**
	 * @return
	 */
	public static Logger getLogger() {
		return logger;
	}
	
	/**
	 * 
	 */
	private static void nextLogFile() {
		if(!logger.folder.exists()){
			logger.folder.mkdir();
		}
		
		//Sets up the writer to a new log file
		int i = -1;
		do{
			i++;
			logger.file = new File(logger.folderName + "\\"
				+ logger.fileNamePrefix + "" + i + "" + logger.fileNameSuffix);
		}while(logger.file.exists());
		
		//Create log file
		try{
			logger.file.createNewFile();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//Setup print stream to file
		try {
			logger.logErr = new PrintStream(new FileOutputStream(logger.file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	public static File getLog() {
		return logger.file;
	}
	
	/**
	 * 
	 */
	public static void clearLogs() {
		//Deletes every file beginning with the above prefix and ending with the above suffix
		File folder = new File(logger.folderName + "\\");
		File[] files = folder.listFiles();
		int i = 0;
		
		for(i = 0; i < files.length; i++){
			if(files[i].getPath().startsWith(logger.folderName
				+ "\\" + logger.fileNamePrefix)
				&& files[i].getPath().endsWith(logger.fileNameSuffix)){
				files[i].delete();
			}
		}
	}
	
	/**
	 * @param event
	 */
	public static void log(Event event) {
		if(logger.file == null || logger.file.length() >= logger.sizeLimit){
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
		
		if(logLevel.ordinal() > logger.logLevel.ordinal()){
			return;
		}
		
		//Write the toString of e to a log file
		logger.logErr.println(event.toString());
		//TODO remove this print
		System.out.println(event.toString());
	}
	
	/**
	 * @param logLevel
	 */
	public static void setLogLevel(LogLevel logLevel) {
		logger.logLevel = logLevel;
	}
	
	/**
	 * 
	 */
	public static void restartTimer() {
		logger.restartTime = System.nanoTime();
	}
	
	/**
	 * @return
	 */
	public static long getCurrentTime() {
		//If there has been one, returns the time since the last restartTimer() call,
		//else time since start of main()
		return System.nanoTime() - logger.restartTime;
	}

	/**
	 * @param message
	 */
	public static void logCurrentTime(String message) {
		logCurrentTime(message, TimeUnit.NANOSECONDS);
	}
	
	/**
	 * @param message
	 * @param timeUnit
	 */
	public static void logCurrentTime(String message, TimeUnit timeUnit) {
		//Logs time since custom point, rather than the default time since a point
		Logger.log(new TimeEvent(message, getCurrentTime(), timeUnit));
	}
	
	/**
	 * @return
	 */
	public static long getStartTime() {
		return logger.startTime;
	}
}
