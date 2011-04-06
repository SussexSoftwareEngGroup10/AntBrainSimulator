package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Logger {
	private static final long sizeLimit = 100 * 1000 * 1000; //100MB
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
			if(files[i].getPath().startsWith(folderName + "\\" + fileNamePrefix) && files[i].getPath().endsWith(fileNameSuffix)){
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
		System.err.println(e.toString());
		
		//Reset printing to the console
		System.setErr(sysErr);
	}
}
