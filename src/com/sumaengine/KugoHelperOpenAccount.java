package com.sumaengine;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.TimerTask;
import java.util.Date;
import org.apache.log4j.Logger;

public class KugoHelperOpenAccount extends TimerTask {
	
	static DbmysqlLayer mysqlDBLayer1;
	public static int counterRun = 1;
	static Logger log = Logger.getLogger(KugoHelperOpenAccount.class.getName());
	static SumaProcessor sumaproc;
	SumaResponse sumaresponse;
	SumaResponse srespOAuth;
	
	public void run() 
    {
		log.info("-");
		log.info("TIMER TASK For Open Account started.. ["+ counterRun + "]");
		
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
	        ResultSet rs = null; 
	        ResultSet rsOpenAuth = null;
	        int i = 1;
	        
	        /*
	         * Algoritma:
	         * 1. cari record2 yang regisrasi dan command type =33 dan status nya = 0 
	         */
	        
	        //log.info("QUERYING DATA FROM DATABASE...");
	        String query1 =    "SELECT * FROM reg_srv_up_kugo WHERE \n"
	        				+ "tbl_ref = 'registration' \n"
	        				+ "AND command_type = 33 \n"
	        				+ "AND (status_send = '0' OR status_send = '2') \n"
	        				+ "ORDER BY idSuma ASC ";       
	        rs = mysqlDBLayer1.selectQuery(query1);	       
	        
	        if (!rs.next()) {
	        	
	        	//log.info("");
	        	log.info( "NO RECORDS - Open Account TO PROCESS.." );	        	
	        	
	        } else {	        	
	        	
	        	log.info("FOUND RECORDS - Open Account TO PROCESSED..");	        	
	        	
	        	do 
	        		{        	
	            	            	
	            	/*
	            	 * Algoritma:
	            	 * 2. proses recod-record untuk open account.
	            	 * buat object sumarecord sr1
	            	 */
	            	SumaRecord sr1 = new SumaRecord();
	            	sr1.setIdSuma(rs.getString(1));
	            	sr1.setPackageName(rs.getString(2));
	            	sr1.setKodeProduk(rs.getString(3));
	            	sr1.setSubsNo(rs.getLong(4));
	            	sr1.setViewcardNo(rs.getString(5));
	            	sr1.setTblRef(rs.getString(13));
	            	sr1.setCommandType(rs.getLong(14));
	            	sr1.setStatusSend(rs.getLong(15));
	            	
	            	
	            	/*
	            	 * Algoritma step2: process record
	            	 * 
	            	 */
	            	log.info("PROCESSING RECORDS" + "["+i+"]");
	            	log.info("IdSuma: " + sr1.getIdSuma());
	            	log.info("SubscriberNo: " + sr1.getSubsNo());
	            	log.info("Nomor Kartu " + sr1.getViewcardNo());
	            	log.info("TblRef: " + sr1.getTblRef());
	            	log.info("CommandType: " + sr1.getCommandType());
	            	
	            	
	            	/*
	            	 * step 2.1 : process open account to SUMA 
	            	 */
	            	//log.info("Opening Account to SUMA Server..");
	            	
	            	
	            	try {	            		
	            	
	            		// open account to SUMA
	            		sumaresponse = sumaproc.openAccount(sr1.getIdSuma(),sr1.getViewcardNo());
	            		
	            		/*
		            	 * jika mendapatkan response sukses
		        		 * update ke DB status = 2 / Temporary bahwa akan memproses record auth setelahnya
		            	 */
	            		String updateQuery = "UPDATE reg_srv_up_kugo \n"
	            				+ "SET status_send = '2',\n"
	            				+ "ac_data = '"+sumaresponse.getResponse()+"'\n"
	            				+ "WHERE idSuma = "+sumaresponse.getIdSuma()+"\n"
	            				+ "";
	            		mysqlDBLayer1.updateQuery(updateQuery);
	            		log.info("Open Account update to DB as temp status "+ sumaresponse.getIdSuma() + " status_send = 2" );
	            		
	            		
	            		/*
	            		 * Jika Open Account sukses, continue auth the package while registration.
	            		 * Querynya adalah tbl ref = registration & command type = 1
	            		 * 
	            		 */
	            		
	            		log.info("Waiting for 1 second..");
	            		wait(1000);
	            		
	            		if (sumaresponse.getResponseStatus() == "1") {
	            			
	            			// get record yang siap di auth untuk kartu tersebut.
	            			String recordQueryAuth = "SELECT * FROM reg_srv_up_kugo \n"
	            					+"WHERE tbl_ref = 'registration' \n"
	            					+"AND command_type = 1 AND status_send = '0' \n"
	            					+"AND viewcard_no = '"+sr1.getViewcardNo()+"'";	            			
	            			rsOpenAuth = mysqlDBLayer1.selectQuery(recordQueryAuth);
	            			int j = 1;
	            			
	            			// Loop record proses ke suma.
	            			if (!rsOpenAuth.next()) {
	            				/*
	            				 * Tidak ada record untuk di proses auth, langung update id suma menjadi 1
	            				 */
	            				String updateQueryFin1 = "UPDATE reg_srv_up_kugo \n"
	    	            				+ "SET status_send = '"+sumaresponse.getResponseStatus()+"',\n"
	    	            				+ "ac_data = '"+sumaresponse.getResponse()+"'\n"
	    	            				+ "WHERE idSuma = "+sumaresponse.getIdSuma()+"\n"
	    	            				+ "";
	    	            		mysqlDBLayer1.updateQuery(updateQueryFin1);
	    	            		log.info("No record found to openAuth for VC "+ sr1.getViewcardNo());
	    	            		log.info("Open Account update to DB Final status "+ sumaresponse.getIdSuma() + " status_send = " + sumaresponse.getResponseStatus() );
	    	            		
	            			} else {
	            				
	            				/*
	            				 * Loop proses record untuk openAtuh disini.
	            				 */
	            				log.info("Found records to openAuth for VC: "+ sr1.getViewcardNo());
	            				do {
	            					
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
	            					
	            					log.info("Processing oAuth" + "["+j+"]");
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
	            					 * release object
	            					 */
	            	            	srOAuth = null;
	            	            	j++;
	            					
	            				} while (
	            						rsOpenAuth.next()
	            						);
	            				
	            				
	            				/*
	            				 * Update parent apapun hasil nya sudah di proses
	            				 */
	            				String updateQueryFin2 = "UPDATE reg_srv_up_kugo \n"
	    	            				+ "SET status_send = '"+sumaresponse.getResponseStatus()+"',\n"
	    	            				+ "ac_data = '"+sumaresponse.getResponse()+"'\n"
	    	            				+ "WHERE idSuma = "+sumaresponse.getIdSuma()+"\n"
	    	            				+ "";
	    	            		mysqlDBLayer1.updateQuery(updateQueryFin2);
	    	            		log.info("Open Account update to DB Final status "+ sumaresponse.getIdSuma() + " status_send = " + sumaresponse.getResponseStatus() );    	            		
	            				
	            			} // End Else rsOpenAuth ada record nya   
	            			
	            			// close the resultSet oAuth:
	            			rsOpenAuth.close();
	            			
	            		} else { // Else dari If Sumaresponse.getResponse == 1
	            			
	            			// Sumaresponse.getResponse nya bukan 1, mungkin -1, update langunsg ke DB Final status
	            			String updateQueryFin2 = "UPDATE reg_srv_up_kugo \n"
    	            				+ "SET status_send = '"+sumaresponse.getResponseStatus()+"',\n"
    	            				+ "ac_data = '"+sumaresponse.getResponse()+"'\n"
    	            				+ "WHERE idSuma = "+sumaresponse.getIdSuma()+"\n"
    	            				+ "";
    	            		mysqlDBLayer1.updateQuery(updateQueryFin2);
    	            		log.info("Open Account update to DB Final status "+ sumaresponse.getIdSuma() + " status_send = " + sumaresponse.getResponseStatus() );
    	            		
	            		}
	            		
	            	} catch (Exception exp ) {
	            		
	            		log.error(exp.toString());
	            	}         	   
	            	
	            	/*
	            	 * null kan sumarecord sr1 
	            	 */
	            	log.info("Release object sumarecord");
	            	sr1 = null;
	            	i++;
	            
	            } while (rs.next());
	            	
	        	// End do
	        	
	        } // End else if rs.next
	        
			
			
			// DONE
	        
	        // Closing database connection
	        rs.close();  	        
	        mysqlDBLayer1.releaseDblConnection();
	        
	        //log.info("CLOSING DB CONNECTION");
			
		} catch (Exception expAll ) {
			//throw e;
			log.error("Error Found : " + expAll.toString());
		} 
		
				
		log.info("Task Open Account thread sleep for 5 seconds..");		
		log.info("-");
		counterRun++;
        
        /*
         * Never Sync
         */
		
		//synchronized(KugoMain.obj)
        //{
        	//TimerTesting3.obj.notify();
        //}
		
		
    }
	
	public static void wait(int ms)
	{
	    try
	    {
	        Thread.sleep(ms);
	    }
	    catch(InterruptedException ex)
	    {
	        Thread.currentThread().interrupt();
	    }
	}

}
