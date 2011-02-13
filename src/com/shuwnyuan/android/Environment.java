package com.shuwnyuan.android;

import android.graphics.Canvas;


public class Environment {
    private final double left;
    private  double right;
    private final double top;
    private final double bottom;

    public Environment(double x, double y, double w, double h) {
        this.left = x;
        this.right = x + w;
        this.top = y;
        this.bottom = y + h;
    }

    public Environment setWidth(double w) {
        return new Environment(this.left, this.top, w, this.bottom - this.top);
    }

    public Environment setHeight(double h) {
        return new Environment(this.left, this.top, this.right - this.left, h);
    }

    public boolean collision(Vector curPos, Vector prePos) {
        boolean collide = false;
        if (curPos.getX() < this.left) {
            curPos.setX(this.left);
            collide = true;
        }
        else if (curPos.getX() > this.right) {
            curPos.setX(this.right);
            collide = true;
        }
        else if (curPos.getY() < this.top) {
            curPos.setY(this.top);
            collide = true;
        }
        else if (curPos.getY() > this.bottom) {
            curPos.setY(this.bottom);
            collide = true;
        }
        return collide;
    }

    public void draw(Canvas canvas, double scaleFactor) {
    }
}
