package com.sumaengine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class SumaProcessor {
	
	private String fileName = "properties/suma.properties";
	private Properties sumaProperties = new Properties();
	private static String sumaServer = "";
	private static int sumaPort = 0;
	private static String CAS_Version = "";
	static Logger log = Logger.getLogger(SumaProcessor.class.getName());
	private static short COMMAND_TYPE_INQUIRY = 32;
	private static short COMMAND_TYPE_CLOSE_ACCOUNT =21;
	private static short COMMAND_TYPE_CLOSE_ACCOUNT_LEN = 9;
	private static short COMMAND_TYPE_OPEN_ACCOUNT = 33;
	private static short COMMAND_TYPE_OPEN_ACCOUNT_LEN = 9;
	private static short COMMAND_TYPE_AUTH = 1;
	private static short COMMAND_TYPE_AUTH_LEN = 18;
	
	
		
	
	public SumaProcessor() {
		
	}
	
	public SumaResponse setAuth(String sessionIdParam, String cardNumber, String productId, String productName, Date beginTime, Date endTime) {
		SumaResponse aSumaResponse = new SumaResponse();
		
		sumaServer = this.sumaProperties.getProperty("SumaServer").toString();                
        sumaPort = Integer.valueOf(this.sumaProperties.getProperty("SumaPort").toString());
        CAS_Version = this.sumaProperties.getProperty("SumaCASVersion").toString();
        
        try {
           //log.info("Connecting to " + sumaServer + " on port " + sumaPort);
           Socket client = new Socket(sumaServer, sumaPort);
           
           //log.info("Just connected to " + client.getRemoteSocketAddress());
           
           OutputStream outToServer = client.getOutputStream();
           //log.info("Output stream get From Server..");
                  
           DataOutputStream out = new DataOutputStream(outToServer);
           //log.info("Output stream created from local to server..");
           
           //log.info("Try to communicate to with localsocket : " + client.getLocalSocketAddress());
           
           
            //log.info("Try writing to the stream..");
            try {
                
                // 1.Message Header // 1.1 Session ID, 2 Byte, maksium 65536, int 
                int sessionId = Integer.valueOf(sessionIdParam.substring(0,sessionIdParam.length()-2));
                String sessionHex = Integer.toHexString(sessionId);                
                if (sessionHex.length() == 3) { sessionHex = "0"+sessionHex; }
                if (sessionHex.length() == 2) { sessionHex = "00"+sessionHex; }
                if (sessionHex.length() == 1) { sessionHex = "000"+sessionHex; }                
                //log.info("Session ID Hex Format Final = 0x" + sessionHex.toUpperCase());                
                byte[] sessionid_bytes = hex2Byte(sessionHex);                
                

                // 1.Message Header   // 1.2 CAS_VER, 1 Byte, maksium 256, short 
                short CAS_VER = Short.valueOf(CAS_Version);
                String CAS_VERHex = Integer.toHexString(CAS_VER);                
                if (CAS_VERHex.length() == 1) { CAS_VERHex = "0"+CAS_VERHex; }                
                //log.info("CAS_VER Hex Format = 0x" + CAS_VERHex.toUpperCase());                
                byte[] CAS_VER_bytes = hex2Byte(CAS_VERHex);
                

                // 1.Message Header   // 1.3 command type, 1 Byte 
                short commandType = COMMAND_TYPE_AUTH;
                String commandTypeHex = Integer.toHexString(commandType);                
                if (commandTypeHex.length() == 1) { commandTypeHex = "0"+commandTypeHex; }                
                //log.info("commandType Open Auth Hex Format = 0x" + commandTypeHex.toUpperCase());                
                byte[] commandTypeBytes = hex2Byte(commandTypeHex);                               
                
                
                // 1.Message Header  // 1.4 Data length, 2 Byte  
                int data_length = COMMAND_TYPE_AUTH_LEN;
                String data_length_hex = Integer.toHexString(data_length);                
                if (data_length_hex.length() == 1) { data_length_hex = "000"+data_length_hex;}
                if (data_length_hex.length() == 2) { data_length_hex = "00"+data_length_hex;}
                if (data_length_hex.length() == 3) { data_length_hex = "0"+data_length_hex;}                
                //log.info("data_length Hex Format = 0x" + data_length_hex.toUpperCase());                
                byte[] data_length_bytes = hex2Byte(data_length_hex);                    
                
                
                /*
                 * Databody untuk Open Account, Total 9 Byte
                 * 4 byte Card id 
                 * 1 byte product amount, value = 1, karena 1 1 proses nya
                 * 1 byte send_or_not, default 1
                 * 1 byte tapingCtrl, default 0
                 * 2 byte product id   
                 * 4 byte begin time
                 * 4 byte end time 
                 * 1 byte desc, default 0
                 */
                
                // Databoyd cardNumber
                BigInteger cardNumberbi = new BigInteger (cardNumber);
                long cardNumberlong = cardNumberbi.longValue();
                String cardNumberHex = Long.toHexString(cardNumberlong);                
                if (cardNumberHex.length() == 7) {cardNumberHex = "0"+cardNumberHex;}
                if (cardNumberHex.length() == 6) {cardNumberHex = "00"+cardNumberHex;}
                if (cardNumberHex.length() == 5) {cardNumberHex = "000"+cardNumberHex;}
                if (cardNumberHex.length() == 4) {cardNumberHex = "0000"+cardNumberHex;}
                if (cardNumberHex.length() == 3) {cardNumberHex = "00000"+cardNumberHex;}
                if (cardNumberHex.length() == 2) {cardNumberHex = "000000"+cardNumberHex;}
                if (cardNumberHex.length() == 1) {cardNumberHex = "0000000"+cardNumberHex;}                
                //log.info("Card Number Hex Format Final = 0x" + cardNumberHex.toUpperCase());                
                byte[] cardNumberBytes_Conf = hex2Byte(cardNumberHex);                
                
                // jumlah product
                short jml_product = 1;
                String jml_productHex = Integer.toHexString(jml_product);                
                if (jml_productHex.length() == 1) { jml_productHex = "0"+jml_productHex; }                
                //log.info("jml_productHex Hex Format = 0x" + jml_productHex.toUpperCase());                
                byte[] jml_productBytes = hex2Byte(jml_productHex);
                  
                // send or not
                short send_or_not = 1;
                String send_or_notHex = Integer.toHexString(send_or_not);                
                if (send_or_notHex.length() == 1) { send_or_notHex = "0"+send_or_notHex; }                
                //log.info("send_or_notHex Hex Format = 0x" + send_or_notHex.toUpperCase());                
                byte[] send_or_notBytes = hex2Byte(send_or_notHex);
                
                //tappingCtrl
                short tappingCtrl = 0;
                String tappingCtrlHex = Integer.toHexString(tappingCtrl);
                if (tappingCtrlHex.length() ==1) { tappingCtrlHex = "0"+tappingCtrlHex;}                
                //log.info("tappingCtrlHex Hex Format = 0x" + tappingCtrlHex.toUpperCase());                
                byte[] tappingCtrlBytes = hex2Byte(tappingCtrlHex);
                
                // product ID                  
                String productIDHex = Integer.toHexString(Integer.valueOf(productId));                
                if (productIDHex.length() == 1) { productIDHex = "000"+productIDHex;}
                if (productIDHex.length() == 2) { productIDHex = "00"+productIDHex;}
                if (productIDHex.length() == 3) { productIDHex = "0"+productIDHex;}                
                //log.info("productIDHex Hex Format = 0x" + productIDHex.toUpperCase());                
                byte[] productIDBytes = hex2Byte(productIDHex); 
                
                // Begin time 
                long beginTimeMilis = beginTime.getTime();
                String beginTimeHex = Long.toHexString(beginTimeMilis/1000L);
                //log.info("beginTimeHex = 0x" + beginTimeHex.toUpperCase());                
                byte[] beginTimeBytes = hex2Byte(beginTimeHex);
                
                // end time
                long endTimeMilis = endTime.getTime();
                String endTimeHex = Long.toHexString(endTimeMilis/1000L);
                //log.info("endTimeHex = 0x" + endTimeHex.toUpperCase());                
                byte[] endTimeBytes = hex2Byte(endTimeHex);                
                
                // Desc
                short desc1 = 0;
                String desc1Hex = Integer.toHexString(desc1);
                if (desc1Hex.length() ==1) { desc1Hex = "0"+desc1Hex;}                
                //log.info("desc1Hex Hex Format = 0x" + desc1Hex.toUpperCase());                
                byte[] desc1Bytes = hex2Byte(desc1Hex);
                
                
                /*
                 * Congcat all datasent to Stream
                 */               
                ByteArrayOutputStream baos = new ByteArrayOutputStream();                
                baos.write(sessionid_bytes);
                baos.write(CAS_VER_bytes);
                baos.write(commandTypeBytes);
                baos.write(data_length_bytes);                
                baos.write(cardNumberBytes_Conf);
                baos.write(jml_productBytes);
                baos.write(send_or_notBytes);
                baos.write(tappingCtrlBytes);
                baos.write(productIDBytes);
                baos.write(beginTimeBytes);
                baos.write(endTimeBytes);
                baos.write(desc1Bytes); 
           
                         
                byte[] finalMessage = baos.toByteArray();
                //log.info("Final message, Length : " + finalMessage.length + " Byte");
                
                String finalMessageHexa = byte2hex(finalMessage);
                log.info("Final Message, content: " + "0x"+finalMessageHexa.toUpperCase());
             
                out.write(finalMessage);
                //log.info("Message sent to CAS");
                
            } catch (IOException e) {

                log.error("Writing failure,  Exception from server : ");
                e.printStackTrace();
            }                 
                      
           InputStream inFromServer = client.getInputStream();
           //log.info("Input stream get From Server..");
            
           DataInputStream in = new DataInputStream(inFromServer);
           //log.info("Input stream created..");
           //log.info("Reading input streaam....");
            
            try {
                                  
                byte[] BufferMsg = new byte[63];   // 1Kb

                in.read(BufferMsg);
                log.info("Server response , LENGTH " + BufferMsg.length  + ", Mesage = " +  "0x"+byte2hex(BufferMsg).toUpperCase() );
                
                /*
                 * Processing response data
                 */
                if (BufferMsg.length != 0) {
                               	
                	//Get Header Response
                	byte[] sessionResp = new byte[2];
                	byte[] cas_VerResp = new byte[1];
                	byte[] cmdTpyeResp = new byte[1];
                	byte[] lengthResp = new byte[2];
                	
                	// Ambil header session id
                	sessionResp[0] = BufferMsg[0];
                	sessionResp[1] = BufferMsg[1];
                	
                	// Ambil header cas_ver & cmdType
                	cas_VerResp[0] = BufferMsg[2];
                	cmdTpyeResp[0] = BufferMsg[3];
                	
                	// Ambil header length response nya.
                	lengthResp[0] = BufferMsg[4];
                	lengthResp[1] = BufferMsg[5];
                	
                	String lengthRespHex = byte2hex(lengthResp);
                	int lengthRespint = Integer.parseInt(lengthRespHex.toUpperCase(),16);                	
                	//log.info("Found response length = " + lengthRespint);
                	
                	// Ambil response data sesuai length diatas.
                	byte[] responseData = new byte[lengthRespint];                	
                	for (int i=0; i < lengthRespint ; i++ ) {                		
                		responseData[i] = BufferMsg[6+i];                		
                	}
                	
                	log.info("Response data = 0x" + byte2hex(responseData).toUpperCase());
                	
                	/*
                	 * Response data berbeda tiap command
                	 * Command open Auth sbb, input nya ada array byte[]
                	 * 4 byte = result 
                	 * 1 Byte = return amount = quantiti auth data returned
                	 * 2 byte = product number 
                	 * 1 byte = desc 
                	 * looping dari i=1 to return amount
                	 */
                	byte[] respResult = new byte[4];
                	
                	// Ambil 4 byte response result
                	respResult[0] = responseData[0];
                	respResult[1] = responseData[1];
                	respResult[2] = responseData[2];
                	respResult[3] = responseData[3]; 	
                	String resultHex = byte2hex(respResult);                	
                	long resultInt = Long.parseLong(resultHex.toUpperCase(), 16);
                	
                	aSumaResponse.setIdSuma(sessionIdParam);
                	aSumaResponse.setResponse("0x"+resultHex.toUpperCase());
                	
                	if (resultInt == 0 ) {
                		aSumaResponse.setResponseStatus("1");
                		log.info("Summary; Authorization: "+ cardNumber +"; "+ productName+"; "+ productId +"; Success; 0x" + resultHex.toUpperCase());
                		
                	} else {
                		aSumaResponse.setResponseStatus("-1");
                		log.info("Summary; Authorization: "+ cardNumber +"; "+ productName+"; "+ productId +"; Not Success; 0x" + resultHex.toUpperCase());
                		
                	}               	
                	
                } else {
                	log.error("Response message is 0, problem with socket connection, pls restart the engine " );
                }
                
                in.close();
                     
            } catch (IOException e2) {
                log.error("Reading Failure, Exception from server : " + e2.toString());
                e2.printStackTrace();
            }
           
          //log.info("Closing stream..");
           
          client.close();
            
          //log.info("Closed");
            
        } catch (IOException e) {
            log.error("Exception from server : " + e.toString());
           e.printStackTrace();
        }
		
		
		return aSumaResponse;
	}
	
	
	
	public SumaResponse openAccount(String sessionIdParam, String cardNumber) {
		
		SumaResponse aSumaResponse = new SumaResponse();
		
        sumaServer = this.sumaProperties.getProperty("SumaServer").toString();                
        sumaPort = Integer.valueOf(this.sumaProperties.getProperty("SumaPort").toString());
        CAS_Version = this.sumaProperties.getProperty("SumaCASVersion").toString();
        
        try {
           //log.info("Connecting to " + sumaServer + " on port " + sumaPort);
           Socket client = new Socket(sumaServer, sumaPort);
           
           //log.info("Just connected to " + client.getRemoteSocketAddress());
           
           OutputStream outToServer = client.getOutputStream();
           //log.info("Output stream get From Server..");
                  
           DataOutputStream out = new DataOutputStream(outToServer);
           //log.info("Output stream created from local to server..");
           
           //log.info("Try to communicate to with localsocket : " + client.getLocalSocketAddress());
           
           
            //log.info("Try writing to the stream..");
            try {
                
                // 1.Message Header // 1.1 Session ID, 2 Byte, maksium 65536, int 
                int sessionId = Integer.valueOf(sessionIdParam.substring(0,sessionIdParam.length()-2));
                String sessionHex = Integer.toHexString(sessionId);                
                if (sessionHex.length() == 3) { sessionHex = "0"+sessionHex; }
                if (sessionHex.length() == 2) { sessionHex = "00"+sessionHex; }
                if (sessionHex.length() == 1) { sessionHex = "000"+sessionHex; }                
                //log.info("Session ID Hex Format Final = 0x" + sessionHex.toUpperCase());                
                byte[] sessionid_bytes = hex2Byte(sessionHex);                
                

                // 1.Message Header   // 1.2 CAS_VER, 1 Byte, maksium 256, short 
                short CAS_VER = Short.valueOf(CAS_Version);
                String CAS_VERHex = Integer.toHexString(CAS_VER);                
                if (CAS_VERHex.length() == 1) { CAS_VERHex = "0"+CAS_VERHex; }                
                //log.info("CAS_VER Hex Format = 0x" + CAS_VERHex.toUpperCase());                
                byte[] CAS_VER_bytes = hex2Byte(CAS_VERHex);
                

                // 1.Message Header   // 1.3 command type, 1 Byte 
                short commandType = COMMAND_TYPE_OPEN_ACCOUNT;
                String commandTypeHex = Integer.toHexString(commandType);                
                if (commandTypeHex.length() == 1) { commandTypeHex = "0"+commandTypeHex; }                
                //log.info("commandType OPEN ACCOUNT Hex Format = 0x" + commandTypeHex.toUpperCase());                
                byte[] commandTypeBytes = hex2Byte(commandTypeHex);                               
                
                
                // 1.Message Header  // 1.4 Data length, 2 Byte  
                int data_length = COMMAND_TYPE_OPEN_ACCOUNT_LEN;
                String data_length_hex = Integer.toHexString(data_length);                
                if (data_length_hex.length() == 1) { data_length_hex = "000"+data_length_hex;}
                if (data_length_hex.length() == 2) { data_length_hex = "00"+data_length_hex;}
                if (data_length_hex.length() == 3) { data_length_hex = "0"+data_length_hex;}                
                //log.info("data_length Hex Format = 0x" + data_length_hex.toUpperCase());                
                byte[] data_length_bytes = hex2Byte(data_length_hex);                    
                
                
                /*
                 * Databody untuk Open Account, Total 9 Byte
                 * 4 byte Card id 
                 * 4 byte expiration time
                 * 1 byte is_send, vaule = 1
                 */
                
                // Databoyd cardNumber
                BigInteger cardNumberbi = new BigInteger (cardNumber);
                long cardNumberlong = cardNumberbi.longValue();
                String cardNumberHex = Long.toHexString(cardNumberlong);                
                if (cardNumberHex.length() == 7) {cardNumberHex = "0"+cardNumberHex;}
                if (cardNumberHex.length() == 6) {cardNumberHex = "00"+cardNumberHex;}
                if (cardNumberHex.length() == 5) {cardNumberHex = "000"+cardNumberHex;}
                if (cardNumberHex.length() == 4) {cardNumberHex = "0000"+cardNumberHex;}
                if (cardNumberHex.length() == 3) {cardNumberHex = "00000"+cardNumberHex;}
                if (cardNumberHex.length() == 2) {cardNumberHex = "000000"+cardNumberHex;}
                if (cardNumberHex.length() == 1) {cardNumberHex = "0000000"+cardNumberHex;}                
                //log.info("Card Number Hex Format Final = 0x" + cardNumberHex.toUpperCase());                
                byte[] cardNumberBytes_Conf = hex2Byte(cardNumberHex);                
                                
                // Databody Expired_Time: expired time of close account command. Recommendation: expired time = send time+ 1 day.
                long timeSaatini = System.currentTimeMillis();
                int timeBesok = 86400 + (int)(timeSaatini/1000L);
                String timeBesokHex = Long.toHexString(timeBesok);
                //log.info("timeBesokHex = 0x" + timeBesokHex.toUpperCase());                
                byte[] timeBesokBytes = hex2Byte(timeBesokHex);
                
                // Databody is_send parameter 1 Byte
                short is_send = 1;
                String is_sendHex = Integer.toHexString(is_send);                
                if (is_sendHex.length() == 1) { is_sendHex = "0"+is_sendHex; }                
                //log.info("is_sendHex Hex Format = 0x" + is_sendHex.toUpperCase());                
                byte[] is_sendHexBytes = hex2Byte(is_sendHex);  
                
                /*
                 * Congcat all datasent to Stream
                 */               
                ByteArrayOutputStream baos = new ByteArrayOutputStream();                
                baos.write(sessionid_bytes);
                baos.write(CAS_VER_bytes);
                baos.write(commandTypeBytes);
                baos.write(data_length_bytes);                
                baos.write(cardNumberBytes_Conf);
                baos.write(timeBesokBytes);
                baos.write(is_sendHexBytes);
                         
                byte[] finalMessage = baos.toByteArray();
                //log.info("Final message, Length : " + finalMessage.length + " Byte");
                
                String finalMessageHexa = byte2hex(finalMessage);
                log.info("Final Message, content: " + "0x"+finalMessageHexa.toUpperCase());
             
                out.write(finalMessage);
                //log.info("Message sent to CAS");
                
            } catch (IOException e) {

                log.error("Writing failure,  Exception from server : ");
                e.printStackTrace();
            }                 
                      
           InputStream inFromServer = client.getInputStream();
           //log.info("Input stream get From Server..");
            
           DataInputStream in = new DataInputStream(inFromServer);
           //log.info("Input stream created..");
           //log.info("Reading input streaam....");
            
            try {
                                  
                byte[] BufferMsg = new byte[63];   // 1Kb

                in.read(BufferMsg);
                log.info("Server response , LENGTH " + BufferMsg.length  + ", Mesage = " +  "0x"+byte2hex(BufferMsg).toUpperCase() );
                
                /*
                 * Processing response data
                 */
                if (BufferMsg.length != 0) {
                               	
                	//Get Header Response
                	byte[] sessionResp = new byte[2];
                	byte[] cas_VerResp = new byte[1];
                	byte[] cmdTpyeResp = new byte[1];
                	byte[] lengthResp = new byte[2];
                	
                	// Ambil header session id
                	sessionResp[0] = BufferMsg[0];
                	sessionResp[1] = BufferMsg[1];
                	
                	// Ambil header cas_ver & cmdType
                	cas_VerResp[0] = BufferMsg[2];
                	cmdTpyeResp[0] = BufferMsg[3];
                	
                	// Ambil header length response nya.
                	lengthResp[0] = BufferMsg[4];
                	lengthResp[1] = BufferMsg[5];
                	
                	String lengthRespHex = byte2hex(lengthResp);
                	int lengthRespint = Integer.parseInt(lengthRespHex.toUpperCase(),16);                	
                	log.info("Found response length = " + lengthRespint);
                	
                	// Ambil response data sesuai length diatas.
                	byte[] responseData = new byte[lengthRespint];                	
                	for (int i=0; i < lengthRespint ; i++ ) {                		
                		responseData[i] = BufferMsg[6+i];                		
                	}
                	log.info("Response data = 0x" + byte2hex(responseData).toUpperCase());
                	
                	/*
                	 * Response data berbeda tiap command
                	 * Command inquiry sbb, input nya ada array byte[]
                	 * 4 byte = result 
                	 * 1 byte = Quantity of EMM packet returned
                	 * for(I=0;I<N;I++) N=Emm_Count
                	 * 1 byte Emm_Len 
                	 * variable byte Emm_Data determined by emm_len
                	 */
                	byte[] respResult = new byte[4];
                	
                	// Ambil 4 byte response result
                	respResult[0] = responseData[0];
                	respResult[1] = responseData[1];
                	respResult[2] = responseData[2];
                	respResult[3] = responseData[3]; 	
                	String resultHex = byte2hex(respResult);                	
                	long resultInt = Long.parseLong(resultHex.toUpperCase(), 16);
                	
                	aSumaResponse.setIdSuma(sessionIdParam);
                	aSumaResponse.setResponse("0x"+resultHex.toUpperCase());
                	
                	if (resultInt == 0 ) {
                		log.info("Summary; Open Account: "+ cardNumber +"; Success; 0x" + resultHex.toUpperCase());
                		aSumaResponse.setResponseStatus("1");
                		
                	} else {
                		log.error("Summary; Open Account: "+ cardNumber +"; Not Success; 0x"+ resultHex.toUpperCase() );
                		aSumaResponse.setResponseStatus("-1");
                		
                	}               	
                	
                } else {
                	log.error("Response message is 0, problem with socket connection, pls restart the engine " );
                }
                
                in.close();
                     
            } catch (IOException e2) {
                log.error("Reading Failure, Exception from server : " + e2.toString());
                e2.printStackTrace();
            }
           
          //log.info("Closing stream..");
           
          client.close();
            
          //log.info("Closed");
            
        } catch (IOException e) {
            log.error("Exception from server : " + e.toString());
           e.printStackTrace();
        }
        
        return aSumaResponse;
		
	}
	
	
	public void cardInquiry(String sessionIdParam, String cardNumber) {
		
		//String serverName = "Localhost";
        sumaServer = this.sumaProperties.getProperty("SumaServer").toString();                
        sumaPort = Integer.valueOf(this.sumaProperties.getProperty("SumaPort").toString());
        CAS_Version = this.sumaProperties.getProperty("SumaCASVersion").toString();
        
        try {
           //log.info("Connecting to " + sumaServer + " on port " + sumaPort);
           Socket client = new Socket(sumaServer, sumaPort);
           
           //log.info("Just connected to " + client.getRemoteSocketAddress());
           
           OutputStream outToServer = client.getOutputStream();
           //log.info("Output stream get From Server..");
                  
           DataOutputStream out = new DataOutputStream(outToServer);
           //log.info("Output stream created from local to server..");
           
           //log.info("Try to communicate to with localsocket : " + client.getLocalSocketAddress());
           
           
            //log.info("Try writing to the stream..");
            try {
                
                // 1.Message Header // 1.1 Session ID, 2 Byte, maksium 65536, int 
                int sessionId = Integer.valueOf(sessionIdParam.substring(0,sessionIdParam.length()-2));
                String sessionHex = Integer.toHexString(sessionId);
                //log.info("Session ID Decimal = " + sessionId);
                //log.info("Session ID Hex Format = " + sessionHex.toUpperCase());
                
                /*
                 * 2 Byte completion
                 */
                if (sessionHex.length() == 3) { sessionHex = "0"+sessionHex; }
                if (sessionHex.length() == 2) { sessionHex = "00"+sessionHex; }
                if (sessionHex.length() == 1) { sessionHex = "000"+sessionHex; }
                
                //log.info("Session ID Hex Format Final = 0x" + sessionHex.toUpperCase());
                
                byte[] sessionid_bytes = hex2Byte(sessionHex);                
                //log.info("Session ID parameter, Length : " + sessionid_bytes.length + " bytes ");
                
                for (int i=0; i < sessionid_bytes.length; i++) {
                    //log.info("Byte "+i+" = " +sessionid_bytes[i]);    
                } 	               
                

                // 1.Message Header   // 1.2 CAS_VER, 1 Byte, maksium 256, short 
                short CAS_VER = Short.valueOf(CAS_Version);
                String CAS_VERHex = Integer.toHexString(CAS_VER);
                //log.info("CAS_VER Decimal = " + CAS_VER);
                //log.info("CAS_VER Hex Format = " + CAS_VERHex.toUpperCase());        
                
                if (CAS_VERHex.length() == 1) { CAS_VERHex = "0"+CAS_VERHex; }
                
                //log.info("CAS_VER Hex Format = 0x" + CAS_VERHex.toUpperCase());
                
                byte[] CAS_VER_bytes = hex2Byte(CAS_VERHex);
                //log.info("CAS_VER length : " + CAS_VER_bytes.length + " bytes ");
                
                for (int i=0; i < CAS_VER_bytes.length; i++) {
                	//log.info("Byte "+i+" = " +CAS_VER_bytes[i]);    
                }              
                

                // 1.Message Header   // 1.3 command type, 1 Byte, maksium 256, short 
                short commandType = COMMAND_TYPE_INQUIRY;
                String commandTypeHex = Integer.toHexString(commandType);
                //log.info("commandType = " + commandType);
                //log.info("commandType Hex Format = " + commandTypeHex.toUpperCase());
                
                if (commandTypeHex.length() == 1) { commandTypeHex = "0"+commandTypeHex; }
                
                //log.info("commandType Hex Format = 0x" + commandTypeHex.toUpperCase());
                
                byte[] commandTypeBytes = hex2Byte(commandTypeHex);                               
                //log.info("commandTypeBytes Length : " + commandTypeBytes.length + " bytes" );
                
                for (int i=0; i < commandTypeBytes.length; i++) {
                  //  log.info("Byte "+i+" = " +commandTypeBytes[i]);    
                }                                
                
                
                //2. data body, 1 adalah card number
                BigInteger cardNumberbi = new BigInteger (cardNumber);
                long cardNumberlong = cardNumberbi.longValue();
                String cardNumberHex = Long.toHexString(cardNumberlong);
                //log.info("Card Number Decimal = " + cardNumberbi);
                //log.info("Card Number Hex = " + cardNumberHex);                
                
                if (cardNumberHex.length() == 7) {cardNumberHex = "0"+cardNumberHex;}
                if (cardNumberHex.length() == 6) {cardNumberHex = "00"+cardNumberHex;}
                if (cardNumberHex.length() == 5) {cardNumberHex = "000"+cardNumberHex;}
                if (cardNumberHex.length() == 4) {cardNumberHex = "0000"+cardNumberHex;}
                if (cardNumberHex.length() == 3) {cardNumberHex = "00000"+cardNumberHex;}
                if (cardNumberHex.length() == 2) {cardNumberHex = "000000"+cardNumberHex;}
                if (cardNumberHex.length() == 1) {cardNumberHex = "0000000"+cardNumberHex;}
                
                //log.info("Card Number Hex Format Final = 0x" + cardNumberHex);
                
                byte[] cardNumberBytes_Conf = hex2Byte(cardNumberHex);
                
                //log.info("Card Number Byte length = " + cardNumberBytes_Conf.length + " byte ");                
        		    
            	for (int i=0; i < cardNumberBytes_Conf.length; i++) {        	
            	//	log.info("Byte "+i+" = " +cardNumberBytes_Conf[i]);    
                }
            	
            	// 1.Message Header  // 1.4 Data length, 2 Byte untuk nambahin product, maksium 256, short 
            	/*
            	 * data length di ambil dari length cardnumber
            	 */
                int data_length = cardNumberBytes_Conf.length;
                String data_length_hex = Integer.toHexString(data_length);
                //log.info("data_length = " + data_length);
                //log.info("data_length Hex Format = " + data_length_hex);
                
                if (data_length_hex.length() == 1) { data_length_hex = "000"+data_length_hex;}
                if (data_length_hex.length() == 2) { data_length_hex = "00"+data_length_hex;}
                if (data_length_hex.length() == 3) { data_length_hex = "0"+data_length_hex;}
                
                //log.info("data_length Hex Format = 0x" + data_length_hex);
                
                byte[] data_length_bytes = hex2Byte(data_length_hex);  
                
                //log.info("data_length_bytes parameter, Length : " + data_length_bytes.length + " bytes ");
                
                for (int i=0; i < data_length_bytes.length; i++) {
                  //  log.info("Byte "+i+" = " +data_length_bytes[i]);    
                }
            	
                
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                
                baos.write(sessionid_bytes);
                baos.write(CAS_VER_bytes);
                baos.write(commandTypeBytes);
                baos.write(data_length_bytes);                               
                baos.write(cardNumberBytes_Conf);               
         
                byte[] finalMessage = baos.toByteArray();
                log.info("Final message, Length : " + finalMessage.length + " Byte");
                
                String finalMessageHexa = byte2hex(finalMessage);
                log.info("Final Message, content: " + "0x"+finalMessageHexa.toUpperCase());
                
                //log.info("");        

                out.write(finalMessage);
                //log.info("Message sent to CAS");
                
            } catch (IOException e) {

                log.error("Writing failure,  Exception from server : ");
                e.printStackTrace();
            }                      
           
           
           InputStream inFromServer = client.getInputStream();
           log.info("Input stream get From Server..");
            
           DataInputStream in = new DataInputStream(inFromServer);
           //log.info("Input stream created..");
           log.info("Reading input streaam....");
            
            try {
                                  
                byte[] BufferMsg = new byte[127];   // 1Kb

                in.read(BufferMsg);
                log.info("Server response , LENGTH " + BufferMsg.length  + ", Mesage = " +  "0x"+byte2hex(BufferMsg).toUpperCase() );
                
                /*
                 * Processing response data
                 */
                if (BufferMsg.length != 0) {
                	/*
                	 * 
                	 */
                	
                	//Get Header session ID 
                	byte[] sessionResp = new byte[2];
                	byte[] cas_VerResp = new byte[1];
                	byte[] cmdTpyeResp = new byte[1];
                	byte[] lengthResp = new byte[2];
                	
                	sessionResp[0] = BufferMsg[0];
                	sessionResp[1] = BufferMsg[1];
                	
                	cas_VerResp[0] = BufferMsg[2];
                	cmdTpyeResp[0] = BufferMsg[3];
                	
                	lengthResp[0] = BufferMsg[4];
                	lengthResp[1] = BufferMsg[5];
                	
                	String lengthRespHex = byte2hex(lengthResp);
                	int lengthRespint = Integer.parseInt(lengthRespHex.toUpperCase(),16);
                	//long lengthRespint = Long.parseLong(lengthRespHex.toUpperCase(), 16);
                	log.info("Found response length = " + lengthRespint);
                	
                	byte[] responseData = new byte[lengthRespint];
                	
                	for (int i=0; i < lengthRespint ; i++ ) {                		
                		responseData[i] = BufferMsg[6+i];                		
                	}
                	
                	log.info("Response data = 0x" + byte2hex(responseData).toUpperCase());
                	
                	/*
                	 * Response data berbeda tiap command
                	 * Command inquiry sbb, input nya ada array byte[]
                	 * 4 byte = result 
                	 * 2 byte = quantity product
                	 * variable
                	 * product 
                	 * 2 byte product number 
                	 * 4 byte begin time 
                	 * 4 byte end time 
                	 * 4 byte entitle time
                	 */
                	byte[] respResult = new byte[4];
                	byte[] respQuantity = new byte[2];
                	
                	for (int j = 0; j < respResult.length; j++) {
                		respResult[j] = responseData[j];
                	}
                	
                	String resultHex = byte2hex(respResult);
                	//int resultInt = Integer.parseInt(resultHex.toUpperCase(), 16);
                	long resultInt = Long.parseLong(resultHex.toUpperCase(), 16);
                	if (resultInt == 0 ) {
                		log.info("Response sukses 0x" + resultHex.toUpperCase());
                		
                		/*
                		 * get quantity product.
                		 */
                		for (int j = 0; j < respQuantity.length; j++) {
                			respQuantity[j] = responseData[j+4];
                    	}
                		String quantityHex = byte2hex(respQuantity);
                		int quantityInt = Integer.parseInt(quantityHex.toUpperCase(), 16);
                		
                		if (quantityInt > 0 ) {
                			
                			log.info("Found record Auth, " + quantityInt + " Auth");
                			
                			int counterProduct = 0;
                			for (int i = 0; i < quantityInt; i++) {
                				
                				/*
                				 * loop array quantity , isinya adalah
                				 * 2 byte product number 
                				 * 4 byte begin time
                				 * 4 byte end time
                				 * 4 byte entitle time 
                				 * total 14 byte untuk satu product, 
                				 */
                				byte[] productNumber = new byte[2];
                            	byte[] beginTime = new byte[4];
                            	byte[] endTime = new byte[4];
                            	//byte[] entitleTime = new byte[4];
                            	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            	
                            	//PRODUCT NUMBER
                            	for (int j = 0; j < productNumber.length; j++) {
                            		productNumber[j] = responseData[j+6+counterProduct];
                            	}
                            	String productNumberHex = byte2hex(productNumber);
                            	int productNumberint = Integer.parseInt(productNumberHex, 16);
                            	log.info("Found product = " + productNumberint);
                            	
                            	// Begin time
                            	for (int j = 0; j < beginTime.length; j++) {
                            		beginTime[j] = responseData[j+8+counterProduct];
                            	}
                            	long beginTimeLong = Long.parseLong(byte2hex(beginTime), 16);
                            	Date beginTimeDate = new Date(beginTimeLong*1000);
                            	log.info("Found begin time = " + sdf.format(beginTimeDate));
                            	
                            	// End time
                            	for (int j = 0; j < endTime.length; j++) {
                            		endTime[j] = responseData[j+12+counterProduct];
                            	}
                            	long endTimeLong = Long.parseLong(byte2hex(endTime), 16);
                            	Date endTimeDate = new Date(endTimeLong*1000);
                            	log.info("Found end time = " + sdf.format(endTimeDate));
                            	
                            	/*
                            	 * Entitlement time tidak diimplement 
                            	 * 
                            	for (int j = 0; j < entitleTime.length; j++) {
                            		entitleTime[j] = responseData[j+16+counterProduct];
                            	}
                            	long entitleTimeLong = Long.parseLong(byte2hex(entitleTime), 16);
                            	Date entitleTimeDate = new Date(entitleTimeLong*1000);
                            	log.info("Found entitleTimeDate = " + sdf.format(entitleTimeDate));
                            	*/
                            	
                            	counterProduct = counterProduct + 10;
                            	
                            	
                			}
                			
                		} else {
                			
                			log.info("Not Found record Auth, " + quantityInt + " Auth");
                		}
                		
                		
                		
                		
                	} else {
                		log.error("CAS Response NOT SUCCESS with Code 0x" + resultHex.toUpperCase() );
                	}
                	
                	
                	
                }
                
                in.close();
                
                     
            } catch (IOException e2) {
                log.error("Reading Failure, Exception from server : " + e2.toString());
                e2.printStackTrace();
            }
           
          log.info("Closing stream..");
           
          client.close();
            
          log.info("Closed");
            
        } catch (IOException e) {
            log.error("Exception from server : " + e.toString());
           e.printStackTrace();
        }
		
	}
	
	public SumaResponse closeAccount(String sessionIdParam, String cardNumber) {
	
		SumaResponse aSumaResponse = new SumaResponse();
		
        sumaServer = this.sumaProperties.getProperty("SumaServer").toString();                
        sumaPort = Integer.valueOf(this.sumaProperties.getProperty("SumaPort").toString());
        CAS_Version = this.sumaProperties.getProperty("SumaCASVersion").toString();
        
        try {
           log.info("Connecting to " + sumaServer + " on port " + sumaPort);
           Socket client = new Socket(sumaServer, sumaPort);
           
           log.info("Just connected to " + client.getRemoteSocketAddress());
           
           OutputStream outToServer = client.getOutputStream();
           //log.info("Output stream get From Server..");
                  
           DataOutputStream out = new DataOutputStream(outToServer);
           //log.info("Output stream created from local to server..");
           
           log.info("Try to communicate to with localsocket : " + client.getLocalSocketAddress());
           
           
            log.info("Try writing to the stream..");
            try {
                
                // 1.Message Header // 1.1 Session ID, 2 Byte, maksium 65536, int 
                int sessionId = Integer.valueOf(sessionIdParam.substring(0,sessionIdParam.length()-2));
                String sessionHex = Integer.toHexString(sessionId);                
                if (sessionHex.length() == 3) { sessionHex = "0"+sessionHex; }
                if (sessionHex.length() == 2) { sessionHex = "00"+sessionHex; }
                if (sessionHex.length() == 1) { sessionHex = "000"+sessionHex; }                
                log.info("Session ID Hex Format Final = 0x" + sessionHex.toUpperCase());                
                byte[] sessionid_bytes = hex2Byte(sessionHex);                
                

                // 1.Message Header   // 1.2 CAS_VER, 1 Byte, maksium 256, short 
                short CAS_VER = Short.valueOf(CAS_Version);
                String CAS_VERHex = Integer.toHexString(CAS_VER);                
                if (CAS_VERHex.length() == 1) { CAS_VERHex = "0"+CAS_VERHex; }                
                log.info("CAS_VER Hex Format = 0x" + CAS_VERHex.toUpperCase());                
                byte[] CAS_VER_bytes = hex2Byte(CAS_VERHex);
                

                // 1.Message Header   // 1.3 command type, 1 Byte 
                short commandType = COMMAND_TYPE_CLOSE_ACCOUNT;
                String commandTypeHex = Integer.toHexString(commandType);                
                if (commandTypeHex.length() == 1) { commandTypeHex = "0"+commandTypeHex; }                
                log.info("commandType CLOSE ACCOUNT Hex Format = 0x" + commandTypeHex.toUpperCase());                
                byte[] commandTypeBytes = hex2Byte(commandTypeHex);                               
                
                
                // 1.Message Header  // 1.4 Data length, 2 Byte  
                int data_length = COMMAND_TYPE_CLOSE_ACCOUNT_LEN;
                String data_length_hex = Integer.toHexString(data_length);                
                if (data_length_hex.length() == 1) { data_length_hex = "000"+data_length_hex;}
                if (data_length_hex.length() == 2) { data_length_hex = "00"+data_length_hex;}
                if (data_length_hex.length() == 3) { data_length_hex = "0"+data_length_hex;}                
                log.info("data_length Hex Format = 0x" + data_length_hex.toUpperCase());                
                byte[] data_length_bytes = hex2Byte(data_length_hex);                    
                
                
                /*
                 * Databody untuk close Account, Total 9 Byte
                 * 4 byte Card id 
                 * 1 byte is send, default value = 1
                 * 4 byte expiration time
                 */
                
                // Databoyd cardNumber
                BigInteger cardNumberbi = new BigInteger (cardNumber);
                long cardNumberlong = cardNumberbi.longValue();
                String cardNumberHex = Long.toHexString(cardNumberlong);                
                if (cardNumberHex.length() == 7) {cardNumberHex = "0"+cardNumberHex;}
                if (cardNumberHex.length() == 6) {cardNumberHex = "00"+cardNumberHex;}
                if (cardNumberHex.length() == 5) {cardNumberHex = "000"+cardNumberHex;}
                if (cardNumberHex.length() == 4) {cardNumberHex = "0000"+cardNumberHex;}
                if (cardNumberHex.length() == 3) {cardNumberHex = "00000"+cardNumberHex;}
                if (cardNumberHex.length() == 2) {cardNumberHex = "000000"+cardNumberHex;}
                if (cardNumberHex.length() == 1) {cardNumberHex = "0000000"+cardNumberHex;}                
                log.info("Card Number Hex Format Final = 0x" + cardNumberHex.toUpperCase());                
                byte[] cardNumberBytes_Conf = hex2Byte(cardNumberHex);
                
                // Databody is_send parameter 1 Byte
                short is_send = 1;
                String is_sendHex = Integer.toHexString(is_send);                
                if (is_sendHex.length() == 1) { is_sendHex = "0"+is_sendHex; }                
                log.info("is_sendHex Hex Format = 0x" + is_sendHex.toUpperCase());                
                byte[] is_sendHexBytes = hex2Byte(is_sendHex); 
                                
                // Databody Expired_Time: expired time of close account command. Recommendation: expired time = send time+ 1 day.
                long timeSaatini = System.currentTimeMillis();
                int timeBesok = 86400 + (int)(timeSaatini/1000L);
                String timeBesokHex = Long.toHexString(timeBesok);
                log.info("timeBesokHex = 0x" + timeBesokHex.toUpperCase());                
                byte[] timeBesokBytes = hex2Byte(timeBesokHex);
                
                
                /*
                 * Congcat all datasent to Stream
                 */               
                ByteArrayOutputStream baos = new ByteArrayOutputStream();                
                baos.write(sessionid_bytes);
                baos.write(CAS_VER_bytes);
                baos.write(commandTypeBytes);
                baos.write(data_length_bytes);                
                baos.write(cardNumberBytes_Conf);
                baos.write(is_sendHexBytes);
                baos.write(timeBesokBytes);
         
                byte[] finalMessage = baos.toByteArray();
                log.info("Final message, Length : " + finalMessage.length + " Byte");
                
                String finalMessageHexa = byte2hex(finalMessage);
                log.info("Final Message, content: " + "0x"+finalMessageHexa.toUpperCase());
             
                out.write(finalMessage);
                log.info("Message sent to CAS");
                
            } catch (IOException e) {

                log.error("Writing failure,  Exception from server : ");
                e.printStackTrace();
            }                 
                      
           InputStream inFromServer = client.getInputStream();
           log.info("Input stream get From Server..");
            
           DataInputStream in = new DataInputStream(inFromServer);
           //log.info("Input stream created..");
           log.info("Reading input streaam....");
            
            try {
                                  
                byte[] BufferMsg = new byte[255];   // 1Kb

                in.read(BufferMsg);
                log.info("Server response , LENGTH " + BufferMsg.length  + ", Mesage = " +  "0x"+byte2hex(BufferMsg).toUpperCase() );
                
                /*
                 * Processing response data
                 */
                if (BufferMsg.length != 0) {
                               	
                	//Get Header Response
                	byte[] sessionResp = new byte[2];
                	byte[] cas_VerResp = new byte[1];
                	byte[] cmdTpyeResp = new byte[1];
                	byte[] lengthResp = new byte[2];
                	
                	// Ambil header session id
                	sessionResp[0] = BufferMsg[0];
                	sessionResp[1] = BufferMsg[1];
                	
                	// Ambil header cas_ver & cmdType
                	cas_VerResp[0] = BufferMsg[2];
                	cmdTpyeResp[0] = BufferMsg[3];
                	
                	// Ambil header length response nya.
                	lengthResp[0] = BufferMsg[4];
                	lengthResp[1] = BufferMsg[5];
                	
                	String lengthRespHex = byte2hex(lengthResp);
                	int lengthRespint = Integer.parseInt(lengthRespHex.toUpperCase(),16);                	
                	log.info("Found response length = " + lengthRespint);
                	
                	// Ambil response data sesuai length diatas.
                	byte[] responseData = new byte[lengthRespint];                	
                	for (int i=0; i < lengthRespint ; i++ ) {                		
                		responseData[i] = BufferMsg[6+i];                		
                	}
                	
                	log.info("Response data = 0x" + byte2hex(responseData).toUpperCase());
                	
                	/*
                	 * Response data berbeda tiap command
                	 * Command inquiry sbb, input nya ada array byte[]
                	 * 4 byte = result 
                	 * 1 byte = Quantity of EMM packet returned
                	 * for(I=0;I<N;I++) N=Emm_Count
                	 * 1 byte Emm_Len 
                	 * variable byte Emm_Data determined by emm_len
                	 */
                	byte[] respResult = new byte[4];
                	
                	// Ambil 4 byte response result
                	respResult[0] = responseData[0];
                	respResult[1] = responseData[1];
                	respResult[2] = responseData[2];
                	respResult[3] = responseData[3]; 	
                	String resultHex = byte2hex(respResult);                	
                	long resultInt = Long.parseLong(resultHex.toUpperCase(), 16);
                	
                	aSumaResponse.setIdSuma(sessionIdParam);
                	aSumaResponse.setResponse("0x"+resultHex.toUpperCase());
                	
                	if (resultInt == 0 ) {
                		log.info("Response sukses 0x" + resultHex.toUpperCase());
                		aSumaResponse.setResponseStatus("1");
                		
                	} else {
                		log.error("CAS Response NOT SUCCESS with Code 0x" + resultHex.toUpperCase() );
                		aSumaResponse.setResponseStatus("-1");
                		
                	}               	
                	
                } else {
                	log.error("Response message is 0, problem with socket connection, pls restart the engine " );
                }
                
                in.close();
                     
            } catch (IOException e2) {
                log.error("Reading Failure, Exception from server : " + e2.toString());
                e2.printStackTrace();
            }
           
          log.info("Closing stream..");
           
          client.close();
            
          log.info("Closed");
            
        } catch (IOException e) {
            log.error("Exception from server : " + e.toString());
           e.printStackTrace();
        }
        
        return aSumaResponse;
		
	}
	
	public void loadProperties () throws FileNotFoundException, IOException {
		this.sumaProperties.load(new FileInputStream(new File(this.fileName)));       
        
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
