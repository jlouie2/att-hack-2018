package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 3/9/2018.
 */

public class Trash extends Obstacle {//THIS OBSTACLE IS 1x2

    public Trash(GameView view, int color, int hitPenalty, int y, int width) {
        super(view, color, hitPenalty, y, width,1,2);
    }
}