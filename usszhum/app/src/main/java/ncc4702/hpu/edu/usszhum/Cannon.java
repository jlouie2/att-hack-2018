package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;


public class Cannon {
    private int baseRadius;//Cannon base's radius
    private int barrelLength; //Cannon barrel's length
    private Point barrelEnd = new Point(); //endpoint of Cannon's barrel
    private double barrelAngle; //angle of the Cannon's barrel
    private Cannonball cannonball;; //the Cannon's Cannonball
    private Paint paint = new Paint();// Paint used to draw the cannon
    private CannonView view; //view containing the Cannon

    //constructor
    public Cannon(CannonView view, int baseRadius, int barrelLength, int barrelWidth){
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

    //creates and fires Cannonball in the direction Cannon points
    public void fireCannonball(){
        //calculate the Cannonball velocity's x component
        int velocityX = (int) (CannonView.CANNONBALL_SPEED_PERCENT*view.getScreenWidth()*Math.sin(barrelAngle));

        //calculate the Cannonball velocity's y component
        int velocityY = (int)(CannonView.CANNONBALL_SPEED_PERCENT*view.getScreenWidth()*-Math.cos(barrelAngle));

        //clculate the Cannonball's radius
        int radius = (int) (view.getScreenHeight()*CannonView.CANNONBALL_RADIUS_PERCENT);

        //construct Cannonball and position it in the Cannon
        cannonball = new Cannonball(view, Color.BLACK, CannonView.CANNON_SOUND_ID, -radius, view.getScreenHeight()/2 -radius, radius, velocityX, velocityY);

        cannonball.playSound();//play fire Cannonball sound
    }

    //draws the Cannon on the Canvas
    public void draw(Canvas canvas){
        //draw cannon barrel
        canvas.drawLine(0,view.getScreenHeight()/2, barrelEnd.x, barrelEnd.y, paint);

        //draw cannon base
        canvas.drawCircle(0,(int) view.getScreenHeight()/2, (int) baseRadius, paint);

    }

    //returns the Cannonball that this Cannon fired
    public Cannonball getCannonball(){
        return cannonball;
    }

    //removes the Cannonball from the game
    public void removeCannonball(){
        cannonball = null;
    }


}
