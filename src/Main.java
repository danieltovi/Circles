public class Main {

    // The image constructed
    private static Canvas img;

    // Image dimensions
    private static int width = 1920;
    private static int height = 1080;

    // program arguments
    private static int r;
    private static int c;
    private static boolean multithreaded;

    // drawn circles counter
    private static int drawn = 0;
    private static final Object mutex = new Object();

    /**
     * Draw up to 'c' random circles of max radius 'r' on a Canvas either serially or over two threads.
     * The resulting image will be saved in the main directory as circles.png
     * The runtime of the method will be printed on the terminal
     * @param args Program arguments should have the form: int int boolean (i.e. maxRadii numCircles multithreaded?)
     */
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis(); // start timer

            // parse program arguments
            if (args.length < 3)
                throw new Exception("Missing arguments, only " + args.length + " were specified!");
            // arg 0 is the max radius
            r = Integer.parseInt(args[0]);
            // arg 1 is circle count
            c = Integer.parseInt(args[1]);
            // arg 2 is a boolean (false to run in serial, true to run on 2 threads)
            multithreaded = Boolean.parseBoolean(args[2]);

            // create a Canvas to draw circles on
            img = new Canvas(width,height);

            if (multithreaded) {
                // create two threads
                MyThread t0 = new MyThread(0);
                MyThread t1 = new MyThread(1);

                t0.start();
                t1.start();

                t0.join();
                t1.join();
            }
            else {
                // run threadJob on parent thread (will never have to wait for a lock)
                threadJob(0);
            }

            // save the resulting image
            img.saveImg("circles.png");

            // print out the runtime
            System.out.println("Runtime = " + (System.currentTimeMillis() - start));

        } catch (Exception e) {
            System.out.println("ERROR " +e);
            e.printStackTrace();
        }
    }

    /**
     * Job to be executed by each thread until the requisite number of circles drawn is met
     * @param tID a thread's binary ID
     * @throws InterruptedException exception thrown on failed drawCircle
     */
    public static void threadJob(int tID) throws InterruptedException {
        while (true) {
            // "atomically" test and increment drawn
            synchronized (mutex) {
                if (drawn < c) drawn++;
                else break;
            }

            img.drawCircle(new RandomCircle(width, height, r), tID);
        }
    }

}
