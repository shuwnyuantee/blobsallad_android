package org.shuwnyuan.blobsallad;

//import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;


public class BlobCollective {
    private int maxNum;
    private int numActive;
    private List<Blob> blobs = new ArrayList<Blob>();
    private Vector tmpForce;
    private Blob selectedBlob;

    public BlobCollective(double x, double y, int maxNum) {
        this.maxNum = maxNum;
        this.numActive = 1;
        this.tmpForce = new Vector(0.0, 0.0);
        this.selectedBlob = null;
        blobs.add(0, new Blob(x, y, 0.4, 8));
    }

    public void split()
    {
        int i, maxIndex = 0;
        double maxRadius = 0.0;
        int emptySlot;
        Blob motherBlob = null, newBlob;

        if (this.numActive == this.maxNum)
        {
            return;
        }

        emptySlot = this.blobs.size();
        for (i = 0; i < this.blobs.size(); i++)
        {
            if (this.blobs.get(i) != null && this.blobs.get(i).getRadius() > maxRadius)
            {
                maxRadius = this.blobs.get(i).getRadius();
                motherBlob = this.blobs.get(i);
            }
            else if (this.blobs.get(i) == null)
            {
                emptySlot = i;
            }
        }

        motherBlob.scale(0.75);
        newBlob = new Blob(motherBlob.getXPos(), motherBlob.getYPos(), motherBlob.getRadius(), 8);

        for (i = 0; i < this.blobs.size(); i++)
        {
            if(this.blobs.get(i) == null)
            {
                continue;
            }
            this.blobs.get(i).addBlob(newBlob);
            newBlob.addBlob(this.blobs.get(i));
        }
        this.blobs.add(emptySlot, newBlob);

        this.numActive++;
    }

    public void selectedBlobSplit()
    {
        int i = 0;
        int emptySlot;
        Blob motherBlob = null, newBlob;
        
        if(this.selectedBlob == null)
        {
            return;
        }
        
        if (this.numActive == this.maxNum)
        {
            return;
        }

        emptySlot = this.blobs.size();
        for (i = 0; i < this.blobs.size(); i++)
        {
            if (this.blobs.get(i) == null)
            {
                emptySlot = i;
            }
        }

        motherBlob = this.selectedBlob;
        motherBlob.scale(0.75);
        newBlob = new Blob(motherBlob.getXPos(), motherBlob.getYPos(), motherBlob.getRadius(), 8);

        for (i = 0; i < this.blobs.size(); i++)
        {
            if(this.blobs.get(i) == null)
            {
                continue;
            }
            this.blobs.get(i).addBlob(newBlob);
            newBlob.addBlob(this.blobs.get(i));
        }
        this.blobs.add(emptySlot, newBlob);

        this.numActive++;
    }
    
    public int findSmallest(int exclude)
    {
        double minRadius = 1000.0;
        int minIndex = 0;
        int i;

        for (i = 0; i < this.blobs.size(); i++)
        {
            if (i == exclude || this.blobs.get(i) == null)
            {
                continue;
            }
            if (this.blobs.get(i).getRadius() < minRadius)
            {
                minIndex = i;
                minRadius = this.blobs.get(i).getRadius();
            }
        }
        return minIndex;
    }

    public int findClosest(int exclude)
    {
        double minDist = 1000.0;
        int foundIndex = 0;
        double dist, aXbX, aYbY;
        int i;
        PointMass myPointMass, otherPointMass;

        myPointMass = this.blobs.get(exclude).getMiddlePointMass();
        for (i = 0; i < this.blobs.size(); i++)
        {
            if (i == exclude || this.blobs.get(i) == null)
            {
                continue;
            }

            otherPointMass = this.blobs.get(i).getMiddlePointMass();
            aXbX = myPointMass.getXPos() - otherPointMass.getXPos();
            aYbY = myPointMass.getYPos() - otherPointMass.getYPos();
            dist = aXbX * aXbX + aYbY * aYbY;
            if (dist < minDist)
            {
                minDist = dist;
                foundIndex = i;
            }
        }
        return foundIndex;
    }

