package gt.com.papiro;

import java.io.File;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.papiro.Papiro;

import gt.com.papiro.interactivo.Exposicion;
import gt.com.papiro.main.Entrada;
import gt.com.papiro.sincronizador.model.DispositivoConfig;
import gt.com.papiro.util.Constants;
import gt.com.papiro.util.MappingUtil;
import gt.com.papiro.util.SystemUtil;
import android.os.Bundle;
import android.os.Environment;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;

public class LoginActivity extends Activity {

	private GoogleAccountCredential credential;
	private SharedPreferences settings;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);									
		settings = getSharedPreferences("PapiroService", 0);
		credential = GoogleAccountCredential.usingAudience(this, "server:client_id:" + Constants.CLIENT_ID);		
		setAccountName(settings.getString(Constants.PREF_ACCOUNT_NAME, null));		
		if(credential.getSelectedAccountName() == null){
			chooseAccount();
		}else{
			entrar();
		}
	}

	private void setAccountName(String accountName) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Constants.PREF_ACCOUNT_NAME, accountName);
		editor.commit();
		System.out.println("Setteando account name");
		credential.setSelectedAccountName(accountName);		
	}
	
	private void chooseAccount() {
		 startActivityForResult(credential.newChooseAccountIntent(),
		  Constants.REQUEST_ACCOUNT_PICKER);
	}
	public void login(View view){
		chooseAccount();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.REQUEST_ACCOUNT_PICKER:
			if (data != null && data.getExtras() != null) {
				String accountName =
						data.getExtras().getString(
								AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					setAccountName(accountName);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(Constants.PREF_ACCOUNT_NAME, accountName);
					editor.commit();
					entrar();
				}
			}
			break;
		}
	}

	private void entrar() {
//		File sincronizadorConfig = new File(Environment.getExternalStorageDirectory() + "/ads/sincronizador.json");
//		if(sincronizadorConfig.exists()){						
//			DispositivoConfig config = (DispositivoConfig)MappingUtil.loadObjectFromFile(sincronizadorConfig, DispositivoConfig.class);
//			if(config.getUsuario().equalsIgnoreCase(credential.getSelectedAccountName())){
//				Intent intent = new Intent(this, ConfigActivity.class);		
//				startActivity(intent);
//				return;
//			}
//		}	
		Intent intent = new Intent(this, Authentication.class);		
		startActivity(intent);		
		
	}

}
