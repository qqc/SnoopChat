package ucl.hackathon.ssidchat;

public abstract class Encryption {
	
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
