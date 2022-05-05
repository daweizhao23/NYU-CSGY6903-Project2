import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.Security;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import security.DGK.DGKKeyPairGenerator;
import security.DGK.DGKOperations;
import security.DGK.DGKPrivateKey;
import security.DGK.DGKProvider;
import security.DGK.DGKPublicKey;
import security.DGK.DGKSignature;
import security.elgamal.ElGamalKeyPairGenerator;
import security.elgamal.ElGamalPrivateKey;
import security.elgamal.ElGamalProvider;
import security.elgamal.ElGamalCipher;
import security.elgamal.ElGamalPublicKey;
import security.elgamal.ElGamalSignature;
import security.elgamal.ElGamal_Ciphertext;
import security.generic.NTL;
import security.gm.GMCipher;
import security.gm.GMKeyPairGenerator;
import security.gm.GMPrivateKey;
import security.gm.GMPublicKey;
import security.paillier.PaillierCipher;
import security.paillier.PaillierKeyPairGenerator;
import security.paillier.PaillierPrivateKey;
import security.paillier.PaillierPublicKey;
import security.paillier.PaillierSignature;
import security.paillier.PaillierProvider;
import security.socialistmillionaire.alice;
import security.socialistmillionaire.bob;
import security.socialistmillionaire.socialist_millionaires;

public class Minimized 
{
	private static boolean isAlice = false;
	
	private static PaillierPublicKey pk;
	private static PaillierPrivateKey sk;
	
	private static DGKPublicKey pubKey;
	private static DGKPrivateKey privKey;
	
	// Initialize Alice and Bob
	private static ServerSocket bob_socket = null;
	private static Socket bob_client = null;
	private static bob bServer = null;
	private static alice aClient = null;
	
	// Get your test data...
	private static BigInteger [] low = generate_low();
	private static BigInteger [] mid = generate_mid();
	private static BigInteger [] high = generate_high();
	
	private static final int TEST = 100;
	private static final int SIZE = 100;
	private static final int KEY_SIZE = 1024;
	private static final int BILLION = BigInteger.TEN.pow(9).intValue();
	
