package org.shuwnyuan.blobsallad;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;



public class Blob {
    private double x;
    private double y;
    List<Stick> sticks = new ArrayList<Stick>();
    List<PointMass> pointMasses = new ArrayList<PointMass>();
    List<Joint> joints = new ArrayList<Joint>();
    private PointMass middlePointMass;
    private double radius;
    private double drawFaceStyle;
    private double drawEyeStyle;
    private boolean selected;

    public Blob(double x, double y, double radius, int numPointMasses) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.drawFaceStyle = 1;
        this.drawEyeStyle = 1;
        this.selected = false;

        double f = 0.1;
        double low = 0.95, high = 1.05;
        double t;
        int i, p;

        for(i = 0, t = 0.0; i < numPointMasses; i++)
        {
            pointMasses.add(i, new PointMass(Math.cos(t) * radius + x, Math.sin(t) * radius + y, 1.0));
            t += 2.0 * Math.PI / numPointMasses;
        }

        this.middlePointMass = new PointMass(x, y, 1.0);

        this.pointMasses.get(0).setMass(4.0);
        this.pointMasses.get(1).setMass(4.0);

        for (i = 0; i < numPointMasses; i++)
        {
            this.sticks.add(i, new Stick(this.pointMasses.get(i), this.pointMasses.get(clampIndex(i + 1, numPointMasses))));
        }

