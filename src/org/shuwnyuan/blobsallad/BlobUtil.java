package org.shuwnyuan.blobsallad;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class BlobUtil {

	public static final String TAG = "BlobUtil";
	
	// given a file path, return a bitmap
	public static Bitmap imageFilePathToBitmap(Context c, String path, int maxDim) {
		Bitmap bmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		try {
			// compute the smallest size bitmap we need to read
			FileInputStream fis = new FileInputStream(path);
			BitmapFactory.decodeStream(fis, null, opts);
			try {
				fis.close();
			} catch (IOException e) {
				Log.w(TAG, e.getMessage());
			}
			
			int w = opts.outWidth;
			int h = opts.outHeight;
			int s = 1;
			while (true) {
				if ((w/2 < maxDim) || (h/2 < maxDim)) {
					break;
				}
				w /= 2;
				h /= 2;
				s *= 2;
			}

			// scale and read the data
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = s;
			
			fis = new FileInputStream(path);
			bmp = BitmapFactory.decodeStream(fis, null, opts);
			try {
				fis.close();
			} catch (IOException e) {
				Log.w(TAG, e.getMessage());
			}
		} catch (FileNotFoundException e) {
			Log.w(TAG, e.getMessage());
		}
		return bmp ;
	}
	
	// given a file path, return a bitmap
	public static Bitmap imageResourceToBitmap(Context c, Resources res, int id, int maxDim) {
		Bitmap bmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;

		// compute the smallest size bitmap we need to read
		BitmapFactory.decodeResource(res, id, opts);
		
		int w = opts.outWidth;
		int h = opts.outHeight;
		int s = 1;
		while (true) {
			if ((w/2 < maxDim) || (h/2 < maxDim)) {
				break;
			}
			w /= 2;
			h /= 2;
			s *= 2;
		}

		// scale and read the data
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = s;
		bmp = BitmapFactory.decodeResource(res, id, opts);
		
		return bmp;
	}
	
	// given a content or file uri, return a file path
	public static String uriToFilePath(Context context, String contentUri) {
		if (Uri.parse(contentUri).getScheme().equals("content")) {
			String[] p = {MediaStore.MediaColumns.DATA};
			Cursor cursor = context.getContentResolver().query (
					Uri.parse(contentUri),
					p, // which columns
					null, // which rows (all rows)
					null, // selection args (none)
					null); // order-by clause (ascending by name)
			if (cursor != null) {
				int iColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
				if (cursor.moveToFirst()) {
					return(cursor.getString(iColumn));
				}
			}
		}
		if (Uri.parse(contentUri).getScheme().equals("file")) {
			return Uri.parse(contentUri).getPath();
		}
		return null;
	}
}
