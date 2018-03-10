package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

//DON'T NEED THE CLASS JUST THINGS IN THE CLASS
public class Cannon {
    private int baseRadius;//Cannon base's radius
    private int barrelLength; //Cannon barrel's length
    private Point barrelEnd = new Point(); //endpoint of Cannon's barrel
    private double barrelAngle; //angle of the Cannon's barrel
    private Player mPlayer;; //the Cannon's Player
    private Paint paint = new Paint();// Paint used to draw the cannon
    private GameView view; //view containing the Cannon

    //constructor
    public Cannon(GameView view, int baseRadius, int barrelLength, int barrelWidth){
        this.view = view;
        this.baseRadius = baseRadius;
        this.barrelLength = barrelLength;
        paint.setStrokeWidth(barrelWidth);
        paint.setColor(Color.BLACK);
        align(Math.PI/2);// Cannon barrel facing straight right
    }

    //aligns the Cannon's barrel to the given angle
    public void align(double barrelAngle){
        this.barrelAngle = barrelAngle;
        barrelEnd.x = (int)(barrelLength*Math.sin(barrelAngle));
        barrelEnd.y = (int)(-barrelLength*Math.cos(barrelAngle))+view.getScreenHeight()/2;//still don't know where this method comes from
    }

    //creates and fires Player in the direction Cannon points
    public void fireCannonball(){
        //calculate the Player velocity's x component
        int velocityX = (int) (GameView.CANNONBALL_SPEED_PERCENT*view.getScreenWidth()*Math.sin(barrelAngle));

        //calculate the Player velocity's y component
        int velocityY = (int)(GameView.CANNONBALL_SPEED_PERCENT*view.getScreenWidth()*-Math.cos(barrelAngle));

        //clculate the Player's radius
        int radius = (int) (view.getScreenHeight()* GameView.CANNONBALL_RADIUS_PERCENT);

        //construct Player and position it in the Cannon
        //mPlayer = new Player(view, Color.BLACK, GameView.CANNON_SOUND_ID, -radius, view.getScreenHeight()/2 -radius, radius, velocityX, velocityY);

        mPlayer.playSound();//play fire Player sound
    }

    //draws the Cannon on the Canvas
    public void draw(Canvas canvas){
        //draw cannon barrel
        canvas.drawLine(0,view.getScreenHeight()/2, barrelEnd.x, barrelEnd.y, paint);

        //draw cannon base
        canvas.drawCircle(0,(int) view.getScreenHeight()/2, (int) baseRadius, paint);

    }

    //returns the Player that this Cannon fired
    public Player getPlayer(){
        return mPlayer;
    }

    //removes the Player from the game
    public void removeCannonball(){
        mPlayer = null;
    }


}
