package antBrainOps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrainParser {
	public BrainParser() {
		
	}
	
	public Brain readBrainFrom(String path) {
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
				//Gives: "Sense Ahead 1 3 Food  " " state 0:  [SEARCH] is there food in front of me?"

				lineParts[0] = lineParts[0].trim();
				//Gives: "Sense Ahead 1 3 Food"

				//Get stateNum from stateStrings[1]
				stateNum = getStateNum(lineParts[1]);

				brain.setState(stateNum, new State(stateNum, lineParts[0]));
			}
				
			br.close();
			
		}catch(FileNotFoundException FNF){
			FNF.printStackTrace();
		}catch(IOException IO){
			IO.printStackTrace();
		}
		
		return brain;
	}
	
	public void writeBrainTo(Brain brain, String path) {
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
			
		}catch(FileNotFoundException FNF){
			FNF.printStackTrace();
		}catch(IOException IO){
			IO.printStackTrace();
		}
	}
	
	private int getStateNum(String string) {
		//Regex which finds any int in the input string:
		Pattern patternInt = Pattern.compile("\\d+");
		Matcher matchInt = patternInt.matcher(string);
		
		//Find each character in stateStrings[1] which is an int
		matchInt.find();

		//Group all ints found in stateStrings[1], as a string
		String stateNumString = matchInt.group();
		
		return Integer.parseInt(stateNumString);
	}
}
