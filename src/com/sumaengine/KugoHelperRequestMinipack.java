package com.sumaengine;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.TimerTask;
import java.util.Date;
import org.apache.log4j.Logger;

public class KugoHelperRequestMinipack extends TimerTask {
	
	static DbmysqlLayer mysqlDBLayer1;
	public static int counterRun = 1;
	static Logger log = Logger.getLogger(KugoHelperRequestMinipack.class.getName());
	static SumaProcessor sumaproc;
	SumaResponse sumaresponse;
	SumaResponse srespOAuth;
	
	public void run() 
    {
		log.info("-");
		log.info("Task for Request Minipack started.. ["+ counterRun + "]");
		
		try 
		{
			
			/*
			 * create instance suma processor
			 */
			sumaproc = new SumaProcessor();
			sumaproc.loadProperties();
			
			
			// Get Connection to the DB
	        //log.info( "OPENING DATABASE CONNECTION...");
	        
	        mysqlDBLayer1 = new DbmysqlLayer();
	        mysqlDBLayer1.loadProperties("XX");
	        
	        /*
	         * this method return connect inside the class
	         */
	        mysqlDBLayer1.loadDblConnection();             
	        //log.info("OPENING DATABASE CONNECTION [DONE]");  
	        
	        // Process one hit API
	        //string query
	        // prepare query from database
	        ResultSet rsOpenAuth = null;
	        int i = 1;
	        
	        /*
	         * Algoritma:
	         * 1. cari record2 yang request minipack
	         */
	        
	        //log.info("QUERYING DATA FROM DATABASE...");
	        String selectQuery1 = "SELECT * FROM reg_srv_up_kugo WHERE\n"
	        		+ "tbl_ref = 'request_minipacks'\n"
	        		+ "AND command_type = 1\n"
	        		+ "AND status_send = '0'\n"
	        		+ "UNION \n"
	        		+ "SELECT * FROM reg_srv_up_kugo WHERE\n"
	        		+ "tbl_ref = 'registration'\n"
	        		+ "AND command_type = 1\n"
	        		+ "AND status_send = '0'\n"
	        		+ "AND keterangan like '%update_basic%'"
	        		+ "ORDER BY idSuma ASC\n"
	        		+ "";    
	        rsOpenAuth = mysqlDBLayer1.selectQuery(selectQuery1);	
			
	        if (!rsOpenAuth.next()) {
	        	
	        	//log.info("");
	        	log.info( "NO RECORDS - Request Minipack TO PROCESS.." );	        	
	        	
	        } else {
	        
	        	log.info("FOUND RECORDS - Request Minipack TO PROCESSED..");	        	
	        	
	        	do {
	        		
	        		/*
	            	 * Algoritma:
	            	 * 2. proses recod-record untuk open account.
	            	 * buat object sumarecord sr1
	            	 */
	        		// getRecords
					SumaRecord srOAuth = new SumaRecord();
					srOAuth.setIdSuma(rsOpenAuth.getString(1));
					srOAuth.setPackageName(rsOpenAuth.getString(2));
					srOAuth.setKodeProduk(rsOpenAuth.getString(3));
					srOAuth.setSubsNo(rsOpenAuth.getLong(4));
					srOAuth.setViewcardNo(rsOpenAuth.getString(5));
					srOAuth.setTblRef(rsOpenAuth.getString(13));
					srOAuth.setCommandType(rsOpenAuth.getLong(14));
					srOAuth.setStatusSend(rsOpenAuth.getLong(15));
					srOAuth.setInitDate(rsOpenAuth.getTimestamp(7));
					srOAuth.setLimitDate(rsOpenAuth.getTimestamp(8));
					
					log.info("Processing oAuth" + "["+i+"]");
	            	log.info("IdSuma: " + srOAuth.getIdSuma());
	            	log.info("SubscriberNo: " + srOAuth.getSubsNo());
	            	log.info("Nomor Kartu: " + srOAuth.getViewcardNo());
	            	log.info("Kode Product: " + srOAuth.getKodeProduk());
	            	log.info("Nama Product: " + srOAuth.getPackageName());
	            	log.info("Begin Time: " + srOAuth.getInitDate());
	            	log.info("End time: " + srOAuth.getLimitDate());
	            	log.info("TblRef: " + srOAuth.getTblRef());
	            	log.info("CommandType: " + srOAuth.getCommandType());
	            	
	            	//Proses the date Begin Time
	            	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	            	Date beginTime = new Date(srOAuth.getInitDate().getTime());
	            	log.info("Begin time formated: " + sdf.format(beginTime));
	            	
	            	Date endTime = new Date(srOAuth.getLimitDate().getTime());
	            	log.info("End time formated: " + sdf.format(endTime));
	            	
	            	// Open Auth 
	            	log.info("Open Auth process to SUMA Server..");
	            	
	            	try {
	            		
	            		srespOAuth = sumaproc.setAuth(srOAuth.getIdSuma(), srOAuth.getViewcardNo(), srOAuth.getKodeProduk(),
	            				srOAuth.getPackageName(), beginTime, endTime);
	            		
	            		/*
	            		 * Jika sukses update sukses per record
	            		 */
	            		String updateQueryRecordAuth = "UPDATE reg_srv_up_kugo \n"
	            				+ "SET status_send = '"+srespOAuth.getResponseStatus()+"',\n"
	            				+ "ac_data = '"+srespOAuth.getResponse()+"'\n"
	            				+ "WHERE idSuma = "+srespOAuth.getIdSuma()+"\n"
	            				+ "";
	            		mysqlDBLayer1.updateQuery(updateQueryRecordAuth);
	            		log.info("Oauth record update to DB: "+ srespOAuth.getIdSuma() + " status_send = " + srespOAuth.getResponseStatus());
	            		
	            		
	            	} catch (Exception exp ) {
	            		
	            		log.error(exp.toString());
	            	}          	
	            	
	            	/*
	            	 * null kan sumarecord sr1 
	            	 */
	            	log.info("Release object sumarecord");
	            	srOAuth = null;
	            	i++;
	        		
	        		
	        		
	        	} while (rsOpenAuth.next());
	        	
	        } // End else if rs.next
	        
	        // Closing database connection
	        rsOpenAuth.close();  	        
	        mysqlDBLayer1.releaseDblConnection();
	        
	        //log.info("CLOSING DB CONNECTION");
			
		} catch (Exception expAll ) {
			//Try catch Run secara Umum
			log.error("Class, run method(); Error Found : " + expAll.toString());
		} 
		
				
		log.info("Task Request Minipack sleep for 5 seconds..");		
		log.info("-");
		counterRun++;
       
		
    }

}
