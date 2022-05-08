import java.math.BigInteger;

/* Java program for Merge Sort */
class PlaintextSorting
{
    // Merges two subarrays of arr[].
    // First subarray is arr[l..m]
    // Second subarray is arr[m+1..r]
    public static void Merge(BigInteger arr[], int l, int m, int r)
    {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;
 
        /* Create temp arrays */
        BigInteger L[] = new BigInteger [n1];
        BigInteger R[] = new BigInteger [n2];
 
        /*Copy data to temp arrays*/
        for (int i=0; i<n1; ++i)
            L[i] = arr[l + i];
        for (int j=0; j<n2; ++j)
            R[j] = arr[m + 1+ j];
 
 
        /* Merge the temp arrays */
 
        // Initial indexes of first and second subarrays
        int i = 0, j = 0;
 
        // Initial index of merged subarray array
        int k = l;
        while (i < n1 && j < n2)
        {
        	boolean comp;
        	try{
        		comp = (R[j].compareTo(L[i])==1);
        	}
        	catch (Exception e)
        	{
        		System.out.println("Cannot find Alice");
        		e.printStackTrace();
        		comp = false;
        	}
            if (comp)
            {
                arr[k] = L[i];
                i++;
            }
            else
            {
                arr[k] = R[j];
                j++;
            }
            k++;
        }
 
        /* Copy remaining elements of L[] if any */
        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
        }
 
        /* Copy remaining elements of R[] if any */
        while (j < n2)
        {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
    
    public static void MergeSort(BigInteger arr[], int l, int r)
    {
        if (l < r)
        {
        	System.out.println("MergeSorting: "+ r);
        	// Find the middle point
            int m = (l+r)/2;
 
            // Sort first and second halves
            MergeSort(arr, l, m);
            MergeSort(arr , m+1, r);
 
            // Merge the sorted halves
            Merge(arr, l, m, r);
        }
    }
    
    public static void MergeSort(BigInteger arr[]) {
    	MergeSort(arr, 0, arr.length-1);
    }

	// Driver code
	public static void main(String args[])
	{
		long startTime = System.currentTimeMillis();
		BigInteger[] data_array = csv.readDataLineByLine(8, 500);
		MergeSort(data_array);
		long endTime = System.currentTimeMillis();
		System.out.println("Sorting time:" + (endTime - startTime));
	}
}
