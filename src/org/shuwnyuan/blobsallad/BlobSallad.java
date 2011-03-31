package org.shuwnyuan.blobsallad;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnKeyListener;
import android.util.Log;
import android.service.wallpaper.WallpaperService;


public class BlobSallad extends WallpaperService {
	
	private final Handler mHandler = new Handler();
	// time between frames (msec)
	private static final int FRAME_INTERVAL = 20;
	
	
	@Override
    public Engine onCreateEngine() {
        return new BlobSalladEngine(this);
    }
 
    @Override
    public void onCreate() {
        super.onCreate();
        /* now let's wait until the debugger attaches */
        android.os.Debug.waitForDebugger();
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    
    public class BlobSalladEngine extends Engine implements OnGestureListener, OnDoubleTapListener { //OnKeyListener
    	private static final String TAG = "BlobSalladEngine";
    	private WallpaperService mContext = null;
    	private boolean	mVisible = true;
    	private int	mHeight = 0;
    	private int	mWidth = 0;
    	private float mXOff = 0f;
    	private float mYOff = 0f;
    	private int	mXOffPix = 0;
    	private int	mYOffPix = 0;
	    private MediaPlayer mSplitSound = null;
	    private MediaPlayer mJoinSound = null;
	    private Canvas mCanvas = null;
		private Bitmap mBitmap = null;
		private Bitmap mBgBitmap = null;
		private final Paint	mPaint = new Paint();
		private static final int DEFAULT_ALPHA = 70;
		
	    private Environment env = new Environment(0.2, 0.2, 2.6, 1.6);
	    private final double scaleFactor = 150.0;
	    private BlobCollective blobColl = new BlobCollective(1.0, 1.0, 200);
//	    private Vector gravity = new Vector(0.0, 10.0);
	    //set default to no gravity
	    private Vector gravity = new Vector(2.0, 5.0);
	    private Point savedMouseCoords = null;
	    private Point selectOffset = null;
	    private GestureDetector gestureDetector;
	    private int counter = 0;
	    private boolean isFirstTime = true;
	    

    	private final Runnable mDoNextFrame = new Runnable() {
    		public void run() {
    			doNextFrame();
    		}
    	};
    	
    	BlobSalladEngine(WallpaperService context) {
    		final Paint paint = mPaint; 
    		paint.setColor(0xffffffff);
    		paint.setAntiAlias(true);
    		paint.setStrokeWidth(2);
    		paint.setStrokeCap(Paint.Cap.ROUND);
    		paint.setStyle(Paint.Style.STROKE);
    		
    		mContext = context;
    		gestureDetector = new GestureDetector(mContext, this);
    		Log.v(TAG, "started");
    	}
 
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }
 
        @Override
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
    		return (super.onCommand(action, x, y, z, extras, resultRequested));
    	}
        
        @Override
        public void onDestroy() {
            super.onDestroy();
            this.releaseSound();
            mHandler.removeCallbacks(mDoNextFrame);
        }
        
        @Override
        public void onVisibilityChanged(boolean visible) {
        	mVisible = visible;
    		if (visible) {
    			this.initializeSound();
    			doNextFrame();
    		}
    		else {
    			this.releaseSound();
    			mHandler.removeCallbacks(mDoNextFrame);
    		}
        }
 
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        	super.onSurfaceChanged(holder, format, width, height);
    		mWidth = width;
    		mHeight = height;
    		
