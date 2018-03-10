package ncc4702.hpu.edu.usszhum;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by ReadUnpingco on 3/10/18.
 */

public class Hallway extends GameElement{
    private String TAG = "HALLWAY";
    private GameView view;
    private Paint paint = new Paint();
    private int height;
    private int width = 5;
    private ArrayList<int[]> hallElements;

    //constructor
    public Hallway(GameView view, int color, int soundId,
                   int tileSide, ArrayList<int[]> hallElements){
        super(view, color, soundId, 0, 0, view.getScreenWidth() , view.getHeight()+336, 1);
        this.view = view;
        this.hallElements = hallElements;
        Log.d(TAG, "screen:" + view.getScreenWidth() + ":" + view.getScreenHeight());
        Log.d(TAG, "screen:" + view.getWidth() + ":" + view.getHeight());
    }

    public int getDistanceRemaining(){
        return this.hallElements.size();
    }

    //creates rows and fires at runner
    @Override
    public void update(double interval){
        //get row
        int[] movingRow = this.hallElements.get(0);
        this.hallElements.remove(0);

        String rowString = "";
        for(int i = 0; i < movingRow.length; i++){
            rowString += movingRow[i] + ",";
        }

        Log.d(TAG, "ROW:" + rowString);
        //calculate the cannonball velocity's x component
//        int velocityX = (int) (GameView.CANNONBALL_SPEED_PERCENT *
//                view.getScreenWidth() * Math.sin(barrelAngle));
//
//        //calculate the cannonball velocity's x component
//        int velocityY = (int) (CannonView.CANNONBALL_SPEED_PERCENT *
//                view.getScreenWidth() * -Math.cos(barrelAngle));
//
//        //calculate the Cannonball's radius
//        int radius = (int) (view.getScreenHeight() *
//                CannonView.CANNONBALL_RADIUS_PERCENT);
//
//        //construct Cannonball and position it in the
//        cannonball = new Cannonball(view, Color.BLACK,
//                CannonView.CANNON_SOUND_ID, -radius,
//                view.getScreenHeight() / 2 - radius,
//                radius, velocityX, velocityY);
//
//        cannonball.playSound();
    }
//    returns the Cannonball that this Cannon fired
//    public Cannonball getCannonball(){
//        return cannonball;
//    }

    //removes the Cannonball from the game
//    public void removeGameElement(){
//        cannonball = null;
//    }

}
