import java.util.*;
import java.io.*;

public class Hashing{
	 
	 /**Stores the size of the Hash Table**/
	 public static int tblSize;
	 /**Stores the type of collision resolution scheme to be used during insertion and searching.**/
	 public static String scheme;
	 /**Stores the path to the data file (csv) who's records populate the Hash Table.**/
	 public static String inputFile;
	 /**Stores the number of keys to be searched for in the Hash Table.**/
	 public static int K;
	 /**Stores the records from the data file when linear or quadratic probing is used.**/
	 public static String[] arrHash;
	 /**Stores the records from the data file when chaining is used.**/
	 public static LinkedList<String>[] arrChain;
	 /**Stores the number of records that are in the data file which populates the Hash Table.**/
	 public static int numRecords;
	 /**Stores the total number of probes made for all insertions.**/
	 public static int probeCount;
	 /**Stores a random subset of Date/Time strings from the data file.**/
	 public static String[] searchKeys;
	 /**Stores the total number of probes generated over a set of searches.**/
	 public static int totalSearchProbes;
	 /**Stores the number of probes in the longest probe sequence over a set of searches.**/
	 public static int maxProbes;
	 /**Stores the average number of probes made per search for a set of searches.**/
	 public static int avgSearchProbes; 
	 
	/**Navigates between various method invocations and variable states depending on the 
    * arguments passed when running Hashing from the command line.
    * <p>
    * @param args[0] the size of the Hash Table.
    * @param args[1] the collision resolution scheme to be used during insertion and searching - Linear, Quadratic or Chaining can be used.
    * @param args[2] path to the file who's records will populate the Hash Table.
    * @param args[3] the number of keys to be searched for in the Hash Table.
    * **/
	public static void main(String args[]){
		
		int numArgs = args.length;
		if (numArgs != 4){
			System.out.println("Four arguments are required to run the program.");
			System.exit(0);
		}else{
			//assign command line arguments to attributes
			tblSize = Integer.parseInt(args[0]);
			if(isPrime(tblSize)==false){
				System.out.println("Table size must be a prime number.");
				System.exit(0);
			}
			scheme = args[1]; 
			inputFile = args[2];
			K = Integer.parseInt(args[3]);
		}
		probeCount =0;
		
		
		insert();
		
		System.out.println("\n" + scheme);
		
		//print out load factor and probe count for insertion
		float loadFactor = (float)numRecords/(float)tblSize;
		System.out.println(String.format("%-25s %s","\nTotal insertion probe count: "," " + probeCount));
		System.out.println(String.format("%-29s %.5s","Load Factor: ", " " + loadFactor) + "\n");
		
		//write load factor and probe count to textfiles for analysis
		String insertOutput= "../results/Insertion/" + scheme + ".txt";
		try{
		   PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(insertOutput, true)));
		   w.println(tblSize + ", " + loadFactor + ", " + probeCount);
           w.close();
	   }catch(IOException e) {System.out.println(e);}
		
		//create subset of size K from data file containing dateTime strings, pass each one to find method
		if (K>0){
			getSubset(K);//populate searchKeys array with random subset from input file
			totalSearchProbes =0;
			maxProbes =0; 
			avgSearchProbes =0;
			for (int i=0; i<K; i++) {find(searchKeys[i]);}
			avgSearchProbes = Math.round((float)totalSearchProbes/(float)K);
			
			//print out values of totalSearchProbeCount, avgSearchProbeCount and maxProbes for analysis
			System.out.println(String.format("%-29s %s","Total search probe count: "," " + totalSearchProbes));
			System.out.println(String.format("%-29s %.5s","Average search probe count: "," " + avgSearchProbes));
			System.out.println(String.format("%-29s %s","Maximum search probe count: ", " " + maxProbes) + "\n");
			
			//write load factor and totalSearchProbes count to textfile for analysis
			String totalSearchOutput= "../results/Searching/Total/Total_" + scheme + ".txt";
			try{
			   PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(totalSearchOutput, true)));
			   w.println(tblSize + ", " + loadFactor + ", " + totalSearchProbes);
			   w.close();
		    }catch(IOException e) {System.out.println(e);}
			
			//write load factor and avglSearchProbes to textfile for analysis
		    String avgSearchOutput= "../results/Searching/Average/Average_" + scheme + ".txt";
			try{
			   PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(avgSearchOutput, true)));
			   w.println(tblSize + ", " + loadFactor + ", " + avgSearchProbes);
			   w.close();
		    }catch(IOException e) {System.out.println(e);}
		   
		   //write load factor and maxProbes count to textfile for analysis
		    String maxSearchOutput= "../results/Searching/Max/Max_" + scheme + ".txt";
			try{
			   PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(maxSearchOutput, true)));
			   w.println(tblSize + ", " + loadFactor + ", " + maxProbes);
			   w.close();
		    }catch(IOException e) {System.out.println(e);}
		}
		
	}
	
	//checks if a positive integer is a prime number
	/**Checks if a given positive integer is a prime number.
	 * @param num the number that will be determined either prime or not prime.
	 * @return true if the number is prime and false if the number is not prime.**/
	public static boolean isPrime(int num){
		
		boolean prime = true;
		if (num<=1)
			return false;
		for(int i=2; i<=num; i++){
			if(num%i==0 && i!=num)
				return false;
		}
		return true;

	
	}
	/**Populates the Hash Table with records from the data file specified by the command line arguments.
	 * When inserting, the method resolves collisions according to the scheme specified by the command line arguments.**/ 
	public static void insert(){
		File f = new File(inputFile);
		Scanner s = null;
		arrHash = new String[tblSize];//initialise HashTable used for Linear and Quadratic probing
		arrChain = new LinkedList[tblSize];//initialise Hash Table used for Chaining
		numRecords = 0;
		
		int lineCount = -1;
		try{
			s = new Scanner(f);
			//loop through records in csv to insert each one individually
			while((s.hasNext()) && (lineCount<= tblSize+1)){
				String line = s.nextLine();
				if (lineCount>=0){
					String[] parts = line.split(",");
					String record = parts[0] + ", " + parts[1] + ", " + parts[3];
					String dateTime = parts[0];
					
					int hashVal = hash(dateTime);
					int checkHash = hashVal;
					int maxProbe;
					numRecords++;

				
					//Determine collision resolution scheme and probe accordingly
					int i=1;
			
					if (!scheme.equals("Chaining")){
						maxProbe = 0;
						while(arrHash[checkHash]!=null){
							probeCount++;
							if (scheme.equals("Linear")){
								if (numRecords >tblSize){
									System.out.println("The table is full.");
									System.exit(0);
								}else
									checkHash = (hashVal + i)%tblSize;	
							}if (scheme.equals("Quadratic")){
								if (maxProbe ==tblSize){
									System.out.println(maxProbe);
									System.out.println("Failed to insert " + dateTime + ". Please choose a larger table size.");
									System.exit(0);
								}else
									checkHash = (hashVal + i<<1)%tblSize;
							}
							i++;
							maxProbe++;
						}
						arrHash[checkHash] = record;//insert record into Hash Table
					}else{
						if(arrChain[hashVal]==null){
							LinkedList<String> list = new LinkedList<String>();
							list.add(record);//insert record into Hash Table
							arrChain[hashVal] = list;
						}else{
							arrChain[hashVal].add(record);//insert record into Hash Table
							probeCount++;
						}
					}
				}
			 
				lineCount++;
			}
		}catch(IOException e) {System.out.println(e);}
	}
	
	 /**Calculates and returns the index associated with the given key.
	  * @param key the key who's index will be calculated.
	  * @return the index of the given key.**/
	 public static int hash(String key){
		double hashVal = 0;
		for (int i=0 ; i<key.length(); i++)
			{hashVal+= key.charAt(i)*i*i;}
		hashVal%=tblSize;
		return (int) hashVal;	
	 }
	 
	 /**Populates the searchKeys array with a random subset of Date/Time strings from the textfile 
	  * created before running the program.
	  * @param x the number of keys to be loaded into the array.**/
	 public static void getSubset(int x){
		File f = new File("../data/cleaned_data_shuffled.txt");
		Scanner s = null;
		ArrayList<String>  arrAllRecords = new ArrayList<String>();
		searchKeys = new String[x];
		
		//load all records from textfile file into ArrayList
		int lineCount = -1;
		try{
		 s = new Scanner(f);
		 while((s.hasNext())){
			 String dateTime = s.nextLine();
			 if (lineCount>=0)
				arrAllRecords.add(dateTime);
			 
			 lineCount++;
		 }
		}catch(IOException e) {System.out.println(e);}
		
		//load K items from shuffled list into searchKeys array
		for (int i=0; i<x; i++){
			searchKeys[i] = arrAllRecords.get(i);
		}
		
	 }
	 
	  /**Searches for a record in the Hash Table associated with the given key. The 
	   * method also increments the totalSearchProbes counter as necessary and after each search 
	   * determines if the number of probes generated for the search is larger than the number of 
	   * probes generated for previous searches.
	   * @param key the key who's associated record will be searched for in the Hash Table.**/
	  public static void find(String key){
		int index = hash(key);
		int checkIndex = index;
		
		int singleSearchProbes =0;
		int i=0;
		int j=0;
		String[] parts;
		String dateTime;
		
		/*Search for the record corresponding to the given Date/Time string and 
		 *probe according to scheme specified by command line arguments.*/
		if (scheme.equals("Chaining")){
			LinkedList<String> l = arrChain[index];
			parts = l.get(0).split(", ");
			dateTime = parts[0];
			
			if (l.get(0)!=key){
				while ((i<l.size()) && (!dateTime.equals(key))){
					String[] partsTemp = l.get(i).split(", ");
					dateTime = partsTemp[0];
					singleSearchProbes++;
					totalSearchProbes++;
					i++;
				}
				if (singleSearchProbes > maxProbes) {maxProbes = singleSearchProbes;}
			}
		}else{
			parts = arrHash[checkIndex].split(", ");
			dateTime = parts[0];
			while (!dateTime.equals(key)){
				if (scheme.equals("Linear"))
					checkIndex= (index + j)%tblSize;
				if (scheme.equals("Quadratic"))
					checkIndex= (index + j<<1)%tblSize;
				if (arrHash[checkIndex]!=null){
					String partsTemp[] = arrHash[checkIndex].split(", ");
					dateTime = partsTemp[0];
				}
				singleSearchProbes++;
				totalSearchProbes++;
				j++;
			}
			if (singleSearchProbes > maxProbes) {maxProbes = singleSearchProbes;}
	  }
	}
}
