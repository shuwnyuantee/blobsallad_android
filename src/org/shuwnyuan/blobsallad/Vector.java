package org.shuwnyuan.blobsallad;

public class Vector {

	private double x;
    private double y;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Vector)) {
            return false;
        }
        Vector v = (Vector)obj;
        return (x == v.x) && (y == v.y);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }
    
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void addX(double x) {
        this.setX(this.getX() + x);
    }

    public void addY(double y) {
        this.setY(this.getY() + y);
    }

    public void set(Vector v) {
        this.setX(v.getX());
        this.setY(v.getY());
    }

    public void add(Vector v) {
        this.setX(this.getX() + v.getX());
        this.setY(this.getY() + v.getY());
    }

    public void sub(Vector v) {
        this.setX(this.getX() - v.getX());
        this.setY(this.getY() - v.getY());
    }

    public double dotProd(Vector v) {
        return this.getX() * v.getX() + this.getY() * v.getY();
    }

    public double length() {
        return Math.sqrt(this.getX() * this.getX() + this.getY() * this.getY());
    }

    public void scale(double scaleFactor) {
        this.setX(this.getX() * scaleFactor);
        this.setY(this.getY() * scaleFactor);
    }

    @Override
    public String toString() {
        return " X: " + this.getX() + " Y: " + this.getY();
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }
}
