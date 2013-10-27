package ucl.hackathon.snoopchat;

import java.util.Random;


public abstract class SimpleEncryption {
	
	public int stringToInt(String inputString)
	{
		return Integer.parseInt("1234");
	}

	public int generateCode()
	{
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(9000) + 1000;
		
		return index;
	}
	
	public int getOffset(int codeWord)
	{
		int a,b,c,d;
		
		a = (codeWord - (codeWord % 1000)) / 1000;
		b = ((codeWord - (codeWord % 100)) % 1000) / 100;
		c = ((codeWord % 1000) % 100) - (codeWord % 10);
		d = codeWord % 10;
		
		return a + b + c + d;
	}
	
	public String encrypt(String unencryptedString, int codeWord)
	{
		int offset = getOffset(codeWord);
		
		StringBuilder stringBuilder = new StringBuilder(unencryptedString);
		
		for(int n=0;n<unencryptedString.length();n++)
		{
			stringBuilder.setCharAt(n, encryptChar(stringBuilder.charAt(n), offset));
		}
		
		return stringBuilder.toString();	
	}
	
	public String decrypt(String encryptedString, int codeWord)
	{
		int offset = getOffset(codeWord);
		
		StringBuilder stringBuilder = new StringBuilder(encryptedString);
		
		for(int n=0;n<encryptedString.length();n++)
		{
			stringBuilder.setCharAt(n, decryptChar(stringBuilder.charAt(n), offset));
		}
		
		return stringBuilder.toString();
	}
	
	public char encryptChar(char inputChar, int offset)
	{
		return intToAscii((asciiToInt(inputChar) + offset));
	}
	
	public char decryptChar(char inputChar, int offset)
	{
		return intToAscii((asciiToInt(inputChar) - offset));
	}
	
	public int asciiToInt(char asciiValue)
	{
		return (int) asciiValue;
	}
	
	public char intToAscii(int d)
	{
		return (char) d;
	}
	
}
