package gt.com.papiro;

import gt.com.papiro.interactivo.Exposicion;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;
import android.text.TextWatcher;
import android.util.Log;

public class Authentication extends Activity {
	private EditText etPassword;
	private ScheduledExecutorService scheduleTaskExecutor;
	int timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		prepararTemporizador();
		timer = 0;		
		etPassword =  (EditText)findViewById(R.id.etPassword);
		etPassword.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				processPassword();
				timer = 0;
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.authentication, menu);
		return true;
	}
	
	public void processButton(View view){
		String numberPressed = (String) ((Button) view).getText();
		etPassword.setText(etPassword.getText().toString() + numberPressed);
		processPassword();
	}

	private void processPassword() {		
		String pass = etPassword.getText().toString();
		if(pass.equalsIgnoreCase("7454")){
			detenerTemporizador();
			Intent intent = new Intent(this, ConfigActivity.class);		
			startActivity(intent);			
		}
		timer = 0;
	}
	
	private void prepararTemporizador() {		
		scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {

			public void run() {				
				timer++;
				if(timer > 10){
					detenerTemporizador();					
					Intent intent = new Intent(getThis(), Exposicion.class);		
					startActivity(intent);
				}
			}
		}, 1000, 1000, TimeUnit.MILLISECONDS);
		
	}
	private Activity getThis(){
		return this;
	}
	private void detenerTemporizador(){
		scheduleTaskExecutor.shutdown();
	}

}
