package com.sumaengine;


import java.util.Date;
import java.util.TimerTask;


class Helper extends TimerTask
{
	public static int i = 0;
	
    public void run()
    {

    	System.out.println(new Date(System.currentTimeMillis()).toString() + "Task 1 Running");
    	wait(10000);
    	System.out.println(new Date(System.currentTimeMillis()).toString() + "Task 1 Done");
    	
    	
    	/*
    	System.out.println("Nunggu 2 detik");
    	wait(1000);
    	System.out.println("Nunggu 3 detik");
    	wait(1000);
    	System.out.println("Nunggu 4 detik");
    	wait(1000);
    	System.out.println("Nunggu 5 detik");
    	wait(1000);
    	System.out.println("Nunggu 6 detik");
    	wait(1000);
    	System.out.println("Nunggu 7 detik");
    	wait(1000);
    	System.out.println("Nunggu 8 detik");
    	wait(1000);
    	System.out.println("Nunggu 9 detik");
    	wait(1000);
    	System.out.println("Nunggu 10 detik");
    	wait(1000);
    	*/
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