    		mCanvas = createCanvasBuffer(format, width, height);
    		loadBgBitmap();
    		return;
        }
 
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            return;
        }
 
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mDoNextFrame);
            return;
        }
 
        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
        	mXOff = xOffset;
    		mYOff = yOffset;
    		
    		if(mBgBitmap != null){
    			mXOffPix = (int) (mXOff * (mWidth - mBgBitmap.getWidth()));
    			mYOffPix = (int) (mYOff * (mHeight - mBgBitmap.getHeight()));
    		}
    		doNextFrame();
    		return;
        }
 
        Canvas createCanvasBuffer(int format, int width, int height) {
    		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    		Canvas newCanvas = new Canvas(mBitmap);
    		return(newCanvas);
    	}
        
        private void loadBgBitmap() {
    		Bitmap bmp = null;
    		if (mWidth > 0 && mHeight > 0)
    		{
    			// load a fixed background image
	    		bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sky);
	    		mBgBitmap = scaleBgBitmap(bmp);
	    		if(mBgBitmap != null){
					mXOffPix = (int) (mXOff * (mWidth - mBgBitmap.getWidth()));
					mYOffPix = (int) (mYOff * (mHeight - mBgBitmap.getHeight()));
				}
	    		// draw background image to canvas
	    		mCanvas.drawBitmap(mBgBitmap, mXOffPix, mYOffPix, mPaint);
    		}
    		return;
    	}
        
        private Bitmap scaleBgBitmap(Bitmap bitmap) {
    		Bitmap result = null;
    		int bw = bitmap.getWidth();
    		int bh = bitmap.getHeight();
    		
    		double s = (double)mHeight / (double)bh;
    		int newW = (int)(bw * s);
    		if(newW < mWidth){
    			newW = mWidth;
    		}
    		result = Bitmap.createScaledBitmap(bitmap, newW, mHeight, false);
    		return result;
    	}
        
        void doNextFrame() {
        	final SurfaceHolder holder = getSurfaceHolder();

            // draw background image if not exist
        	if (mBgBitmap == null) {
				loadBgBitmap();
			}
			
			// update blobs
			this.updateBlobColl();

        	Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                	c.drawBitmap(mBitmap, 0, 0, mPaint);
                	// draw blobs
                	this.draw(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }
        	
        	// schedule the next frame
    		mHandler.removeCallbacks(mDoNextFrame);
    		if (mVisible) {
    			mHandler.postDelayed(mDoNextFrame, FRAME_INTERVAL);
    		}
    		return;
        }
        
        public void updateBlobColl()
		{
		    double dt = 0.05;
		    
		    if (savedMouseCoords != null && selectOffset != null)
		    {
		        blobColl.selectedBlobMoveTo(savedMouseCoords.getX() - selectOffset.getX(), savedMouseCoords.getY() - selectOffset.getY());
		    }
		
		    blobColl.move(dt);
		    blobColl.sc(env);
		    
		    counter++;
		    if (counter >= 100)
		    {
		    	counter = 0;
		    	blobColl.setRandomForce();
		    }
		    
		    if (isFirstTime == true)
		    {
		    	blobColl.setForce(gravity);
		    	isFirstTime = false;
		    }
		}
       
        void draw(Canvas canvas)
        {
        	// View is resizable. We need to update accordingly.
        	// Our offset is 0.2 (40 / scaleFactor)
        	// Hence, offset the entire size with 40 * 2.
   
        	// this.env = env.setWidth((width - 80.0)/ scaleFactor).setHeight((height - 80.0)/ scaleFactor);
        	this.env = env.setWidth((mWidth - 40.0)/ scaleFactor).setHeight((mHeight - 40.0)/ scaleFactor);
        	env.draw(canvas, scaleFactor);
        	blobColl.draw(canvas, scaleFactor);
        }
        
        public void initializeSound()
        {
        	if (mSplitSound == null)
        	{
        		mSplitSound = MediaPlayer.create(mContext, R.raw.receive);
        	}
        	if (mJoinSound == null)
        	{
        		mJoinSound = MediaPlayer.create(mContext, R.raw.online);
        	}
        }
        
        public void releaseSound()
        {
        	if (mSplitSound != null)
        	{
        		mSplitSound.release();
        		mSplitSound = null;
        	}
        	if (mJoinSound != null)
        	{
        		mJoinSound.release();
        		mJoinSound = null;
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
        
        public Point getMouseCoords(MotionEvent event)
        {
        	return new Point(event.getX()/scaleFactor, event.getY()/scaleFactor);
        }
        
        // onDoubleTap 		- split blob
        // onFling 			- move blob
        // onSingleTap 		- on blob -> join blob
        //					  on empty area -> move blob to here
        // toggle gravity ??
        @Override
        public void onTouchEvent(MotionEvent event)
        {
            if (gestureDetector.onTouchEvent(event)) {
                return;
            }
            return;
        }
        
        // method for OnGestureListener
    	@Override
    	public boolean onDown(MotionEvent event)
    	{
    		return true;
    	}

    	@Override
    	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    	{
    		// select blob
    		Point mouseCoords;
            mouseCoords = getMouseCoords(e1);
            if (mouseCoords == null)
            {
                return true;
            }
            selectOffset = blobColl.selectBlob(mouseCoords.getX(), mouseCoords.getY());
            if (selectOffset == null)
            {
                return true;
            }
    			
    		// move blob
            mouseCoords = getMouseCoords(e2);
            if (mouseCoords == null)
            {
                return true;
            }
            blobColl.selectedBlobMoveTo(mouseCoords.getX() - selectOffset.getX(), mouseCoords.getY() - selectOffset.getY());
            savedMouseCoords = mouseCoords;

            // unselect blob
            blobColl.unselectBlob();
            savedMouseCoords = null;
            selectOffset = null;

            // draw here to avoid lag
            doNextFrame();
      		return true;
    	}

    	@Override
    	public void onLongPress(MotionEvent event)
    	{
    	}

    	@Override
    	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    	{
    		// select blob
    		Point mouseCoords;
            mouseCoords = getMouseCoords(e1);
            if (mouseCoords == null)
            {
                return true;
            }
            selectOffset = blobColl.selectBlob(mouseCoords.getX(), mouseCoords.getY());
            if (selectOffset == null)
            {
                return true;
            }
    			
    		// move blob
            mouseCoords = getMouseCoords(e2);
            if (mouseCoords == null)
            {
                return true;
            }
            blobColl.selectedBlobMoveTo(mouseCoords.getX() - selectOffset.getX(), mouseCoords.getY() - selectOffset.getY());
            savedMouseCoords = mouseCoords;

            // unselect blob
            blobColl.unselectBlob();
            savedMouseCoords = null;
            selectOffset = null;

            // draw here to avoid lag
            doNextFrame();
      		return true;
    	}

    	@Override
    	public void onShowPress(MotionEvent event)
    	{
    	}

    	@Override
    	public boolean onSingleTapUp(MotionEvent event)
    	{
    		return false;
    	}

    	// method for OnDoubleTapListener
    	@Override
    	public boolean onDoubleTap(MotionEvent event)
    	{
    		Point mouseCoords;
    		mouseCoords = getMouseCoords(event);
    		if(mouseCoords == null)
    		{
    		    return true;
    		}

    		selectOffset = blobColl.selectBlob(mouseCoords.getX(), mouseCoords.getY());
    		if (selectOffset == null)
            {
                return true;
            }
    		
    		blobColl.selectedBlobSplit();
    		blobColl.unselectBlob();
            selectOffset = null;
            
            // draw here to avoid lag
            doNextFrame();
            // play sound effect
            mSplitSound.start();
            
    		return true;
    	}

    	@Override
    	public boolean onDoubleTapEvent(MotionEvent arg0)
    	{
    		return false;
    	}

    	// on single tap
    	// 1) if tap on blob, blob will join with another nearest blob
    	// 2) if tap on empty area, a nearest blob will move to under your finger
    	@Override
    	public boolean onSingleTapConfirmed(MotionEvent event)
    	{
    		Point mouseCoords;
    		mouseCoords = getMouseCoords(event);
    		if(mouseCoords == null)
    		{
    		    return true;
    		}
    		
    		double x = mouseCoords.getX();
    		double y = mouseCoords.getY();

    		selectOffset = blobColl.selectBlob(x, y);
    		if (selectOffset != null)
    		{
    			//1) if tap on blob, blob will join with another nearest blob
        		if ( blobColl.selectedBlobJoin() )
        		{
        			// play sound effect, only if join 2 blobs successful
                    mJoinSound.start();
        		}
        		blobColl.unselectBlob();
                selectOffset = null;
    		}
    		else
            {
    			//2) if tap on empty area, a nearest blob will move to under your finger
        		selectOffset = blobColl.selectNearestBlob(x, y);
                if (selectOffset == null)
                {
                    return true;
                }

        		// move blob
                blobColl.selectedBlobMoveTo(x, y);
                savedMouseCoords = mouseCoords;

                // unselect blob
                blobColl.unselectBlob();
                savedMouseCoords = null;
                selectOffset = null;
            }
    		
    		// draw here to avoid lag
            doNextFrame();
    		return true;
    	}

//		@Override
//		public boolean onKey(View view, int keyCode, KeyEvent event) {
//			switch(keyCode) {
//		        case KeyEvent.KEYCODE_DPAD_LEFT:
//		            blobColl.addForce(new Vector(-50.0, 0.0));
//		            break;
//		        case KeyEvent.KEYCODE_DPAD_UP:
//		            blobColl.addForce(new Vector(0, -50.0));
//		            break;
//		        case KeyEvent.KEYCODE_DPAD_RIGHT:
//		            blobColl.addForce(new Vector(50.0, 0.0));
//		            break;
//		        case KeyEvent.KEYCODE_DPAD_DOWN:
//		            blobColl.addForce(new Vector(0.0, 50.0));
//		            break;
//		        case KeyEvent.KEYCODE_J:
//		            blobColl.join();
//		            return true;
//		        case KeyEvent.KEYCODE_H:
//		            blobColl.split();
//		            return true;
//		        case KeyEvent.KEYCODE_G:
//		            toggleGravity();
//		            return true;
//		        default:
//		            break;
//		    }
//	    	return false;
//		}
    }

   
}