	public static void main(String [] args)
	{
		Security.addProvider(new DGKProvider());
		Security.addProvider(new PaillierProvider());

		if (args.length != 0)
		{
			System.out.println("Alice mode activated...");
			isAlice = true;
		}
		
		try
		{
			// DO NOT USE ASSERT WHEN CONDUCTING THE TESTS!!!!
			if (isAlice)
			{
				// I need to ensure that Alice has same Keys as Bob! and initialize as well
				aClient = new alice(new Socket("0.0.0.0", 9254));
				System.out.println("Alice Socket Set");
				
				// TO BE CONSISTENT I NEED TO USE KEYS FROM BOB!
				pk = aClient.getPaillierPublicKey();
				sk = aClient.getPaillierPrivateKey();
				System.out.println("Pallier Key Received");
				
				/*
				pubKey = aClient.getDGKPublicKey();
				privKey = aClient.getDGKPrivateKey();
				
				System.out.println("DGK Key Received");
				*/
				aClient.setDGKMode(false);
				System.out.println("DGK Mode: " + aClient.isDGK());
				
				/*
				BigInteger pCipher1 = PaillierCipher.encrypt(new BigInteger("10"), pk);
				System.out.println("Ciphertext 1 Set");
				BigInteger pCipher2 = PaillierCipher.encrypt(new BigInteger("30"), pk);
				System.out.println("Ciphertext 2 Set. Encrypted: " + pCipher2);
				*/
				
				// USE AN EVEN NUMBER OF ELEMENTS, otherwise Bob's part doesn't work right.
				BigInteger pCiphers[] = { PaillierCipher.encrypt(new BigInteger("2"), pk), PaillierCipher.encrypt(new BigInteger("30"), pk), PaillierCipher.encrypt(new BigInteger("13"), pk), PaillierCipher.encrypt(new BigInteger("9"), pk), PaillierCipher.encrypt(new BigInteger("134"), pk), PaillierCipher.encrypt(new BigInteger("10"), pk) };
				
				
				//BigInteger pCipherMult = aClient.multiplication(pCipher1, pCipher2);
				
				//System.out.println("Encrypted Cipher Multiplication: " + pCipherMult);
				
				/*
				boolean pCipherGreater = aClient.Protocol4(pCipher1, pCipher2);
				
				System.out.println("Ciphertext 1 greater than Ciphertext 2? " + pCipherGreater);
				*/
				
				System.out.println("Encrypted array before sort:");
				for (int i = 0; i < pCiphers.length; i++) {
					System.out.println(pCiphers[i]);
				}
				
				PaillierMergeSort(pCiphers);
				
				System.out.println("Decrypted array after sort:");
				for (int i = 0; i < pCiphers.length; i++) {
					System.out.println(PaillierCipher.decrypt(pCiphers[i], sk));
				}
				
				System.exit(0);
				
			}
			else
			{
				// Build DGK Keys
				DGKKeyPairGenerator gen = new DGKKeyPairGenerator(16, 160, 1024);
				gen.initialize(KEY_SIZE, null);
				KeyPair DGK = gen.generateKeyPair();
				pubKey = (DGKPublicKey) DGK.getPublic();
				privKey = (DGKPrivateKey) DGK.getPrivate();
				
				// Build Paillier Keys
				PaillierKeyPairGenerator p = new PaillierKeyPairGenerator();
				p.initialize(KEY_SIZE, null);
				KeyPair pe = p.generateKeyPair();
				pk = (PaillierPublicKey) pe.getPublic();
				sk = (PaillierPrivateKey) pe.getPrivate();
				
				
				bob_socket = new ServerSocket(9254);
				System.out.println("Bob is ready on " + bob_socket.getLocalSocketAddress() + ":" + bob_socket.getLocalPort());
				
				bob_client = bob_socket.accept();
				bServer = new bob(bob_client, pe, DGK);
				
				bServer.setDGKMode(false);
				//bServer.multiplication();
				//bServer.Protocol4();
				
				// Support Code for Bob's comparisons during Mergesort. BobMerge should use the same number of elements as pCiphers contains!!!
				for (int i = 0; i < BobMerge(6); i++) {
					bServer.Protocol4();
				}
				
				
			}
		}
		catch (IOException | ClassNotFoundException x)
		{
			x.printStackTrace();
		}
		catch(IllegalArgumentException o)
		{
			System.out.println("LIBRARY THREW ERROR ON RUNTIME!");
			o.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(!isAlice)
			{
				try 
				{
					if(bob_client != null)
					{
						bob_client.close();
					}
					if(bob_socket != null)
					{
						bob_socket.close();
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
    // Merges two subarrays of arr[].
    // First subarray is arr[l..m]
    // Second subarray is arr[m+1..r]
    public static void PaillierMerge(BigInteger arr[], int l, int m, int r)
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
        		comp = aClient.Protocol4(R[j], L[i]);
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
    
    public static void PaillierMergeSort(BigInteger arr[], int l, int r)
    {
        if (l < r)
        {
            // Find the middle point
            int m = (l+r)/2;
 
            // Sort first and second halves
            PaillierMergeSort(arr, l, m);
            PaillierMergeSort(arr , m+1, r);
 
            // Merge the sorted halves
            PaillierMerge(arr, l, m, r);
        }
    }
    
    public static void PaillierMergeSort(BigInteger arr[]) {
    	PaillierMergeSort(arr, 0, arr.length-1);
    }
    
    public static int BobMerge(int i) {
    	if (i == 1) {
    		return 0;
    	}else {
    		return 2*BobMerge(i/2) + i;
    	}
    }
    
	
    
	
	//---------------------Generate numbers-----------------------------------
	// Original low
	public static BigInteger [] generate_low()
	{
		BigInteger [] test_set = new BigInteger[16];
		test_set[0] = new BigInteger("1");
		test_set[1] = new BigInteger("2");
		test_set[2] = new BigInteger("4");
		test_set[3] = new BigInteger("8");
		test_set[4] = new BigInteger("16");
		test_set[5] = new BigInteger("32");
		test_set[6] = new BigInteger("64");
		test_set[7] = new BigInteger("128");
		test_set[8] = new BigInteger("256");
		test_set[9] = new BigInteger("512");
		
		test_set[10] = new BigInteger("1024");
		test_set[11] = new BigInteger("2048");
		test_set[12] = new BigInteger("4096");
		test_set[13] = new BigInteger("8192");
		test_set[14] = new BigInteger("16384");
		test_set[15] = new BigInteger("32768");
		
		BigInteger t = BigInteger.ZERO;
		for (int i = 0; i < test_set.length;i++)
		{
			test_set[i] = test_set[i].add(t);
		}
		return test_set;
	}
	
	// Original Medium
	public static BigInteger[] generate_mid()
	{
		BigInteger [] test_set = new BigInteger[16];
		test_set[0] = new BigInteger("1");
		test_set[1] = new BigInteger("2");
		test_set[2] = new BigInteger("4");
		test_set[3] = new BigInteger("8");
		test_set[4] = new BigInteger("16");
		test_set[5] = new BigInteger("32");
		test_set[6] = new BigInteger("64");
		test_set[7] = new BigInteger("128");
		test_set[8] = new BigInteger("256");
		test_set[9] = new BigInteger("512");
		
		test_set[10] = new BigInteger("1024");
		test_set[11] = new BigInteger("2048");
		test_set[12] = new BigInteger("4096");
		test_set[13] = new BigInteger("8192");
		test_set[14] = new BigInteger("16384");
		test_set[15] = new BigInteger("32768");
		
		BigInteger t = new BigInteger("5");
		for (int i = 0; i < test_set.length; i++)
		{
			test_set[i] = test_set[i].add(t);
		}
		return test_set;
	}
	
	// Original High
	public static BigInteger[] generate_high()
	{
		BigInteger [] test_set = new BigInteger[16];
		
		test_set[0] = new BigInteger("1");
		test_set[1] = new BigInteger("2");
		test_set[2] = new BigInteger("4");
		test_set[3] = new BigInteger("8");
		test_set[4] = new BigInteger("16");
		test_set[5] = new BigInteger("32");
		test_set[6] = new BigInteger("64");
		test_set[7] = new BigInteger("128");
		test_set[8] = new BigInteger("256");
		test_set[9] = new BigInteger("512");
		
		test_set[10] = new BigInteger("1024");
		test_set[11] = new BigInteger("2048");
		test_set[12] = new BigInteger("4096");
		test_set[13] = new BigInteger("8192");
		test_set[14] = new BigInteger("16384");
		test_set[15] = new BigInteger("32768");
		
		BigInteger t = new BigInteger("10");
		for (int i = 0; i < test_set.length; i++)
		{
			test_set[i] = test_set[i].add(t);
		}
		return test_set;
	}
}
