package com.sumaengine;

import java.util.TimerTask;

class MyTimerTask extends TimerTask {

    private Object lock;

    public MyTimerTask(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        System.out.println("inside timer");
        synchronized (lock) {
            lock.notifyAll();
        }
    }

}
