package antWorld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import utilities.IOEvent;
import utilities.InformationHighEvent;
import utilities.InformationLowEvent;
import utilities.Logger;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public final class WorldParser {
	private static final String folderName = "worlds";
	private static final File folder = new File(folderName);
	private static final String fileNameSuffix = ".world";
	
	/**
	 * @param name
	 * @return the world in the file "name"
	 */
	public static World readWorldFrom(String name) {
		String path = folderName + "\\" + name + "" + fileNameSuffix;
		Logger.log(new InformationLowEvent("Begun reading World object from \"" + path + "\""));
		BufferedReader br;
		File f = new File(path);
		String line;
		World world = null;
		String[] rowCellStrings;
		char[][] cellChars;
		int rows;
		int cols;
		int r;
		int c;
		
		try{
			br = new BufferedReader(new FileReader(f));
			
			//Check for the length specification at the top of the file
			try{
				rows = Integer.parseInt(br.readLine());
				cols = Integer.parseInt(br.readLine());
			}catch(NumberFormatException e){
				Logger.log(new IOEvent("Failed to read row and column values from "
					+ path + ". " + e.getMessage(), e));
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
			Logger.log(new IOEvent(e.getMessage(), e));
		}
		Logger.log(new InformationHighEvent("Completed reading World object from \"" + path + "\""));
		return world;
	}
	
	/**
	 * @param world
	 * @param name name to give the file
	 */
	public static void writeWorldTo(World world, String name) {
		String path = folderName + "\\" + name + "" + fileNameSuffix;
		Logger.log(new InformationLowEvent("Begun writing World object to \"" + path + "\""));
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
			Logger.log(new IOEvent(e.getMessage(), e));
		}
		Logger.log(new InformationHighEvent("Completed writing World object to \"" + path + "\""));
	}
}
