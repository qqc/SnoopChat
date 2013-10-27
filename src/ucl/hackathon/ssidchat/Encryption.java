package ucl.hackathon.ssidchat;

import java.util.Random;

public abstract class Encryption {
	
	// this method shouldn't be in this class, it's just here to show you how to use the functions
	// KEYS SHOULD ONLY BE GENERATED ONCE EACH TIME THE APP STARTS
	// ************
	public void generateKeys()
	{
		int myPrivateKey = 0;
		int myPublicKey = 0;
		int x, y;
		int xy = 0;
		int smallerxy = 1;
		
		while(myPrivateKey < 2 || myPrivateKey == smallerxy)
		{	
			x = generatePrime();
			y = generatePrime();
			
			xy = x*y;
			
			smallerxy = (x-1)*(y-1)+1;
			
			myPrivateKey = findLargestFactor(smallerxy);
		}
		
		myPublicKey = smallerxy/myPrivateKey;
		
		//encryptString("enter string here",myPublicKey,xy);
	}
	//**************
	
	public String encryptString(String unencryptedString, double theirPublicKey, double xy)
	{
		StringBuilder stringBuilder = new StringBuilder(unencryptedString);
		
		for(int n=0;n<unencryptedString.length();n++)
		{
			stringBuilder.setCharAt(n, encryptChar(stringBuilder.charAt(n), theirPublicKey, xy));
		}
		
		return stringBuilder.toString();
	}
	
	public String decryptString(String encryptedString, double myPrivateKey, double xy)
	{
		StringBuilder stringBuilder = new StringBuilder(encryptedString);
		
		for(int n=0;n<encryptedString.length();n++)
		{
			stringBuilder.setCharAt(n, decryptChar(stringBuilder.charAt(n), myPrivateKey, xy));
		}
		
		return stringBuilder.toString();
	}
	
	public int generatePrime()
	{
		Random randomGenerator = new Random();
		
		int[] primeArray = {13,17,19,23,29,31,37,41};
		int index = randomGenerator.nextInt(primeArray.length);
		
		return primeArray[index];
	}
	
	public int findLargestFactor(int n)
	{
		int largest = 0;
		
		for (int i = 1;i<n;i++)
            if ( n % i == 0 )
                if ( i > largest )
                    largest = i;
		
		return largest;
	}
	
	private double RSAEncryption(double input, double theirPublicKey, double xy)
	{
		return Math.pow(input,theirPublicKey) % xy;
	}
	
	private double RSADecryption(double input, double myPrivateKey, double xy)
	{
		return Math.pow(input,myPrivateKey) % xy;
	}
	
	private double asciiToInt(char asciiValue)
	{
		return Character.getNumericValue(asciiValue);
	}
	
	private char intToAscii(double d)
	{
		return (char) d;
	}
	
	private char encryptChar(char unencryptedValue, double theirPublicKey, double xy)
	{
		return intToAscii(RSAEncryption(asciiToInt(unencryptedValue), theirPublicKey, xy));
	}
	
	private char decryptChar(char encryptedValue, double myPrivateKey, double xy)
	{
		return intToAscii(RSADecryption(asciiToInt(encryptedValue), myPrivateKey, xy));
	}

}
