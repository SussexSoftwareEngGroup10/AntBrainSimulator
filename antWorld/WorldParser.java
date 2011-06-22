package antWorld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import engine.SoundPlayer;

import utilities.ErrorEvent;
import utilities.IOEvent;
import utilities.InformationLowEvent;
import utilities.InformationNormEvent;
import utilities.Logger;

/**
 *  WorldParser
 *  to allow the reading and writing of World objects to and from files.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public final class WorldParser {
	private static final String folderName = "worlds";
	private static final File folder = new File(folderName);
	private static final String fileNameSuffix = ".world";
	
	/**
	 *  WorldParser
	 *  disables instantiation of this class
	 * @throws InstantiationException when called
	 */
	public WorldParser() throws InstantiationException {
		throw new InstantiationException("WorldParser class cannot be instantiated");
	}
	
	/**
	 *  readWorldFromContest
	 *  to effectively cast the World read from the given location as
	 * suitable for a contest
	 * @param name the path to the file to be read from
	 * @param soundPlayer If sounds should be played for this world, pass in a 
	 * 					  sound player, otherwise pass in null
	 * @return a World read from the file at the path specified
	 * @throws IOEvent if file not found, or another IO exception is thrown
	 * @throws ErrorEvent if the World is not a suitable contest world
	 */
	public static World readWorldFromContest(String name, SoundPlayer soundPlayer) throws IOEvent, ErrorEvent {
		String path = getPath(name);
		World world = readWorldFrom(path, soundPlayer);
		if(!world.isContest()){
			throw new ErrorEvent("World read from \"" + path +
				"\" is not suitable for contests");
		}
		return world;
	}
	
	/**
	 *  readWorldFromCustom
	 *  to read a World from the file specified, not necessarily
	 * suitable for contests
	 * @param name the path to the file to be read from
	 * @param soundPlayer If sounds should be played for this world, pass in a 
	 * 					  sound player, otherwise pass in null
	 * @return a World read from the file at the path specified
	 * @throws IOEvent if file not found, or another IO exception is thrown
	 */
	public static World readWorldFrom(String name, SoundPlayer soundPlayer) throws IOEvent {
		String path;
		if(name.endsWith(fileNameSuffix)){
			path = name;
		}else{
			path = name + fileNameSuffix;
		}
		if(!path.contains("\\" + folderName + "\\")){
			path = folderName + "\\" + path;
		}
		
		Logger.log(new InformationLowEvent("Begun reading World object from \"" + path + "\""));
		
		BufferedReader br;
		File f = new File(path);
		String line;
		World world = null;
		String[] rowCellStrings;
		char[][] cellChars;
		int rows;
		int cols;
		
		try{
			br = new BufferedReader(new FileReader(f));
			
			//Check for the length specification at the top of the file
			try{
				rows = Integer.parseInt(br.readLine());
				cols = Integer.parseInt(br.readLine());
			}catch(NumberFormatException e){
				throw new IOEvent("Failed to read row and column values from "
					+ path + ". " + e.getMessage(), e);
			}
			
			cellChars = new char[rows][cols];
			
			//Read in the file 1 line at a time, not including first 2 lines
			for(int r = 0; r < rows; r++){
				line = br.readLine();
				
				if(line == null){
					//End of file has been reached early
					throw new IOEvent("rows < number declared");
				}
				
				//Remove indentation
				line = line.trim();
				
				//Convert into an array of chars split by space
				rowCellStrings = line.split(" ");
				if(rowCellStrings.length < cols){
					throw new IOEvent("cols in row " + r + " < number declared");
				}
				if(rowCellStrings.length > cols){
					throw new IOEvent("cols in row " + r + " > number declared");
				}
				for(int c = 0; c < cols; c++){
					cellChars[r][c] = rowCellStrings[c].charAt(0);
				}
			}
			if(br.readLine() != null){
				throw new IOEvent("rows > number declared");
			}
			br.close();
			
			world = new World(cellChars, soundPlayer);
		}catch(IOException e){
			throw new IOEvent(e.getMessage(), e);
		} catch (ErrorEvent e) {
			throw new IOEvent(e.getMessage(), e);
		}
		Logger.log(new InformationNormEvent("Completed reading World object from \"" + path + "\""));
		return world;
	}
	
	/**
	 *  writeWorldTo
	 *  to write a World to a file
	 * @param world the World to write to file
	 * @param name the name to give the file
	 * @throws IOEvent if an IOException is thrown
	 */
	public static void writeWorldTo(World world, String name) throws IOEvent {
		if(!folder.exists()){
			folder.mkdir();
		}
		
		String path;
		if(name.endsWith(fileNameSuffix)){
			path = name;
		}else{
			path = name + fileNameSuffix;
		}
		if(!path.contains("\\" + folderName + "\\")){
			path = folderName + "\\" + path;
		}
		Logger.log(new InformationLowEvent("Begun writing World object to \"" + path + "\""));
		
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
			throw new IOEvent(e.getMessage(), e);
		}
		Logger.log(new InformationNormEvent("Completed writing World object to \"" + path + "\""));
	}
	
	private static String getPath(String name) {
		String path;
		if(name.endsWith(fileNameSuffix)){
			path = name;
		}else{
			path = name + fileNameSuffix;
		}
		if(!path.contains("\\" + folderName + "\\")){
			path = folderName + "\\" + path;
		}
		return path;
	}
}
