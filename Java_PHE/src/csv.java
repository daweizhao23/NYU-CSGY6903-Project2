import java.io.FileReader;
import com.opencsv.CSVReader;
import java.math.BigInteger;

public class csv {
// Java code to illustrate reading a
	static String file = "../data/healthcare-dataset-stroke-data_cleaned.csv";
	private static final int DATA_SIZE = 10; //4888 
	public static BigInteger[] readDataLineByLine(int col)
	{
		BigInteger[] array = new BigInteger[DATA_SIZE];
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
	        while ((nextRecord = csvReader.readNext()) != null && i < DATA_SIZE) {
	        	int int_val = (int) Double.parseDouble(nextRecord[col]);
	        	array[i] = BigInteger.valueOf(int_val);
	        	i = i + 1;
	        }
	        
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
		return array;
	    
	}
}