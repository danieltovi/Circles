import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class RandomCircle {

    // circle dimensions
    public final int x0;
    public final int y0;
    public final int radius;
    public final int colour;

    // canvas dimensions
    private final int height;
    private final int width;

    /**
     * Generate a random pastel colour
     * @param rng a Random object
     * @return the RGB integer value of the randomly generated colour
     */
    private int getRandomColour(Random rng) {
        float hue = rng.nextFloat();
        float saturation = 0.9f;
        float luminance = 1.0f;
        return Color.getHSBColor(hue,saturation,luminance).getRGB();
    }

    /**
     * Constructor to build a random circle that fits the canvas parameters
     * @param width canvas width (pixel count)
     * @param height canvas height (pixel count)
     * @param maxR maximum circle radius (pixel count)
     */
    public RandomCircle(int width, int height, int maxR) {
        Random rng = new Random();
        this.width = width;
        this.height = height;

        x0 = rng.nextInt(width) + 1;
        y0 = rng.nextInt(height) + 1;
        radius = rng.nextInt(maxR) + 1;
        colour = getRandomColour(rng);
    }

    /**
     * Generate and return an ArrayList of x/y coordinates that make up the filled circle on the canvas
     * @return an ArrayList of 2-length integer arrays, trimmed to size
     */
    public ArrayList<int[]> getPixelMap() {
        ArrayList<int[]> pixelMap = new ArrayList<>(4*radius*radius); // initial capacity set to diameter^2

        // loop from 0 to rad instead of -rad to rad and use symmetry to complete the circle
        for (int x = 0; x <= radius; x++) {
            for (int y = 0; y <= radius; y++) {
                if(x*x + y*y > radius*radius) continue; // skip if point is not on the circle

                // add 4 symmetrical points to the map
                // use coordinates modulo dimensions to wrap out of bounds points onto other side of canvas
                // add canvas dimensions to mirrored coordinates before calculating modulo to ensure positive results
                pixelMap.add(new int[]{(x0 + x) % width,(y0 + y) % height});
                pixelMap.add(new int[]{(x0 + x) % width,(y0 - y + height) % height});
                pixelMap.add(new int[]{(x0 - x + width) % width,(y0 + y) % height});
                pixelMap.add(new int[]{(x0 - x + width) % width,(y0 - y + height) % height});
            }
        }

        pixelMap.trimToSize();
        return pixelMap;
    }

}
