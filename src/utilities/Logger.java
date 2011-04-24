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
	//Singleton, only ever one instance
	private static Logger logger;
	
	//Use these outside this class so that new levels can be inserted
	public enum LogLevel { NO_LOGGING, ERROR_LOGGING, WARNING_LOGGING, TIME_LOGGING,
	HIGH_LOGGING, NORM_LOGGING, LOW_LOGGING, ALL_LOGGING}
	
	private LogLevel logLevel = LogLevel.NO_LOGGING;
	private final long startTime = System.nanoTime();
	private long restartTime = this.startTime;
	private final long sizeLimit = 10000000; //10MB
	private final String folderName = "logs";
	private final File folder = new File(this.folderName);
	private final String fileNamePrefix = "log_";
	private final String fileNameSuffix = ".log";
	private final PrintStream sysErr = System.err;
	private PrintStream logErr;
	private File file;
	
	private Logger() {
		//No code needed
	}
	
	public static Logger getLogger() {
		if(logger == null){
			logger = new Logger();
		}
		return logger;
	}
	
	private static void nextLogFile() {
		if(!getLogger().folder.exists()){
			getLogger().folder.mkdir();
		}
		
		//Sets up the writer to a new log file
		int i = -1;
		do{
			i++;
			getLogger().file = new File(getLogger().folderName + "\\"
				+ getLogger().fileNamePrefix + "" + i + "" + getLogger().fileNameSuffix);
		}while(getLogger().file.exists());
		
		//Create log file
		try{
			getLogger().file.createNewFile();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//Setup print stream to file
		try {
			getLogger().logErr = new PrintStream(new FileOutputStream(getLogger().file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static File getLog() {
		return getLogger().file;
	}
	
	public static void clearLogs() {
		//Deletes every file beginning with the above prefix and ending with the above suffix
		File folder = new File(getLogger().folderName + "\\");
		File[] files = folder.listFiles();
		int i = 0;
		
		for(i = 0; i < files.length; i++){
			if(files[i].getPath().startsWith(getLogger().folderName
				+ "\\" + getLogger().fileNamePrefix)
				&& files[i].getPath().endsWith(getLogger().fileNameSuffix)){
				files[i].delete();
			}
		}
	}
	
	public static void log(Event event) {
		if(getLogger().file == null || getLogger().file.length() >= getLogger().sizeLimit){
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
		
		if(logLevel.ordinal() > getLogger().logLevel.ordinal()){
			return;
		}
		
		//Setup writing to log file
		System.setErr(getLogger().logErr);
		
		//Write the toString of e to a log file
		System.out.println(event.toString());
		System.err.println(event.toString());
		
		//Reset printing to the console
		System.setErr(getLogger().sysErr);
	}
	
	public static void setLogLevel(LogLevel logLevel) {
		getLogger().logLevel = logLevel;
	}
	
	public static void restartTimer() {
		getLogger().restartTime = System.nanoTime();
	}
	
	public static long getCurrentTime() {
		//If there has been one, returns the time since the last restartTimer() call,
		//else time since start of main()
		return System.nanoTime() - getLogger().restartTime;
	}
	
	public static void logCurrentTime(String message) {
		//Logs time since custom point, rather than the default time since a point
		Logger.log(new TimeEvent(getCurrentTime(), message));
	}
	
	public static long getStartTime() {
		return getLogger().startTime;
	}
}
