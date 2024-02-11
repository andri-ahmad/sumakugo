package com.sumaengine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class SumaProcessor {
	
	private String fileName = "bom.properties";
	private Properties bomProperties = new Properties();
	private static String sumaServer = "";
	private static String sumaPort = "";
	private static String CAS_Version = "";
	static Logger log = Logger.getLogger(SumaProcessor.class.getName());
		
	
	public SumaProcessor() {
		
	}
	
	
	public void openAccount() {
		
	}
	
	public void setAuth() {
		
	}
	
	public void loadProperties () throws FileNotFoundException, IOException {

		ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        this.bomProperties.load(inputStream);
        
       // nusantara.myURL = this.bomProperties.getProperty("URL").toString();
        // nusantara.myURLgetPaket = this.bomProperties.getProperty("URLGETPAKET").toString();
        
		//printOutput.printToShell( "Found URL = " + nusantara.myURL, "");
		//printOutput.printToShell( "Found URL = " + nusantara.myURLgetPaket, "");
        
        
	}
	
	
	public static byte[] hex2Byte(String str) {
       byte[] bytes = new byte[str.length() / 2];
       for (int i = 0; i < bytes.length; i++)
       {
          bytes[i] = (byte) Integer
                .parseInt(str.substring(2 * i, 2 * i + 2), 16);
       }
       return bytes;
    }

	public static String byte2hex(byte[] b) {

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