        for (i = 0, p = 0; i < numPointMasses; i++)
        {
            this.joints.add(p++, new Joint(this.pointMasses.get(i), this.pointMasses.get(clampIndex(i + numPointMasses / 2 + 1, numPointMasses)), low, high));
            this.joints.add(p++, new Joint(this.pointMasses.get(i), this.middlePointMass, high * 0.9, low * 1.1)); // 0.8, 1.2 works
        }
    }

    private int clampIndex(int index, int maxIndex)
    {
        index += maxIndex;
        return index % maxIndex;
    }

    public PointMass getMiddlePointMass()
    {
        return this.middlePointMass;
    }

    public double getRadius()
    {
        return this.radius;
    }

    public void addBlob(Blob blob)
    {
      int index = this.joints.size();
      double dist;

      this.joints.add(index, new Joint(this.middlePointMass, blob.getMiddlePointMass(), 0.0, 0.0));
      dist = this.radius + blob.getRadius();
      this.joints.get(index).setDist(dist * 0.95, 0.0);
    }

    public double getXPos()
    {
        return this.middlePointMass.getXPos();
    }
    
    public double getYPos()
    {
        return this.middlePointMass.getYPos();
    }

    public void scale(double scaleFactor)
    {
        int i;

        for (i = 0; i < this.joints.size(); i++)
        {
            this.joints.get(i).scale(scaleFactor);
        }
        for (i = 0; i < this.sticks.size(); i++)
        {
            this.sticks.get(i).scale(scaleFactor);
        }
        this.radius *= scaleFactor;
    }

    public void move(double dt)
    {
        int i;

        for (i = 0; i < this.pointMasses.size(); i++)
        {
            this.pointMasses.get(i).move(dt);
        }
        this.middlePointMass.move(dt);
    }

    public void sc(Environment env)
    {
        int i, j;

        for (j = 0; j < 4; j++)
        {
            for (i = 0; i < this.pointMasses.size(); i++)
            {
                if(env.collision(this.pointMasses.get(i).getPos(), this.pointMasses.get(i).getPrevPos()) == true)
                {
                    this.pointMasses.get(i).setFriction(0.75);
                }
                else
                {
                    this.pointMasses.get(i).setFriction(0.01);
                }
            }
            for (i = 0; i < this.sticks.size(); i++)
            {
                this.sticks.get(i).sc(env);
            }

            for (i = 0; i < this.joints.size(); i++)
            {
                this.joints.get(i).sc();
            }
        }
    }

    public void setForce(Vector force)
    {
        int i;

        for (i = 0; i < this.pointMasses.size(); i++)
        {
            this.pointMasses.get(i).setForce(force);
        }
        this.middlePointMass.setForce(force);
    }

    public void addForce(Vector force)
    {
        int i;

        for (i = 0; i < this.pointMasses.size(); i++)
        {
            this.pointMasses.get(i).addForce(force);
        }
        this.middlePointMass.addForce(force);
        this.pointMasses.get(0).addForce(force);
        this.pointMasses.get(0).addForce(force);
        this.pointMasses.get(0).addForce(force);
        this.pointMasses.get(0).addForce(force);
    }

    public void moveTo(double x, double y)
    {
        int i;
        Vector blobPos;

        blobPos = this.middlePointMass.getPos();
        x -= blobPos.getX();
        y -= blobPos.getY();

        for (i = 0; i < this.pointMasses.size(); i++)
        {
            blobPos = this.pointMasses.get(i).getPos();
            blobPos.addX(x);
            blobPos.addY(y);
        }
        blobPos = this.middlePointMass.getPos();
        blobPos.addX(x);
        blobPos.addY(y);
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public void drawEars(Canvas canvas, double scaleFactor)
    {
        //ctx.strokeStyle = "#000000";
        //ctx.fillStyle = "#FFFFFF";
        //ctx.lineWidth = 2;
        //
        //ctx.beginPath();
        //ctx.moveTo((-0.55 * this.radius) * scaleFactor, (-0.35 * this.radius) * scaleFactor);
        //ctx.lineTo((-0.52 * this.radius) * scaleFactor, (-0.55 * this.radius) * scaleFactor);
        //ctx.lineTo((-0.45 * this.radius) * scaleFactor, (-0.40 * this.radius) * scaleFactor);
        //ctx.fill();
        //ctx.stroke();
        //
        //ctx.beginPath();
        //ctx.moveTo((0.55 * this.radius) * scaleFactor, (-0.35 * this.radius) * scaleFactor);
        //ctx.lineTo((0.52 * this.radius) * scaleFactor, (-0.55 * this.radius) * scaleFactor);
        //ctx.lineTo((0.45 * this.radius) * scaleFactor, (-0.40 * this.radius) * scaleFactor);
        //ctx.fill();
        //ctx.stroke();
    }

    public void drawHappyEyes1(Canvas canvas, double scaleFactor)
    {
        //ctx.lineWidth = 1;
        //ctx.fillStyle = "#FFFFFF";
        //ctx.beginPath();
        //ctx.arc((-0.15 * this.radius) * scaleFactor,
        //      (-0.20 * this.radius) * scaleFactor,
        //      this.radius * 0.12 * scaleFactor, 0, 2.0 * Math.PI, false);
        //ctx.fill();
        //ctx.stroke();
        //
        //ctx.beginPath();
        //ctx.arc(( 0.15 * this.radius) * scaleFactor,
        //      (-0.20 * this.radius) * scaleFactor,
        //      this.radius * 0.12 * scaleFactor, 0, 2.0 * Math.PI, false);
        //ctx.fill();
        //ctx.stroke();
        //
        //ctx.fillStyle = "#000000";
        //ctx.beginPath();
        //ctx.arc((-0.15 * this.radius) * scaleFactor,
        //      (-0.17 * this.radius) * scaleFactor,
        //      this.radius * 0.06 * scaleFactor, 0, 2.0 * Math.PI, false);
        //ctx.fill();
        //
        //ctx.beginPath();
        //ctx.arc(( 0.15 * this.radius) * scaleFactor,
        //      (-0.17 * this.radius) * scaleFactor,
        //      this.radius * 0.06 * scaleFactor, 0, 2.0 * Math.PI, false);
        //ctx.fill();

    	
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setColor(android.graphics.Color.BLACK);
    	paint.setStyle(Paint.Style.STROKE);
    	paint.setStrokeWidth(1);
    	
    	float x = (float)((-0.15 * this.radius) * scaleFactor);
    	float y = (float)((-0.20 * this.radius) * scaleFactor);
    	float r = (float)(this.radius * 0.12 * scaleFactor);
    	canvas.drawCircle(x, y, r, paint);
    	
    	x = (float)((0.15 * this.radius) * scaleFactor);
    	y = (float)((-0.20 * this.radius) * scaleFactor);
    	canvas.drawCircle(x, y, r, paint);
    	
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);
    	x = (float)((-0.15 * this.radius) * scaleFactor);
    	y = (float)((-0.17 * this.radius) * scaleFactor);
    	r = (float)(this.radius * 0.06 * scaleFactor);
    	canvas.drawCircle(x, y, r, paint);
    	
    	x = (float)((0.15 * this.radius) * scaleFactor);
    	y = (float)((-0.17 * this.radius) * scaleFactor);
    	canvas.drawCircle(x, y, r, paint);
    }

    public void drawHappyEyes2(Canvas canvas, double scaleFactor)
    {
        //ctx.lineWidth = 1;
        //ctx.fillStyle = "#FFFFFF";
        //ctx.beginPath();
        //ctx.arc((-0.15 * this.radius) * scaleFactor,
        //      (-0.20 * this.radius) * scaleFactor,
        //      this.radius * 0.12 * scaleFactor, 0, 2.0 * Math.PI, false);
        //ctx.stroke();
        //
        //ctx.beginPath();
        //ctx.arc(( 0.15 * this.radius) * scaleFactor,
        //      (-0.20 * this.radius) * scaleFactor,
        //      this.radius * 0.12 * scaleFactor, 0, 2.0 * Math.PI, false);
        //ctx.stroke();
        //
        //ctx.lineWidth = 1;
        //ctx.beginPath();
        //ctx.moveTo((-0.25   * this.radius) * scaleFactor,
        //         (-0.20 * this.radius) * scaleFactor);
        //ctx.lineTo((-0.05 * this.radius) * scaleFactor,
        //         (-0.20 * this.radius) * scaleFactor);
        //ctx.stroke();
        //
        //ctx.beginPath();
        //ctx.moveTo(( 0.25   * this.radius) * scaleFactor,
        //         (-0.20 * this.radius) * scaleFactor);
        //ctx.lineTo(( 0.05 * this.radius) * scaleFactor,
        //         (-0.20 * this.radius) * scaleFactor);
        //ctx.stroke();

    	
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setColor(android.graphics.Color.BLACK);
    	paint.setStyle(Paint.Style.STROKE);
    	paint.setStrokeWidth(1);
    	
    	float x = (float)((-0.15 * this.radius) * scaleFactor);
    	float y = (float)((-0.20 * this.radius) * scaleFactor);
    	float r = (float)(this.radius * 0.12 * scaleFactor);
    	canvas.drawCircle(x, y, r, paint);
    	
    	x = (float)((0.15 * this.radius) * scaleFactor);
    	y = (float)((-0.20 * this.radius) * scaleFactor);
    	canvas.drawCircle(x, y, r, paint);

    	Path path = new Path();
    	
    	x = (float)(-0.25   * this.radius * scaleFactor);
    	y = (float)(-0.20 * this.radius * scaleFactor);
    	path.moveTo(x, y);
    	x = (float)(-0.05 * this.radius * scaleFactor);
    	y = (float)(-0.20 * this.radius * scaleFactor);
    	path.lineTo(x, y);
    	canvas.drawPath(path, paint);
    	
    	x = (float)(0.25   * this.radius * scaleFactor);
    	y = (float)(-0.20 * this.radius * scaleFactor);
    	path.moveTo(x, y);
    	x = (float)(0.05 * this.radius * scaleFactor);
    	y = (float)(-0.20 * this.radius * scaleFactor);
    	path.lineTo(x, y);
    	canvas.drawPath(path, paint);
    }

    public void drawHappyFace1(Canvas canvas, double scaleFactor)
    {
        //ctx.lineWidth = 2;
        //ctx.strokeStyle = "#000000";
        //ctx.fillStyle = "#000000";
        //ctx.beginPath();
        //ctx.arc(0.0, 0.0,
        //this.radius * 0.25 * scaleFactor, 0, Math.PI, false);
        //ctx.stroke();

    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(android.graphics.Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		  
		Path path = new Path();
		float x1 = -1 * (float)(this.radius * 0.25 * scaleFactor);
		float y1 = -1 * (float)(this.radius * 0.25 * scaleFactor);
		float x2 = (float)(this.radius * 0.25 * scaleFactor);
		float y2 = (float)(this.radius * 0.25 * scaleFactor);
		
		RectF rect1 = new RectF(x1, y1, x2, y2);
		path.addArc(rect1, 0, 180);
		canvas.drawPath(path, paint);
    }
    
    public void drawHappyFace2(Canvas canvas, double scaleFactor)
    {
        //ctx.lineWidth = 2;
        //ctx.strokeStyle = "#000000";
        //ctx.fillStyle = "#000000";
        //ctx.beginPath();
        //ctx.arc(0.0, 0.0,
        //this.radius * 0.25 * scaleFactor, 0, Math.PI, false);
        //ctx.fill();

    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(android.graphics.Color.BLACK);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(2);
		  
		Path path = new Path();
		float x1 = -1 * (float)(this.radius * 0.25 * scaleFactor);
		float y1 = -1 * (float)(this.radius * 0.25 * scaleFactor);
		float x2 = (float)(this.radius * 0.25 * scaleFactor);
		float y2 = (float)(this.radius * 0.25 * scaleFactor);
		
		RectF rect1 = new RectF(x1, y1, x2, y2);
		path.addArc(rect1, 0, 180);
		canvas.drawPath(path, paint);
    }

    public void drawOohFace(Canvas canvas, double scaleFactor)
    {
        //ctx.lineWidth = 2;
        //ctx.strokeStyle = "#000000";
        //ctx.fillStyle = "#000000";
        //ctx.beginPath();
        //ctx.arc(0.0, (0.1 * this.radius) * scaleFactor,
        //this.radius * 0.25 * scaleFactor, 0, Math.PI, false);
        //ctx.fill();
        //
        //ctx.beginPath();
        //
        //ctx.moveTo((-0.25 * this.radius) * scaleFactor, (-0.3 * this.radius) * scaleFactor);
        //ctx.lineTo((-0.05 * this.radius) * scaleFactor, (-0.2 * this.radius) * scaleFactor);
        //ctx.lineTo((-0.25 * this.radius) * scaleFactor, (-0.1 * this.radius) * scaleFactor);
        //
        //ctx.moveTo((0.25 * this.radius) * scaleFactor, (-0.3 * this.radius) * scaleFactor);
        //ctx.lineTo((0.05 * this.radius) * scaleFactor, (-0.2 * this.radius) * scaleFactor);
        //ctx.lineTo((0.25 * this.radius) * scaleFactor, (-0.1 * this.radius) * scaleFactor);
        //
        //ctx.stroke();

    	// draw mouth
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(android.graphics.Color.BLACK);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(2);

		float x1 = (float)(-1 * this.radius * 0.25 * scaleFactor);
		float y1 = (float)(-0.15 * this.radius * scaleFactor);
		float x2 = (float)(this.radius * 0.25 * scaleFactor);
		float y2 = (float)(0.35 * this.radius * scaleFactor);
		
		RectF rect1 = new RectF(x1, y1, x2, y2);
		Path path = new Path();
		path.addArc(rect1, 0, 180);
		canvas.drawPath(path, paint);
		
		//draw eyes
		paint.setStyle(Paint.Style.STROKE);
		x1 = (float)(-0.25 * this.radius * scaleFactor); 
		y1 = (float)(-0.3 * this.radius * scaleFactor);
		x2 = (float)(-0.05 * this.radius * scaleFactor);
		y2 = (float)(-0.2 * this.radius * scaleFactor);
		canvas.drawLine(x1, y1, x2, y2, paint);
    	
		x1 = x2;
		y1 = y2;
		x2 = (float)(-0.25 * this.radius * scaleFactor);
		y2 = (float)(-0.1 * this.radius * scaleFactor);
		canvas.drawLine(x1, y1, x2, y2, paint);
		
		
		x1 = (float)(0.25 * this.radius * scaleFactor);
		y1 = (float)(-0.3 * this.radius * scaleFactor);
		x2 = (float)(0.05 * this.radius * scaleFactor);
		y2 = (float)(-0.2 * this.radius * scaleFactor);
		canvas.drawLine(x1, y1, x2, y2, paint);
		
		x1 = x2;
		y1 = y2;
		x2 = (float)(0.25 * this.radius * scaleFactor);
		y2 = (float)(-0.1 * this.radius * scaleFactor);
    	canvas.drawLine(x1, y1, x2, y2, paint);
    }

    public void drawFace(Canvas canvas, double scaleFactor)
    {
        if (this.drawFaceStyle == 1 && Math.random() < 0.05)
        {
            this.drawFaceStyle = 2;
        }
        else if (this.drawFaceStyle == 2 && Math.random() < 0.1)
        {
            this.drawFaceStyle = 1;
        }

        if (this.drawEyeStyle == 1 && Math.random() < 0.025)
        {
            this.drawEyeStyle = 2;
        }
        else if(this.drawEyeStyle == 2 && Math.random() < 0.3)
        {
            this.drawEyeStyle = 1;
        }

        if (this.middlePointMass.getVelocity() > 0.004)
        {
            this.drawOohFace(canvas, scaleFactor);
        }
        else
        {
            if (this.drawFaceStyle == 1)
            {
                this.drawHappyFace1(canvas, scaleFactor);
            }
            else
            {
                this.drawHappyFace2(canvas, scaleFactor);
            }

            if (this.drawEyeStyle == 1)
            {
                this.drawHappyEyes1(canvas, scaleFactor);
            }
            else
            {
                this.drawHappyEyes2(canvas, scaleFactor);
            }
        }
    }

    public PointMass getPointMass(int index)
    {
        index += this.pointMasses.size();
        index = index % this.pointMasses.size();
        return this.pointMasses.get(index);
    }

    public void drawBody(Canvas canvas, double scaleFactor)
    {
//        var i;
//
//        ctx.strokeStyle = "#000000";
//        if(this.selected == true)
//        {
//            ctx.fillStyle = "#FFCCCC";
//        }
//        else
//        {
//            ctx.fillStyle = "#FFFFFF";
//        }
//        ctx.lineWidth = 5;
//        ctx.beginPath();
//        ctx.moveTo(this.pointMasses[0].getXPos() * scaleFactor,
//        this.pointMasses[0].getYPos() * scaleFactor);
//
//        for(i = 0; i < this.pointMasses.length; i++)
//        {
//            var px, py, nx, ny, tx, ty, cx, cy;
//            var prevPointMass, currentPointMass, nextPointMass, nextNextPointMass;
//
//            prevPointMass = this.getPointMass(i - 1);
//            currentPointMass = this.pointMasses[i];
//            nextPointMass = this.getPointMass(i + 1);
//            nextNextPointMass = this.getPointMass(i + 2);
//
//            tx = nextPointMass.getXPos();
//            ty = nextPointMass.getYPos();
//
//            cx = currentPointMass.getXPos();
//            cy = currentPointMass.getYPos();
//
//            px = cx * 0.5 + tx * 0.5;
//            py = cy * 0.5 + ty * 0.5;
//
//            nx = cx - prevPointMass.getXPos() + tx - nextNextPointMass.getXPos();
//            ny = cy - prevPointMass.getYPos() + ty - nextNextPointMass.getYPos();
//
//            px += nx * 0.16;
//            py += ny * 0.16;
//
//            px = px * scaleFactor;
//            py = py * scaleFactor;
//
//            tx = tx * scaleFactor;
//            ty = ty * scaleFactor;
//
//            ctx.bezierCurveTo(px, py, tx, ty, tx, ty);
//        }
//
//        ctx.closePath();
//        ctx.stroke();
//        ctx.fill();


    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStrokeWidth(5);
    	
    	// draw outline
		paint.setColor(android.graphics.Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		
		Path path = new Path();
		float x1 = (float)(this.pointMasses.get(0).getXPos() * scaleFactor);
		float y1 = (float)(this.pointMasses.get(0).getYPos() * scaleFactor);
		path.moveTo(x1, y1);

		for (int i = 0; i < pointMasses.size(); i++)
		{
			double px, py, nx, ny, tx, ty, cx, cy;
			PointMass prevPointMass, currentPointMass, nextPointMass, nextNextPointMass;

			prevPointMass = this.getPointMass(i - 1);
			currentPointMass = this.pointMasses.get(i);
			nextPointMass = this.getPointMass(i + 1);
			nextNextPointMass = this.getPointMass(i + 2);

			tx = nextPointMass.getXPos();
			ty = nextPointMass.getYPos();
			cx = currentPointMass.getXPos();
			cy = currentPointMass.getYPos();

			px = cx * 0.5 + tx * 0.5;
			py = cy * 0.5 + ty * 0.5;

			nx = cx - prevPointMass.getXPos() + tx - nextNextPointMass.getXPos();
			ny = cy - prevPointMass.getYPos() + ty - nextNextPointMass.getYPos();

			px += nx * 0.16;
			py += ny * 0.16;

			px = px * scaleFactor;
			py = py * scaleFactor;

			tx = tx * scaleFactor;
			ty = ty * scaleFactor;

			path.cubicTo((float)px, (float)py, (float)tx, (float)ty, (float)tx, (float)ty);
		}
		canvas.drawPath(path, paint);
		
		// fill blob with color
		paint.setStyle(Paint.Style.FILL);
		if(this.selected == true)
		{
			paint.setARGB(0xFF, 0xFF, 0xCC, 0xCC);
		}
		else
		{
			paint.setColor(android.graphics.Color.WHITE);
		}
		canvas.drawPath(path, paint);
    }

    public void drawSimpleBody(Canvas canvas, double scaleFactor)
    {
        for (int i = 0; i < this.sticks.size(); i++)
        {
            this.sticks.get(i).draw(canvas, scaleFactor);
        }
    }

    public void draw(Canvas canvas, double scaleFactor)
    {
        this.drawBody(canvas, scaleFactor);

        canvas.save();
        canvas.translate((float)(this.middlePointMass.getXPos() * scaleFactor), (float)((this.middlePointMass.getYPos() - 0.35 * this.radius) * scaleFactor));

        Vector up = new Vector(0.0, -1.0);
        Vector ori = new Vector(0.0, 0.0);
        ori.set(this.pointMasses.get(0).getPos());
        ori.sub(this.middlePointMass.getPos());
        double ang = Math.acos(ori.dotProd(up) / ori.length());
        if (ori.getX() < 0.0)
        {
            canvas.rotate((float)-ang);
        }
        else
        {
            canvas.rotate((float)ang);
        }

        this.drawFace(canvas, scaleFactor);

        canvas.restore();
    }
}
