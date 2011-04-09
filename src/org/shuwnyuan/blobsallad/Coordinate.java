package org.shuwnyuan.blobsallad;
import android.view.MotionEvent;

public class Coordinate {
	private double mScaleFactor;

	Coordinate (double scaleFactor) {
		mScaleFactor = scaleFactor;
	}
	
	public Point getCoords(MotionEvent event)
    {
    	return new Point(event.getX()/mScaleFactor, event.getY()/mScaleFactor);
    }
	
}
