package org.shuwnyuan.blobsallad;

public class Joint {
    private PointMass pointMassA;
    private PointMass pointMassB;
    private Vector delta;
    Vector pointMassAPos;
    Vector pointMassBPos;
    double shortConst;
    double longConst;
    double scSquared;
    double lcSquared;

    public Joint(PointMass pointMassA, PointMass pointMassB, double shortConst, double longConst) {
        this.pointMassA = pointMassA;
        this.pointMassB = pointMassB;
        this.delta = new Vector(0.0, 0.0);
        this.pointMassAPos = pointMassA.getPos();
        this.pointMassBPos = pointMassB.getPos();

        this.delta.set(this.pointMassBPos);
        this.delta.sub(this.pointMassAPos);

        this.shortConst = this.delta.length() * shortConst;
        
        this.longConst = this.delta.length() * longConst;
        this.scSquared = this.shortConst * this.shortConst;
        this.lcSquared = this.longConst * this.longConst;
    }

    public void setDist(double shortConst, double longConst)
    {
        this.shortConst = shortConst;
        this.longConst = longConst;
        this.scSquared = this.shortConst * this.shortConst;
        this.lcSquared = this.longConst * this.longConst;
    }

    public void scale(double scaleFactor)
    {
        this.shortConst = this.shortConst * scaleFactor;
        this.longConst = this.longConst * scaleFactor;
        this.scSquared = this.shortConst * this.shortConst;
        this.lcSquared = this.longConst * this.longConst;
    }

    public void sc()
    {
        this.delta.set(this.pointMassBPos);
        this.delta.sub(this.pointMassAPos);

        double dp = this.delta.dotProd(this.delta);

        if(this.shortConst != 0.0 && dp < this.scSquared)
        {
            double scaleFactor;

            scaleFactor = this.scSquared / (dp + this.scSquared) - 0.5;

            this.delta.scale(scaleFactor);

            this.pointMassAPos.sub(this.delta);
            this.pointMassBPos.add(this.delta);
        }
        else if(this.longConst != 0.0 && dp > this.lcSquared)
        {
            double scaleFactor;

            scaleFactor = this.lcSquared / (dp + this.lcSquared) - 0.5;

            this.delta.scale(scaleFactor);

            this.pointMassAPos.sub(this.delta);
            this.pointMassBPos.add(this.delta);
        }
    }
}
