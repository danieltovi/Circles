import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Canvas {

    private BufferedImage img;
    private RandomCircle[] circles = new RandomCircle[2]; // array to store the circle each thread wants to draw
    private final Object lock = new Object();

    /**
     * Determine whether or not a given thread's circle will intersect with the other thread's circle
     * @param tID the binary ID of the thread calling the function
     * @return true if the circles intersect and false otherwise
     */
    private boolean intersection(int tID) {
        // if other thread isn't drawing, don't bother checking for intersections
        if (circles[(tID+1)%2] == null) return false;

        // get coordinates
        int x1,y1,r1,x2,y2,r2,dist;
        x1 = circles[0].x0;
        y1 = circles[0].y0;
        r1 = circles[0].radius;
        x2 = circles[1].x0;
        y2 = circles[1].y0;
        r2 = circles[1].radius;

        // get cartesian distance between both circle origin points
        dist = (int)Math.ceil(Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)));

        // intersection if distance less than or equal to the sum of both radii
        return dist <= (r1+r2);
    }

    /**
     * Constructor to create a BufferedImage with the specified dimensions, initialised to all 0's
     * @param width canvas width (pixel count)
     * @param height canvas height (pixel count)
     */
    public Canvas(int width, int height) {
        img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);

        // set each pixel to rgb value 0
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                img.setRGB(i,j,0);
            }
        }
    }

    /**
     * Save the BufferedImage into a given path as a png
     * @param pathName pathName for the saved image
     * @throws IOException exception thrown on a failed write
     */
    public void saveImg(String pathName) throws IOException {
        File outputFile = new File(pathName);
        ImageIO.write(img, "png", outputFile);
    }

    /**
     * Concurrently draw a given circle's pixel map onto the bufferedImage
     * If both threads are drawing circles that don't intersect, they can do so in parallel.
     * Otherwise, a monitor solution is used to avoid a data race due to drawing overlapping circles.
     * @param c a RandomCircle
     * @param tID a thread's binary ID
     * @throws InterruptedException exception thrown if the synchronized wait is interrupted
     */
    public void drawCircle(RandomCircle c, int tID) throws InterruptedException {
        // add this thread's circle to be drawn to the circles array and wait on the lock if an intersection
        // with another circle being drawn is anticipated
        synchronized (lock) {
            circles[tID] = c;
            while(intersection(tID)) lock.wait();
        }

        int colour = c.colour;
        // iterate through the circle's pixel map and draw the filled circle pixel by pixel
        c.getPixelMap().forEach(pixel -> img.setRGB(pixel[0],pixel[1],colour));

        // notify the other thread that you are done drawing and set your circle-to-be-drawn to null
        synchronized (lock) {
            circles[tID] = null;
            lock.notify();
        }
    }

}
