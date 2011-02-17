package org.shuwnyuan.blobsallad;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class PointMass {

	private Vector cur;
    private Vector prev;
    private double mass;
    private Vector force;
    private Vector result;
    private double friction;
    public static final String LOG_TAG = "BlobSalladGame";

    public PointMass(double cx, double cy, double mass) {
        this.cur = new Vector(cx, cy);
        this.prev = new Vector(cx, cy);
        this.mass = mass;
        this.force = new Vector(0.0, 0.0);
        this.result = new Vector(0.0, 0.0);
        this.friction = 0.01;
    }

    public double getXPos() {
        return this.cur.getX();
    }

    public double getYPos() {
        return this.cur.getY();
    }

    public Vector getPos() {
        return this.cur;
    }

    public double getXPrevPos() {
        return this.prev.getX();
    }

    public double getYPrevPos() {
        return this.prev.getY();
    }

    public Vector getPrevPos() {
        return this.prev;
    }

    public void addXPos(double dx) {
        this.cur.addX(dx);
    }

    public void addYPos(double dy) {
        this.cur.addY(dy);
    }

    public void setForce(Vector force) {
        this.force.set(force);
    }

    public void addForce(Vector force) {
        this.force.add(force);
    }

    public double getMass() {
        return this.mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    // Key function.
    public void move(double dt) {
        double t, a, c, dtdt;

        dtdt = dt * dt;
        a = this.force.getX() / this.mass;
        c = this.cur.getX();
        t = (2.0 - this.friction) * c - (1.0 - this.friction) * this.prev.getX() + a * dtdt;
        this.prev.setX(c);
        this.cur.setX(t);
        
        Log.i(LOG_TAG, "X prev: " + c + ", cur: " + t);
        

        a = this.force.getY() / this.mass;
        c = this.cur.getY();
        t = (2.0 - this.friction) * c - (1.0 - this.friction) * this.prev.getY() + a * dtdt;
        this.prev.setY(c);
        this.cur.setY(t);
        
        Log.i(LOG_TAG, "Y prev: " + c + ", cur: " + t);
    }

    // Key function.
    public double getVelocity() {
        double cXpX, cYpY;
        cXpX = this.cur.getX() - this.prev.getX();
        cYpY = this.cur.getY() - this.prev.getY();
        return cXpX * cXpX + cYpY * cYpY;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public void draw(Canvas canvas, double scaleFactor) {
        // ctx.lineWidth = 2;
        // ctx.fillStyle = '#000000';
        // ctx.strokeStyle = '#000000';
        // ctx.beginPath();
        // ctx.arc(this.cur.getX() * scaleFactor,
        //         this.cur.getY() * scaleFactor,
        //         4.0, 0.0, Math.PI * 2.0, true);
        // ctx.fill();

	    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(0xFF000000);
	    paint.setStyle(Paint.Style.FILL_AND_STROKE);
	    paint.setStrokeWidth(2);
		
	    float x = (float)this.cur.getX()*(float)scaleFactor;
	    float y = (float)this.cur.getY()*(float)scaleFactor;
	    float r = 4;
	    
		canvas.drawCircle( x, y, r, paint);
    }
    
}
