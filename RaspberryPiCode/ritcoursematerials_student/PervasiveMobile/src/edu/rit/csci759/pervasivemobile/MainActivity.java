package edu.rit.csci759.pervasivemobile;

import edu.rit.csci759.pervasivemobile.logic.JSONHandler;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button btn_send_request;
	static EditText et_server_url;
	static EditText et_requst_method;
	TextView tv_response;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		et_server_url = (EditText) findViewById(R.id.et_serverURL);
		et_requst_method = (EditText) findViewById(R.id.et_requestMethod);
		tv_response = (TextView) findViewById(R.id.tv_response);
		
		
		OnClickListener buttonListener = new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	new SendJSONRequest().execute();
		    }
		};
		btn_send_request = (Button) findViewById(R.id.btn_sendRequest);
		btn_send_request.setOnClickListener(buttonListener);
		
		
		
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	
	class SendJSONRequest extends AsyncTask<Void, String, String> {
		String response_txt;
		
		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String serverURL_text = et_server_url.getText().toString();
			String request_method = et_requst_method.getText().toString();
			
			response_txt = JSONHandler.testJSONRequest(serverURL_text, request_method);
			
			return response_txt;
		}
		
		protected void onProgressUpdate(Integer... progress) {
	         //setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(String result) {
	    	 Log.d("debug", result);
	    	 Log.d("debug", response_txt);
	    	 tv_response.setText(result);
	     }
		
	}
}
