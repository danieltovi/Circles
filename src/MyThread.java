public class MyThread implements Runnable {

    private Thread t;
    private int ID;

    /**
     * Constructor to initialise a MyThread with a binary ID
     * @param tID value 0 or 1
     */
    public MyThread(int tID) {
        ID = tID % 2; // % 2 ensure values are either 0 or 1
    }

    /**
     * Override runnable run method to run Circles.threadJob
     */
    public void run() {
        try {
            Main.threadJob(ID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void join() {
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

}