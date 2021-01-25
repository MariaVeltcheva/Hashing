import java.util.*;
import java.io.*;

public class Shuffled{
	
	
	/**Shuffles the records in cleaned_data.csv and extracts the 
	 * Date/Time string from each to be written to a textfile.**/
	public static void main(String args[]){
		
		String inputFile = "../data/cleaned_data.csv";
		
		File f = new File(inputFile);
		Scanner s = null;
		ArrayList<String>  arrAllRecords = new ArrayList<String>();
		
		//load all records from data file into ArrayList
		int lineCount = -1;
		try{
		 s = new Scanner(f);
		 while((s.hasNext())){
			 String line = s.nextLine();
			 String[] parts = line.split(",");
			 String dateTime = parts[0];
			 if (lineCount>=0)
				arrAllRecords.add(dateTime);
			 
			 lineCount++;
		 }
		}catch(IOException e) {System.out.println(e);}
		
		//shuffle the array list
		Collections.shuffle(arrAllRecords);
		
		//write shuffled records to a text file
		String shuffledOutput= "../data/cleaned_data_shuffled.txt";
		try{
		   PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(shuffledOutput, false)));
		   for (String date : arrAllRecords)
				{w.println(date);}
		   w.close();
		}catch(IOException e) {System.out.println(e);}
	
	}

}
