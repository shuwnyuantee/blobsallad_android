package com.shuwnyuan.android;

import com.shuwnyuan.android.BlobSalladView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

public class BlobSallad extends Activity {
	
    BlobSalladView blobSalladView;
    Button startButton;
    ImageView backImage;
    TextView statusText;
	
    public static final String LOG_TAG = "yuan_debug";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        blobSalladView = (BlobSalladView) findViewById(R.id.blob_sallad);
        
        statusText = (TextView) findViewById(R.id.text);
        backImage = (ImageView) findViewById(R.id.back_image);
        startButton = (Button) findViewById(R.id.start_button);
        
        startButton.setOnClickListener(new View.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		statusText.setVisibility(View.INVISIBLE);
        		backImage.setVisibility(View.INVISIBLE);
        		startButton.setVisibility(View.INVISIBLE);

        		// this will show something for BlobSalladView
        		blobSalladView.setVisibility(View.VISIBLE);
        		blobSalladView.setMode(BlobSalladView.READY);
        	}
        });
        
        Log.i(LOG_TAG, "just to debug :)");
    }
	
   
}