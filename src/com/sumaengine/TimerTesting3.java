package com.sumaengine;

import java.util.Timer;
import java.util.TimerTask;
import java.util.*;

public class TimerTesting3 {
	
	protected static TimerTesting3 obj;

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		obj = new TimerTesting3();
        
        //creating a new instance of timer class
        Timer timer = new Timer();
        TimerTask task = new Helper();
 
        //instance of date object for fixed-rate execution
        Date date = new Date();
 
        timer.scheduleAtFixedRate(task, date, 5000);
         
        System.out.println("Timer running");
        synchronized(obj)
        {
            //make the main thread wait
            obj.wait();
             
            //once timer has scheduled the task 4 times, 
            //main thread resumes
            //and terminates the timer
            timer.cancel();
             
            //purge is used to remove all cancelled 
            //tasks from the timer'stack queue
            System.out.println(timer.purge());
        }

	}

}
