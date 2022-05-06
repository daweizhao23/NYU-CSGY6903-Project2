import java.io.FileReader;
import com.opencsv.CSVReader;

public class csv {
// Java code to illustrate reading a
	static String file = "../data/healthcare-dataset-stroke-data.csv";
	
	public static int[] readDataLineByLine(int col)
	{
		int[] array = new int[5110];
	    try {
	    	
	        // Create an object of filereader
	        // class with CSV file as a parameter.
	        FileReader filereader = new FileReader(file);
	 
	        // create csvReader object passing
	        // file reader as a parameter
	        CSVReader csvReader = new CSVReader(filereader);
	        String[] nextRecord;
	 
	        // we are going to read data line by line
	        nextRecord = csvReader.readNext();
	        int i = 0;
	        while ((nextRecord = csvReader.readNext()) != null) {
	        	array[i] = (int) Double.parseDouble(nextRecord[col]);
	        	i = i + 1;
	        }
	        
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
		return array;
	    
	}
}