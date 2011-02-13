package com.shuwnyuan.android;

import android.graphics.Canvas;
import android.graphics.Paint;


public class Stick {
    private PointMass pointMassA;
    private PointMass pointMassB;
    private double length;
    private double lengthSquared;
    private Vector delta;

    public Stick(PointMass pointMassA, PointMass pointMassB) {
        this.pointMassA = pointMassA;
        this.pointMassB = pointMassB;
        this.length = this.pointMassDist(pointMassA, pointMassB);
        this.lengthSquared = this.length * this.length;
        this.delta = new Vector(0.0, 0.0);
    }

    private double pointMassDist(PointMass pointMassA, PointMass pointMassB)
    {
        double aXbX = pointMassA.getXPos() - pointMassB.getXPos();
        double aYbY = pointMassA.getYPos() - pointMassB.getYPos();
        return Math.sqrt(aXbX * aXbX + aYbY * aYbY);
    }

    public PointMass getPointMassA()
    {
        return this.pointMassA;
    }

    public PointMass getPointMassB()
    {
        return this.pointMassB;
    }
    
    public void scale(double scaleFactor)
    {
        this.length *= scaleFactor;
        this.lengthSquared = this.length * this.length;
    }

    // Key function.
    public void sc(Environment env) {
        double dotProd, scaleFactor;
        Vector pointMassAPos, pointMassBPos;

        pointMassAPos = this.pointMassA.getPos();
        pointMassBPos = this.pointMassB.getPos();

        this.delta.set(pointMassBPos);
        this.delta.sub(pointMassAPos);

        dotProd = this.delta.dotProd(this.delta);

        scaleFactor = this.lengthSquared / (dotProd + this.lengthSquared) - 0.5;
        this.delta.scale(scaleFactor);

        pointMassAPos.sub(this.delta);
        pointMassBPos.add(this.delta);
    }

    public void setForce(Vector force)
    {
        this.pointMassA.setForce(force);
        this.pointMassB.setForce(force);
    }

    public void addForce(Vector force)
    {
        this.pointMassA.addForce(force);
        this.pointMassB.addForce(force);
    }

    public void move(double dt)
    {
        this.pointMassA.move(dt);
        this.pointMassB.move(dt);
    }

    public void draw(Canvas canvas, double scaleFactor)
    {
        // this.pointMassA.draw(ctx, scaleFactor);
        // this.pointMassB.draw(ctx, scaleFactor);
        //
        // ctx.lineWidth = 3;
        // ctx.fillStyle = '#000000';
        // ctx.strokeStyle = '#000000';
        // ctx.beginPath();
        // ctx.moveTo(this.pointMassA.getXPos() * scaleFactor,
        //            this.pointMassA.getYPos() * scaleFactor);
        // ctx.lineTo(this.pointMassB.getXPos() * scaleFactor,
        //            this.pointMassB.getYPos() * scaleFactor);
        // ctx.stroke();

        this.pointMassA.draw(canvas, scaleFactor);
        this.pointMassB.draw(canvas, scaleFactor);

        Paint paint = new Paint();
        // set draw line to black colour, line width = 3
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        
        float startX = (float)this.pointMassA.getXPos() * (float)scaleFactor;
        float startY = (float)this.pointMassA.getYPos() * (float)scaleFactor;
        float stopX = (float)this.pointMassB.getXPos() * (float)scaleFactor;
        float stopY = (float)this.pointMassB.getYPos() * (float)scaleFactor;
        
    	canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}
