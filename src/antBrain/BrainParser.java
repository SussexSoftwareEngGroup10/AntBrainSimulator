package antBrain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import utilities.IOEvent;
import utilities.IllegalArgumentEvent;
import utilities.InformationHighEvent;
import utilities.InformationLowEvent;
import utilities.Logger;

/**
 * @title BrainParser
 * @purpose facilitates the reading and writing of brains to and from files.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class BrainParser {
	private static final String folderName = "brains";
	private static final File folder = new File(folderName);
	private static final String fileNameSuffix = ".ant";
	
	/**
	 * @title BrainParser
	 * @purpose disables instantiation of this class
	 * @throws InstantiationException when called
	 */
	public BrainParser() throws InstantiationException {
		throw new InstantiationException("BrainParser class cannot be instantiated");
	}
	
	/**
	 * @title readBrainFrom
	 * @purpose attempts to read a Brain object from the path specified
	 * @param name the path to the file
	 * @return the Brain read from the file specified
	 * @throws IOEvent if any IO operations fail
	 * @throws IllegalArgumentEvent if state cannot be constructed
	 */
	public static Brain readBrainFrom(String name) throws IOEvent, IllegalArgumentEvent {
		String path;
		if(name.endsWith(fileNameSuffix)){
			path = name;
		}else{
			path = name + fileNameSuffix;
		}
		if(!path.contains("\\" + folderName + "\\")){
			path = folderName + "\\" + path;
		}
		
		Logger.log(new InformationLowEvent("Begun reading Brain object from \"" + path + "\""));
		
		Brain brain = new Brain(50);
		BufferedReader br;
		File f = new File(path);
		if(!f.exists()){
			throw new IllegalArgumentEvent("File specified does not exist");
		}
		String line;
		String[] lineParts;
		
		int stateNum;
		
		try{
			br = new BufferedReader(new FileReader(f));
			
			//Read in the file 1 line at a time, creating a State from each line
			int state = 0;
			while(true){
				line = br.readLine();
				//Gives: "Sense Ahead 1 3 Food  ; state 0:
				//[SEARCH] is there food in front of me?"
				
				if(line == null){
					//End of file has been reached
					break;
				}
				
				line.trim();
				if(line.equals("")){
					continue;
				}
				
				lineParts = line.split(";");
				//Gives: "Sense Ahead 1 3 Food  "," state 0:
				//[SEARCH] is there food in front of me?"
				
				lineParts[0] = lineParts[0].trim();
				//Gives: "Sense Ahead 1 3 Food"
				
				try{
					//Get stateNum from stateStrings[1]
					lineParts[1] = lineParts[1].trim();
					stateNum = getStateNum(lineParts[1]);
				}catch(ArrayIndexOutOfBoundsException e){
					stateNum = state;
				}
				brain.put(stateNum, new State(stateNum, lineParts[0]));
				state++;
			}
			br.close();
		}catch(IOException e){
			throw new IOEvent(e.getMessage(), e);
		}
		Logger.log(new InformationHighEvent("Completed reading Brain " +
			"object from \"" + path + "\""));
		return brain;
	}
	
	/**
	 * @title writeBrainTo
	 * @purpose attempts to write a Brain object to the path specified
	 * @param brain the Brain to be written
	 * @param name the path to the file to be created
	 * @throws IOEvent if any of the IO operations fail
	 */
	public static void writeBrainTo(Brain brain, String name) throws IOEvent {
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
		Logger.log(new InformationLowEvent("Begun writing Brain object to \"" + path + "\""));
		
		File outputFile = new File(path);
		
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			
			//Clear the output file
			if(outputFile.exists()){
				outputFile.delete();
			}
			outputFile.createNewFile();
			
			bw.write(brain.toString());
			bw.close();
		}catch(IOException e){
			throw new IOEvent(e.getMessage(), e);
		}
		Logger.log(new InformationHighEvent("Completed writing Brain " +
			"object to \"" + path + "\""));
	}
	
	private static int getStateNum(String string) {
		//Get the state number from the given string. Starts at the
		//index after "state ", and looks for continuous ints.
		String stateNumString = "";
		String substring;
		int i = 0;
		
		for(i = 6; i < string.length(); i++){
			substring = string.substring(i, i + 1);
			try{
				Integer.parseInt(substring);
			}catch(NumberFormatException nfe){
				break;
			}
			stateNumString += substring;
		}
		
		return Integer.parseInt(stateNumString);
	}
}
