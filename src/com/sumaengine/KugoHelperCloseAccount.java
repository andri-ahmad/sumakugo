package com.sumaengine;

import java.sql.ResultSet;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class KugoHelperCloseAccount extends TimerTask {
	
	static DbmysqlLayer mysqlDBLayer1;
	public static int counterRun = 1;
	static Logger log = Logger.getLogger(KugoHelperCloseAccount.class.getName());
	static SumaProcessor sumaproc;
	SumaResponse sumaresponse;
	
	public void run() 
    {
		log.info("");
		log.info("TIMER TASK For Close Account initiated.. ["+ counterRun + "]");
		
		try 
		{	
			/*
			 * create instance suma processor
			 */
			sumaproc = new SumaProcessor();
			sumaproc.loadProperties();		
			
			
			// Get Connection to the DB
	        log.info("----------------------------");
	        log.info( "OPENING DATABASE CONNECTION...");
	        
	        mysqlDBLayer1 = new DbmysqlLayer();
	        mysqlDBLayer1.loadProperties("XX");
	        
	        /*
	         * this method return connect inside the class
	         */
	        mysqlDBLayer1.loadDblConnection();             
	        log.info("OPENING DATABASE CONNECTION [DONE]");  
	        
	        // Process one hit API
	        //string query
	        // prepare query from database
	        ResultSet rs = null; 
	        int i = 1;    
	        
	        
	        /*
	         * Algoritma:
	         * 1. cari record2 yang regisrasi dan command type =33 dan status nya = 0 
	         */
	        
	        log.info("QUERYING DATA FROM DATABASE...");
	        String query1 =    "SELECT * FROM reg_srv_up_kugo WHERE tbl_ref = 'close account' AND command_type = 21 AND status_send = '0' ORDER BY idSuma ASC ";       
	        rs = mysqlDBLayer1.selectQuery(query1);	       
	        
	        if (!rs.next()) {
	        	
	        	log.info("");
	        	log.info( "NO RECORDS TO PROCESS.." );	        	
	        	
	        } else {	        	
	        	
	        	log.info("FOUND RECORDS TO BE PROCESSED..");	        	
	        	
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
	            	log.info("");
	            	log.info("PROCESSING RECORDS" + "["+i+"]");
	            	log.info("IdSuma: " + sr1.getIdSuma());
	            	log.info("SubscriberNo: " + sr1.getSubsNo());
	            	log.info("Nomor Kartu " + sr1.getViewcardNo());
	            	log.info("TblRef: " + sr1.getTblRef());
	            	log.info("CommandType: " + sr1.getCommandType());
	            	
	            	
	            	/*
	            	 * step 2.1 : process open account to SUMA 
	            	 */
	            	log.info("Close Account process to SUMA Server..");
	            	
	            	/*
	            	 * call method nya disini 
	            	 */
	            	
	            	try {
	            		
	            		sumaresponse = sumaproc.closeAccount(sr1.getIdSuma(),sr1.getViewcardNo());
	            		
	            		/*
		            	 * jika mendapatkan response sukses
		        		 * update ke DB status = 1
		            	 */
	            		String updateQuery = "UPDATE reg_srv_up_kugo \n"
	            				+ "SET status_send = '"+sumaresponse.getResponseStatus() +"',\n"
	            				+ "keterangan = '"+sumaresponse.getResponse()+"'\n"
	            				+ "WHERE idSuma = "+sumaresponse.getIdSuma()+"\n"
	            				+ "";
	            		mysqlDBLayer1.updateQuery(updateQuery);
	            		log.info("Close Account Response updated to database.");
	                    
		            	
	            		
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
	        	
	        } // End else if rs.net
	        
			
			
			// DONE
	        
	        // Closing database connection
	        rs.close();    
	        mysqlDBLayer1.releaseDblConnection();
	        
	        log.info("Success::FINISH, CLOSING DB CONNECTION");
	        log.info("Success::-----------------------------");
		
	        
		} catch (Exception expAll ) {
			//throw e;
			log.error("Error Found : " + expAll.toString());
		}         
        
        /*
         * Never Sync
         */
        
		log.info("Object Synchronize...");
		log.info("Main thread sleep for 5 seconds..");		
		counterRun++;
		
		
    }

}
