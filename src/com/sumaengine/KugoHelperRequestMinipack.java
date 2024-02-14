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
	        ResultSet rs = null; 
	        ResultSet rsOpenAuth = null;
	        int i = 1;
	        
	        /*
	         * Algoritma:
	         * 1. cari record2 yang request minipack
	         */
	        
	        //log.info("QUERYING DATA FROM DATABASE...");
	        String query1 =    "SELECT * FROM reg_srv_up_kugo WHERE \n"
	        				+ "tbl_ref = 'registration' \n"
	        				+ "AND command_type = 33 \n"
	        				+ "AND (status_send = '0' OR status_send = '2') \n"
	        				+ "ORDER BY idSuma ASC ";       
	        rs = mysqlDBLayer1.selectQuery(query1);	
			
			
		} catch (Exception expAll ) {
			//Try catch Run secara Umum
			log.error("Class, run method(); Error Found : " + expAll.toString());
		} 
		
				
		log.info("Task Request Minipacl sleep for 5 seconds..");		
		log.info("-");
		counterRun++;
       
		
    }

}
