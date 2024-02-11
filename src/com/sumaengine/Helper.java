package com.sumaengine;


import java.util.TimerTask;


class Helper extends TimerTask
{
	public static int i = 0;
	
    public void run()
    {
        System.out.println("Timer ran " + ++i);
        if(i == 4)
        {
            synchronized(TimerTesting3.obj)
            {
            	TimerTesting3.obj.notify();
            }
        }
    }
}