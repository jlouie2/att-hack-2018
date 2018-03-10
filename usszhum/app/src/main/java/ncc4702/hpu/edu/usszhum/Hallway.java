package ncc4702.hpu.edu.usszhum;

import android.graphics.Paint;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by ReadUnpingco on 3/10/18.
 */

public class Hallway {
    private String TAG = "HALLWAY";
    private GameView view;
    private Paint paint = new Paint();
    private int height;
    private int width = 5;
    private int[][] rows;

    //constructor
    public Hallway(GameView view, LinkedList<int[]> hallData){
        this.view = view;
        this.height = hallData.size();
        Log.d(TAG, "height:" + this.height);
    }
}
