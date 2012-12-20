package com.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class act extends Activity {
    
	private ProgressDialog progressDialog;	
	private Bitmap bitmap = null;
	private String text = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button imageBtn = (Button)findViewById(R.id.Button01);
        Button textBtn = (Button)findViewById(R.id.Button02);
        
        imageBtn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				downloadImage("http://images1.wikia.nocookie.net/__cb20111014135434/samsung/images/thumb/0/0e/698px-Samsung_Logo_svg.png/639px-698px-Samsung_Logo_svg.png");			
			}
        });
        
        textBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				downloadText("http://www.bogotobogo.com/android.html");
			}
        });
    }
    
	private void downloadImage(String urlStr) {
		progressDialog = ProgressDialog.show(this, "", 
					"Downloading Image from " + urlStr);
		final String url = urlStr;
		
		new Thread() {
			public void run() {
				InputStream in = null;
				Message msg = Message.obtain();
				msg.what = 1;
				try {
				    in = openHttpConnection(url);
				    bitmap = BitmapFactory.decodeStream(in);
				    Bundle b = new Bundle();
				    b.putParcelable("bitmap", bitmap);
				    msg.setData(b);
				    in.close();
				} catch (IOException e1) {
				    e1.printStackTrace();
				}
				messageHandler.sendMessage(msg);					
			}
 		}.start();
	}
	
	private void downloadText(String urlStr) {
		progressDialog = ProgressDialog.show(this, "", 
				"Download Text from " + urlStr);
		final String url = urlStr;
		
		new Thread () {
			public void run() {
				int BUFFER_SIZE = 2000;
		        InputStream in = null;
		        Message msg = Message.obtain();
		        msg.what=2;
		        try {
		        	in = openHttpConnection(url);
		            
		            InputStreamReader isr = new InputStreamReader(in);
		            int charRead;
		              text = "";
		              char[] inputBuffer = new char[BUFFER_SIZE];

		                  while ((charRead = isr.read(inputBuffer))>0)
		                  {                    
		                      String readString = 
		                          String.copyValueOf(inputBuffer, 0, charRead);                    
		                      text += readString;
		                      inputBuffer = new char[BUFFER_SIZE];
		                  }
		                 Bundle b = new Bundle();
						    b.putString("text", text);
						    msg.setData(b);
		                  in.close();
	                  
				}catch (IOException e2) {
	                e2.printStackTrace();
	            }
				messageHandler.sendMessage(msg);
			}
		}.start();    
	}
	
	private InputStream openHttpConnection(String urlStr) {
		InputStream in = null;
		int resCode = -1;
		
		try {
			URL url = new URL(urlStr);
			URLConnection urlConn = url.openConnection();
			
			if (!(urlConn instanceof HttpURLConnection)) {
				throw new IOException ("URL is not an Http URL");
			}
			
			HttpURLConnection httpConn = (HttpURLConnection)urlConn;
			httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect(); 

            resCode = httpConn.getResponseCode();                 
            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();                                 
            }         
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}
	
	private Handler messageHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				ImageView img = (ImageView) findViewById(R.id.imageview01);
				img.setImageBitmap((Bitmap)(msg.getData().getParcelable("bitmap")));
				break;
			case 2:
				TextView text = (TextView) findViewById(R.id.textview01);
				text.setText(msg.getData().getString("text"));
				break;
			}
			progressDialog.dismiss();
		}
	};
}