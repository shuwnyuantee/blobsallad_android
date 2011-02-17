package org.shuwnyuan.blobsallad;

import org.shuwnyuan.blobsallad.BlobSalladView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
        blobSalladView.setVisibility(View.INVISIBLE);
        
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
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.new_game:
//            newGame();
        	Toast.makeText(this, "You pressed the new game icon!", Toast.LENGTH_LONG).show();
        	
            return true;
        case R.id.quit:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
   
}
