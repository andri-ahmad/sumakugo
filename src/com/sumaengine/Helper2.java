package com.sumaengine;

import java.util.Date;
import java.util.TimerTask;

class Helper2 extends TimerTask
{
	public static int i = 0;
	
    public void run()
    {

    	System.out.println(new Date(System.currentTimeMillis()).toString() + "Task 2 Running 1 detik");
    	wait(1000);
    	System.out.println(new Date(System.currentTimeMillis()).toString() + "Task 2 Running 2 detik");
    	wait(1000);
    	System.out.println(new Date(System.currentTimeMillis()).toString() + "Task 2 Done");
    	
    	
    	
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

