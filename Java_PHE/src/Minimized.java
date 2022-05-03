import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.Security;
import java.security.SignatureException;
import java.util.ArrayList;
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
				System.out.println("Pallier Key Received");
				pubKey = aClient.getDGKPublicKey();
				
				System.out.println("DGK Key Received");
				aClient.setDGKMode(false);
				System.out.println("DGK Mode: " + aClient.isDGK());
				
				BigInteger pCipher1 = PaillierCipher.encrypt(new BigInteger("10"), pk);
				System.out.println("Ciphertext 1 Set");
				BigInteger pCipher2 = PaillierCipher.encrypt(new BigInteger("3"), pk);
				System.out.println("Ciphertext 2 Set. Encrypted: " + pCipher2);
				
				
				//BigInteger pCipherMult = aClient.multiplication(pCipher1, pCipher2);
				
				//System.out.println("Encrypted Cipher Multiplication: " + pCipherMult);
				
				boolean pCipherGreater = aClient.Protocol4(pCipher1, pCipher2);
				
				System.out.println("Ciphertext 1 greater than Ciphertext 2? " + pCipherGreater);
				
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
				bServer.Protocol4();
				
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
