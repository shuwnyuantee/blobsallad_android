package org.shuwnyuan.blobsallad;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;


public class BlobSalladView extends View {

	private int mMode = NOT_READY;
	public static final int NOT_READY = 0;
    public static final int READY = 1;
    
    private Environment env = new Environment(0.2, 0.2, 2.6, 1.6);
    private final double scaleFactor = 150.0;
    private BlobCollective blobColl = new BlobCollective(1.0, 1.0, 200);
    private Vector gravity = new Vector(0.0, 10.0);
    private volatile boolean stopped = false;
    private Point savedMouseCoords = null;
    private Point selectOffset = null;
    private Timer timer;
    
    
	public BlobSalladView(Context context) {
		super(context);
		initView();
	}

	public BlobSalladView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView()
	{
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
//		setClickable(true);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) 
	{
		super.onLayout(changed, left, top, right, bottom);
		try {
			if (mMode == READY)
			{
		        start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startUpdateTimer()
	{
		if (timer != null)
		{
			timer.cancel();
		}
		
		timer = new Timer();
		timer.schedule(new UpdateTask(), 0, 30);
	}
	
	public class UpdateTask extends TimerTask {
		@Override
		public void run() {
			update();
			// cause an invalidate to happen on a subsequent cycle through the event loop
			// use this to invalidate View from a non-UI thread.
			// onDraw will be called sometime in the future
			postInvalidate();
			
			if (stopped) {
                cancel();
            }
		}
	}
	
	private void toggleGravity()
    {
        if (gravity.getY() > 0.0)
        {
            gravity.setY(0.0);
        }
        else
        {
            gravity.setY(10.0);
        }
    }

    public void stop()
    {
        stopped = true;
    }

    public void start()
    {
        stopped = false;
        startUpdateTimer();
    }

    public void update()
    {
        double dt = 0.05;

        if (savedMouseCoords != null && selectOffset != null)
        {
            blobColl.selectedBlobMoveTo(savedMouseCoords.getX() - selectOffset.getX(), savedMouseCoords.getY() - selectOffset.getY());
        }

        blobColl.move(dt);
        blobColl.sc(env);
        blobColl.setForce(gravity);
    }
	
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        final int width = this.getWidth();
        final int height = this.getHeight();

        // View is resizable. We need to update accordingly.
        // Our offset is 0.2 (40 / scaleFactor)
        // Hence, offset the entire size with 40 * 2.
        
//        this.env = env.setWidth((width - 80.0)/ scaleFactor).setHeight((height - 80.0)/ scaleFactor);
        this.env = env.setWidth((width - 40.0)/ scaleFactor).setHeight((height - 40.0)/ scaleFactor);
        env.draw(canvas, scaleFactor);
        blobColl.draw(canvas, scaleFactor);
    }

    public void setMode(int newMode)
	{
		mMode = newMode;
		if (mMode == READY)
		{
			this.requestLayout();
		}
	}
    
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event)
    {
    	switch(keyCode) {
	        case KeyEvent.KEYCODE_DPAD_LEFT:
	            blobColl.addForce(new Vector(-50.0, 0.0));
	            break;
	        case KeyEvent.KEYCODE_DPAD_UP:
	            blobColl.addForce(new Vector(0, -50.0));
	            break;
	        case KeyEvent.KEYCODE_DPAD_RIGHT:
	            blobColl.addForce(new Vector(50.0, 0.0));
	            break;
	        case KeyEvent.KEYCODE_DPAD_DOWN:
	            blobColl.addForce(new Vector(0.0, 50.0));
	            break;
	        case KeyEvent.KEYCODE_J:
	            blobColl.join();
	            return true;
	        case KeyEvent.KEYCODE_H:
	            blobColl.split();
	            return true;
	        case KeyEvent.KEYCODE_G:
	            toggleGravity();
	            return true;
	        default:
	            break;
	    }
    	
    	return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
    	final int action = event.getAction();
        switch (action) {
	        case MotionEvent.ACTION_DOWN: {
	        	Point mouseCoords;

                if(stopped == true)
                {
                    return true;
                }
                mouseCoords = getMouseCoords(event);
                if(mouseCoords == null)
                {
                    return true;
                }
	        	
                selectOffset = blobColl.selectBlob(mouseCoords.getX(), mouseCoords.getY());
	            break;
	        }
	            
	        case MotionEvent.ACTION_MOVE: {
	            Point mouseCoords;

                if (stopped == true)
                {
                    return true;
                }
                if (selectOffset == null)
                {
                    return true;
                }

                mouseCoords = getMouseCoords(event);

                if(mouseCoords == null)
                {
                    return true;
                }

                blobColl.selectedBlobMoveTo(mouseCoords.getX() - selectOffset.getX(), mouseCoords.getY() - selectOffset.getY());
                savedMouseCoords = mouseCoords;
                
	            // Invalidate to request a redraw
//                invalidate();

	            break;
	        }
	        
	        case MotionEvent.ACTION_UP: {
	        	blobColl.unselectBlob();
                savedMouseCoords = null;
                selectOffset = null;
	        	break;
	        }
        }
        return true;
    }
    
    public Point getMouseCoords(MotionEvent event)
    {
    	return new Point(event.getX()/scaleFactor, event.getY()/scaleFactor);
    }

}