package org.shuwnyuan.blobsallad;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class BlobSettings extends PreferenceActivity implements 
		SharedPreferences.OnSharedPreferenceChangeListener,
		Preference.OnPreferenceClickListener
{
	public static final String BG_IMAGE_KEY = "bgImagePref";
	public static final String USE_BG_IMAGE_KEY = "showUserImagePref";
	
	
	private static final String IMAGE_MIME_TYPE = "image/*";
	private static final int CHOOSE_IMAGE_REQUEST = 1;
	private static final String TAG = "BlobSettings";
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(BlobSallad.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.settings);
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		findPreference(BG_IMAGE_KEY).setOnPreferenceClickListener(this);
		
		return;
	}

	@Override
	protected void onResume() {
		super.onResume();
		return;
	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
		return;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		return;
	}

	@Override
	public boolean onPreferenceClick(Preference pref) {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		int requestCode = 0;
		if (pref.getKey().equals(BG_IMAGE_KEY)) {
			i.setType(IMAGE_MIME_TYPE);
			requestCode = CHOOSE_IMAGE_REQUEST;
		}
		else {
			return false;
		}
		
		try {
			startActivityForResult(i, requestCode);
		} catch (ActivityNotFoundException e) {
			Log.w(TAG, e.getMessage());
		}
		
		return true;
	}
	
	@Override
	public void onActivityResult (int requestCode, int resultCode, Intent data) {
		if ((resultCode == Activity.RESULT_OK) && (requestCode == CHOOSE_IMAGE_REQUEST) && (data != null)) {
			String imagePath = BlobUtil.uriToFilePath(getBaseContext(), data.toUri(0));
			if (imagePath != null) {
				Editor editor = findPreference(BG_IMAGE_KEY).getEditor(); 
				editor.putString(BG_IMAGE_KEY, imagePath);
				editor.commit();
			}
		}
		return;
	}
	
}

