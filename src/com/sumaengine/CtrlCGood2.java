package com.sumaengine;

public class CtrlCGood2 {
	
	/**
     * How to do it correctly
     */
    public static void main(String...args) {

        final Thread itsMe = Thread.currentThread();
        //final Thread itsMe = CtrlCGood2();

        Runtime.getRuntime().addShutdownHook( new Thread(() -> {
	        System.out.println("\nOops! Trapped exit signal...");
	            synchronized (itsMe) {
	                itsMe.notify();
	                
	                try {
	                    itsMe.join();
	                    System.out.println("... Gone");
	                } catch (InterruptedException ie) {
	                    ie.printStackTrace();
	                }
	                
	            }
	        }
	        )
        		);
        
        
        System.out.println("Starting... Ctrl-C to stop.");
        
        try {
            synchronized (itsMe) {
                itsMe.wait(); // This is the "work" this class is doing.
            }
            System.out.println("Ok, ok! I'm leaving!");
            // This stuff takes time
            Thread.sleep(3000);
            System.out.println("Done cleaning my stuff!");
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println("Bye!");
    }

}
