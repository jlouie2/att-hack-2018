package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 3/9/2018.
 */

public class Boost extends Obstacle {

    public Boost(GameView view, int color, int hitPenalty, int y, int width) {
        super(view, color, 1,2, hitPenalty, y, width, (float) 1.0);
    }
}
