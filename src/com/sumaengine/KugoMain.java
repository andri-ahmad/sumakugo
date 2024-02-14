package com.sumaengine;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class KugoMain {
	
	//protected static KugoMain obj;
	static Logger log = Logger.getLogger(KugoMain.class.getName());

	public static void main(String[] args)  throws Exception {
		
		/*
		 * HANDLING CTRL-C INTERUPTION
		 */
		final Thread currThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook( new Thread(() -> {
	        log.info("Oops! Trapped exit signal...");
	            synchronized (currThread) {
	            	currThread.notifyAll();	
	            	try {
	            		currThread.join();
	                    //System.out.println("... Gone");
	                    log.info("Thread end..");
	                } catch (InterruptedException ie) {
	                    ie.printStackTrace();
	                }
	            }
	        }
	        )
        		);
		
		// TODO Auto-generated method stub
		log.info("----- NEW ENINGE KUGO STARTED --------");
		
		//obj = new KugoMain();
		
		//creating a new instance of timer class
        Timer timer = new Timer();
        TimerTask taskInquiry = new KugoHelperInquiry();
        //TimerTask task = new KugoHelperCloseAccount();
        TimerTask taskOpenAccount = new KugoHelperOpenAccount();
        
        //instance of date object for fixed-rate execution
        Date date = new Date();
 
        timer.scheduleAtFixedRate(taskInquiry, date, 5000);
        timer.scheduleAtFixedRate(taskOpenAccount, date, 5000);
         
        //System.out.println("Timer running");
        try {
            synchronized (currThread) {
            	currThread.wait(); // This is the "work" this class is doing.
            }
            //System.out.println("Ok, ok! I'm leaving!");
            log.info("Prepare cleaning up the process...");
            // This stuff takes time
            //main thread resumes
            //and terminates the timer
            timer.cancel();
            log.info("TIMER TASK TERMINTAED..");
             
            //purge is used to remove all cancelled 
            //tasks from the timer'stack queue
            //System.out.println(timer.purge());
            log.info("TIMER TASK PURGING");
            
            //System.out.println("Done cleaning my stuff!");
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        //System.out.println("Bye!");
        log.info("Application stopped!.");
        
        

	}

}