    public void join()
    {
        int blob1Index, blob2Index;
        double r1, r2, r3;

        if(this.numActive == 1)
        {
        return;
        }

        blob1Index = this.findSmallest(-1);
        blob2Index = this.findClosest(blob1Index);

        r1 = this.blobs.get(blob1Index).getRadius();
        r2 = this.blobs.get(blob2Index).getRadius();
        r3 = Math.sqrt(r1 * r1 + r2 * r2);

        this.blobs.set(blob1Index, null);
        this.blobs.get(blob2Index).scale(0.945 * r3 / r2);

        this.numActive--;
    }

    public Point selectBlob(double x, double y)
    {
        int i;
        double minDist = 10000.0;
        PointMass otherPointMass;
        Point selectOffset = null;

        if(this.selectedBlob != null)
        {
            return null;
        }

        for (i = 0; i < this.blobs.size(); i++)
        {
            if(this.blobs.get(i) == null)
            {
                continue;
            }

            otherPointMass = this.blobs.get(i).getMiddlePointMass();
            double aXbX = x - otherPointMass.getXPos();
            double aYbY = y - otherPointMass.getYPos();
            double dist = aXbX * aXbX + aYbY * aYbY;
            if (dist < minDist)
            {
                minDist = dist;
                if (dist < this.blobs.get(i).getRadius() * 0.5)
                {
                    this.selectedBlob = this.blobs.get(i);
                    selectOffset = new Point(aXbX, aYbY);
                    // Shall we have early break here?
                }
            }
        }

        if (this.selectedBlob != null)
        {
            this.selectedBlob.setSelected(true);
        }
        return selectOffset;
    }

    public void unselectBlob()
    {
        if (this.selectedBlob == null)
        {
            return;
        }
        this.selectedBlob.setSelected(false);
        this.selectedBlob = null;
    }

    public void selectedBlobMoveTo(double x, double y)
    {
        if(this.selectedBlob == null)
        {
            return;
        }
        this.selectedBlob.moveTo(x, y);
    }

    public void move(double dt)
    {
        int i;

        for (i = 0; i < this.blobs.size(); i++)
        {
            if (this.blobs.get(i) == null)
            {
                continue;
            }
            this.blobs.get(i).move(dt);
        }
    }

    public void sc(Environment env)
    {
        int i;

        for (i = 0; i < this.blobs.size(); i++)
        {
            if (this.blobs.get(i) == null)
            {
                continue;
            }
            this.blobs.get(i).sc(env);
        }
    }
    
    public void setForce(Vector force)
    {
        int i;

        for (i = 0; i < this.blobs.size(); i++)
        {
            if (this.blobs.get(i) == null)
            {
                continue;
            }
            if (this.blobs.get(i) == this.selectedBlob)
            {
                this.blobs.get(i).setForce(new Vector(0.0, 0.0));
                continue;
            }
            this.blobs.get(i).setForce(force);
        }
    }

    public void addForce(Vector force)
    {
        int i;

        for (i = 0; i < this.blobs.size(); i++)
        {
            if(this.blobs.get(i) == null)
            {
                continue;
            }
            if(this.blobs.get(i) == this.selectedBlob)
            {
                continue;
            }
            this.tmpForce.setX(force.getX() * (Math.random() * 0.75 + 0.25));
            this.tmpForce.setY(force.getY() * (Math.random() * 0.75 + 0.25));
            this.blobs.get(i).addForce(this.tmpForce);
        }
    }

    public void draw(Canvas canvas, double scaleFactor) {
        int i;

        for (i = 0; i < this.blobs.size(); i++)
        {
            if (this.blobs.get(i) == null)
            {
                continue;
            }

            this.blobs.get(i).draw(canvas, scaleFactor);
        }
    }
}
