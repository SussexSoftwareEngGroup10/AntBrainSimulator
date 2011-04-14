package antWorld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import utilities.IOEvent;
import utilities.InformationEvent;
import utilities.Logger;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class WorldParser {
	private static final String folderName = "worlds";
	private static final File folder = new File(folderName);
	private static final String fileNameSuffix = ".world";
	
	public static World readWorldFrom(String name) {
		String path = folderName + "\\" + name + "" + fileNameSuffix;
		if(Logger.getLogLevel() >= 3){
			Logger.log(new InformationEvent("Begun reading World object from \"" + path + "\""));
		}
		if(!folder.exists()){
			folder.mkdir();
		}
		BufferedReader br;
		File f = new File(path);
		String line;
		World world = null;
		String[] rowCellStrings;
		char[][] cellChars;
		int rows = 0;
		int cols = 0;
		int r = 0;
		int c = 0;
		
		try{
			br = new BufferedReader(new FileReader(f));
			
			//Check for the length specification at the top of the file
			try{
				rows = Integer.parseInt(br.readLine());
				cols = Integer.parseInt(br.readLine());
			}catch(NumberFormatException e){
				if(Logger.getLogLevel() >= 1){
					Logger.log(new IOEvent("Failed to read row and column values from "
						+ path + ". " + e.getMessage(), e));
				}
				return null;
			}
			
			cellChars = new char[rows][cols];
			
			//Read in the file 1 line at a time, not including first 2 lines
			r = 0;
			while(true){
				line = br.readLine();
				
				if(line == null){
					//End of file has been reached
					break;
				}
				
				//Remove indentation from some lines
				line = line.trim();
				
				//Convert into an array of chars split by space
				rowCellStrings = line.split(" ");
				for(c = 0; c < cols; c++){
					cellChars[r][c] = rowCellStrings[c].charAt(0);
				}
				
				r++;
			}
			
			br.close();
			
			world = new World(cellChars);
		}catch(IOException e){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new IOEvent(e.getMessage(), e));
			}
		}
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("Completed reading World object from \"" + path + "\""));
		}
		return world;
	}
	
	public static void writeWorldTo(World world, String name) {
		String path = folderName + "\\" + name + "" + fileNameSuffix;
		if(Logger.getLogLevel() >= 3){
			Logger.log(new InformationEvent("Begun writing World object to \"" + path + "\""));
		}
		if(!folder.exists()){
			folder.mkdir();
		}
		File outputFile = new File(path);
		
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			
			//Clear the output file
			if(outputFile.exists()){
				outputFile.delete();
			}
			outputFile.createNewFile();
			
			bw.write(world.toString());
			bw.close();
		}catch(IOException e){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new IOEvent(e.getMessage(), e));
			}
		}
		if(Logger.getLogLevel() >= 3){
			Logger.log(new InformationEvent("Completed writing World object to \"" + path + "\""));
		}
	}
}
