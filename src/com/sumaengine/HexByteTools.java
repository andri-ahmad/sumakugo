package com.sumaengine;

import java.math.BigInteger;

public class HexByteTools {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		coba4Byte();
		//coba1Byte();
		
		String hexaku = "D000001E";
		//int intaku = Integer.parseInt(hexaku, 16);
		long long_aku = Long.parseUnsignedLong(hexaku,16);
		System.out.println("Nilai Hex = " + hexaku);
		System.out.println("Nilai Dec = " + long_aku);

	}
	
	public static void coba1Byte() {
		
		short CAS_VER = 1;
        String CAS_VERHex = Integer.toHexString(CAS_VER);
        System.out.println("CAS_VER Decimal = " + CAS_VER);
        System.out.println("CAS_VER Hex Format = " + CAS_VERHex.toUpperCase());        
        
        if (CAS_VERHex.length() == 1) { CAS_VERHex = "0"+CAS_VERHex; }
        
        System.out.println("CAS_VER Hex Format = " + CAS_VERHex.toUpperCase());
        
        byte[] CAS_VER_bytes = hex2Byte(CAS_VERHex);
        System.out.println("CAS_VER length : " + CAS_VER_bytes.length + " bytes ");
        
        for (int i=0; i < CAS_VER_bytes.length; i++) {
        	System.out.println("Byte "+i+" = " +CAS_VER_bytes[i]);    
        }  
	}
	
	public static void coba4Byte() {
		//2. data body, 1 adalah card number
        BigInteger cardNumberbi = new BigInteger ("16");
        long cardNumberlong = cardNumberbi.longValue();
        String cardNumberHex = Long.toHexString(cardNumberlong);
        System.out.println("Card Number Decimal = " + cardNumberbi);
        System.out.println("Card Number Hex = " + cardNumberHex);
        
        /*
         * persiapan 4 Byte
         */
        if (cardNumberHex.length() == 8) {cardNumberHex = ""+cardNumberHex;}
        if (cardNumberHex.length() == 7) {cardNumberHex = "0"+cardNumberHex;}
        if (cardNumberHex.length() == 6) {cardNumberHex = "00"+cardNumberHex;}
        if (cardNumberHex.length() == 5) {cardNumberHex = "000"+cardNumberHex;}
        if (cardNumberHex.length() == 4) {cardNumberHex = "0000"+cardNumberHex;}
        if (cardNumberHex.length() == 3) {cardNumberHex = "00000"+cardNumberHex;}
        if (cardNumberHex.length() == 2) {cardNumberHex = "000000"+cardNumberHex;}
        if (cardNumberHex.length() == 1) {cardNumberHex = "0000000"+cardNumberHex;}
        
        System.out.println("Card Number Hex Format Final = 0x" + cardNumberHex);
        
        byte[] cardNumberBytes_Conf = hex2Byte(cardNumberHex);
        
        System.out.println("Card Number Byte length = " + cardNumberBytes_Conf.length + " byte ");
        
		    
    	for (int i=0; i < cardNumberBytes_Conf.length; i++) {        	
    		System.out.println("Byte "+i+" = " +cardNumberBytes_Conf[i]);    
        }
	}
	
	public static byte[] hex2Byte(String str)
    {
       byte[] bytes = new byte[str.length() / 2];
       for (int i = 0; i < bytes.length; i++)
       {
          bytes[i] = (byte) Integer
                .parseInt(str.substring(2 * i, 2 * i + 2), 16);
       }
       return bytes;
    }

public static String byte2hex(byte[] b)
    {

     // String Buffer can be used instead

       String hs = "";
       String stmp = "";

       for (int n = 0; n < b.length; n++)
       {
          stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));

          if (stmp.length() == 1)
          {
             hs = hs + "0" + stmp;
          }
          else
          {
             hs = hs + stmp;
          }

          if (n < b.length - 1)
          {
             hs = hs + "";
          }
       }

       return hs;
    }

public static void reverse(byte[] array) {
      if (array == null) {
          return;
      }
      int i = 0;
      int j = array.length - 1;
      byte tmp;
      while (j > i) {
          tmp = array[j];
          array[j] = array[i];
          array[i] = tmp;
          j--;
          i++;
      }
  }

}
