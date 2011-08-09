package com.marakana.yamba;

import winterwell.jtwitter.Twitter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class StatusActivity extends Activity {
	private static final String TAG = StatusActivity.class.toString();
	EditText editText;
	Button updateButton;
	Twitter twitter;
	
	/** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
        
        //Find views
        editText = (EditText) findViewById(R.id.editText);
        updateButton = (Button) findViewById(R.id.buttonUpdate);
        
        updateButton.setOnClickListener((OnClickListener) this);
        
        twitter = new Twitter("student", "password");
        twitter.setAPIRootUrl("http://yamba.marakana.com/api");
    }
    
    public void onClick(View v) {
    	twitter.setStatus(editText.getText().toString());
    	Log.d(TAG, "onClicked");
    }
}