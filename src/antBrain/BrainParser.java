package antBrain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import utilities.IOWarningEvent;
import utilities.InformationEvent;
import utilities.Logger;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class BrainParser {
	public static Brain readBrainFrom(String path) {
		if(Logger.getLogLevel() >= 3){
			Logger.log(new InformationEvent("Begun reading Brain object from \"" + path + "\""));
		}
		Brain brain = new Brain();
		BufferedReader br;
		File f = new File(path);
		String line;
		String[] lineParts;
		
		int stateNum;
		
		try{
			br = new BufferedReader(new FileReader(f));
			
			//Read in the file 1 line at a time, creating a State from each line
			while(true){
				line = br.readLine();
				//Gives: "Sense Ahead 1 3 Food  ; state 0:  [SEARCH] is there food in front of me?"
				
				if(line == null){
					//End of file has been reached
					break;
				}
				
				lineParts = line.split(";");
				//Gives: "Sense Ahead 1 3 Food  "," state 0:  [SEARCH] is there food in front of me?"
				
				lineParts[0] = lineParts[0].trim();
				//Gives: "Sense Ahead 1 3 Food"
				
				//Get stateNum from stateStrings[1]
				lineParts[1] = lineParts[1].trim();
				stateNum = getStateNum(lineParts[1]);
				
				brain.setState(new State(stateNum, lineParts[0]));
			}
			br.close();
		}catch(IOException e){
			Logger.log(new IOWarningEvent(e.getMessage(), e));
		}
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("Completed reading Brain object from \"" + path + "\""));
		}
		return brain;
	}
	
	public static void writeBrainTo(Brain brain, String path) {
		if(Logger.getLogLevel() >= 3){
			Logger.log(new InformationEvent("Begun writing Brain object to \"" + path + "\""));
		}
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
			Logger.log(new IOWarningEvent(e.getMessage(), e));
		}
		if(Logger.getLogLevel() >= 3){
			Logger.log(new InformationEvent("Completed writing Brain object to \"" + path + "\""));
		}
	}
	
	/**
	 * Gets the state number from the given string
	 * Starts at the index after "state ", and looks for ints
	 * Returns the unbroken int starting at said index
	 * e.g. "state 123 456"
	 * returns 123
	 * 
	 * @param string
	 * @return
	 */
	private static int getStateNum(String string) {
//		Alternative method using regex,
//		which is slower but more versatile
//		//Regex which finds any int in the input string:
//		Pattern patternInt = Pattern.compile("\\d+");
//		Matcher matchInt = patternInt.matcher(string);
//		
//		//Find each character in stateStrings[1] which is an int
//		matchInt.find();
//
//		//Group all ints found in stateStrings[1], as a string
//		String stateNumString = matchInt.group();
		
		//Get a number from the string given, starting after "state "
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
