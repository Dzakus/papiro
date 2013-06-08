package gt.com.papiro;

import java.io.File;
import java.io.IOException;

import gt.com.papiro.interactivo.Exposicion;
import gt.com.papiro.sincronizador.Sincronizador;
import gt.com.papiro.sincronizador.model.DispositivoConfig;
import gt.com.papiro.util.Constants;
import gt.com.papiro.util.FileUtil;
import gt.com.papiro.util.MappingUtil;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.papiro.Papiro;
import com.google.api.services.papiro.model.DispositivoConfigRs;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

public class ConfigActivity extends Activity {
	//Google services
	private GoogleAccountCredential credential;
	private SharedPreferences settings;
	private Papiro service;	
	// Components
	private EditText mEdit;
	private TextView loadingTv, welcomeMessageTv;
	private TextView conectadoTv;	
    private ProgressBar mProgress;
    private Button mainButton;
    
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();
    private DispositivoConfig config;
    private boolean ingresaDispositivo;
	public static final String DEVICE_CREATE_PAGE = "https://papirodev.appspot.com/secured/dispositivos/wizard/disp-wiz-1.jsf?nuevo=true";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		iniciarSincronizadorServices();
		initTabs();
		
		mEdit   = (EditText)findViewById(R.id.editText1);
		loadingTv   = (TextView)findViewById(R.id.loadingTv);
		conectadoTv   = (TextView)findViewById(R.id.conectadoTv);
		mProgress = (ProgressBar)findViewById(R.id.progressBar1);
		welcomeMessageTv = (TextView)findViewById(R.id.welcomeMessageTv);
		mainButton = (Button)findViewById(R.id.btnContinuar);
		mainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	siguiente();
            }
			
        });
		iniciarMain();
		loadingTv.setText("");
		conectadoTv.setText("Estas conectado con: " + credential.getSelectedAccountName() + ". Si deseas salir, presiona salir para conectarte con otro usuario");
	}
	public void siguiente(){
		if(ingresaDispositivo){
			validarDispositivo();
		} else{
			Intent intent = new Intent(getThis(), Exposicion.class);		
			startActivity(intent);
		}
	}
	
	private void iniciarMain() {
		ingresaDispositivo = true;
		String baseFile = Environment.getExternalStorageDirectory() + "/ads/";
		File fConfig = new File(baseFile + "sincronizador.json");
		if(fConfig.exists()){
			config = (DispositivoConfig)MappingUtil.loadObjectFromFile(fConfig, DispositivoConfig.class);
			if(!config.getUsuario().equalsIgnoreCase(credential.getSelectedAccountName())){
				welcomeMessageTv.setText("El dueño del dispositivo no es el mismo que el usuario conectado, favor vuelva a ingresar el numero de dispositivo");
				mostrarIngresoDispositivo();
			}else{
				welcomeMessageTv.setText("El dispositivo " + config.getId() + ", esta listo para iniciar presentación");
				mostrarIniciarPresentacion();
			}
		}else{
			welcomeMessageTv.setText("No se ha detectado ningún dispositivo configurado. Por favor ingresa el número de dispositivo a continuación: ");
			mostrarIngresoDispositivo();
		}
	}

	private void mostrarIniciarPresentacion() {
		ingresaDispositivo = false;
		mEdit.setVisibility(View.INVISIBLE);
		loadingTv.setVisibility(View.INVISIBLE);
		mProgress.setVisibility(View.INVISIBLE);
		mainButton.setText("Iniciar presentación");
	}

	private void mostrarIngresoDispositivo() {
		ingresaDispositivo = true;
		mEdit.setVisibility(View.VISIBLE);
		loadingTv.setVisibility(View.VISIBLE);
		mProgress.setVisibility(View.VISIBLE);		
		mainButton.setText("Guardar");
	}

	public void initTabs(){
		Resources res = getResources();
		TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);
		tabs.setup();
		 
		TabHost.TabSpec spec=tabs.newTabSpec("Principal");
		spec.setContent(R.id.tab1);
		spec.setIndicator("Principal",
			    res.getDrawable(android.R.drawable.ic_btn_speak_now));
		tabs.addTab(spec);
		
		 
		spec=tabs.newTabSpec("Dispositivo");
		spec.setIndicator("Dispositivo",
			    res.getDrawable(android.R.drawable.ic_btn_speak_now));
		spec.setContent(R.id.tab2);		
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("Sesión");
		spec.setIndicator("Sesión",
			    res.getDrawable(android.R.drawable.ic_btn_speak_now));
		spec.setContent(R.id.tab3);		
		
		tabs.addTab(spec);
		
		tabs.setCurrentTab(0);
	}
	
	public void crearNuevoDispositivo(View view){		
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(DEVICE_CREATE_PAGE));
		startActivity(i);
	}
	public ConfigActivity getThis(){
		return this;
	}
	public void validarDispositivo(){
		System.out.println("Validando dispositivo");
		String dispo = mEdit.getText().toString();
		final QueryDevice device = new QueryDevice(dispo);
		device.execute();
		mProgressStatus = 0;
		loadingTv.setText("Cargando...");
		new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus < 100) {
                    mProgressStatus = device.getProgress();                    
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(0);
                        }
                    });
                }
                if(device.isSuccess()){
                	device.probarSincronizacion();
                	Intent intent = new Intent(getThis(), Exposicion.class);		
        			startActivity(intent);
                }else{
                	mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                            loadingTv.setText("No se ha podido cargar el dispositivo, verifica que ingresaste el número correcto y que el usuario " + credential.getSelectedAccountName() + " sea dueño del dispositivo");
                        }
                    });
                }
            }
        }).start();

		
	}
	private void iniciarSincronizadorServices() {		
		settings = getSharedPreferences("PapiroService", 0);
		credential = GoogleAccountCredential.usingAudience(this, "server:client_id:" + Constants.CLIENT_ID);		
		setAccountName(settings.getString(Constants.PREF_ACCOUNT_NAME, null));				
		Papiro.Builder builder = new Papiro.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
		service = builder.build(); 
		
	} 
	
	private void setAccountName(String accountName) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Constants.PREF_ACCOUNT_NAME, accountName);
		editor.commit();
		System.out.println("Setteando account name");
		credential.setSelectedAccountName(accountName);		
	}

	
	public class QueryDevice extends AsyncTask<Void, Void, Void>{
		private String deviceId;	
		private int progress;
		private boolean success;
		private boolean deviceChecked;
		private String message;
		Sincronizador sincronizador;
		private QueryDevice(String deviceId){
			this.deviceId = deviceId;
			deviceChecked = false;
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			progress = 0;
			deviceChecked = false;
			message = "Verificando el dispositivo...";
			try {												
				DispositivoConfigRs config = service.devices().config(deviceId, credential.getSelectedAccountName()).execute();				
				DispositivoConfig dispConfig = new DispositivoConfig(config.getId());				
				dispConfig.setUsuario(credential.getSelectedAccountName());
				progress = 10;
				message = "Cargando anuncios...";		
				sincronizador = new Sincronizador(service, credential, true, dispConfig);
				sincronizador.execute();				
				deviceChecked = true;
			} catch (IOException e) {				
				e.printStackTrace();
				success = false;
				progress = 100;
				message = "No se ha logrado cargar. Verifique el número que ingreso y que este dispositivo pertenezca al usuario: ";
			}
			return null;
		}		
		public int getProgress() {
			if(deviceChecked){
				return progress  + (sincronizador.getProgress() - 10);
			}else{
				return progress;
			}
		}
		public void setProgress(int progress) {
			this.progress = progress;
		}
		public boolean isSuccess() {
			if(!deviceChecked){
				return success;
			}else{
				return sincronizador.isSincronizar();
			}
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public void probarSincronizacion(){
			if(sincronizador.isFinalizo() && sincronizador.isSuccessful() && sincronizador.isSincronizar()){						
				String tempFile = Environment.getExternalStorageDirectory() + "/temp/ads/";
				String baseFile = Environment.getExternalStorageDirectory() + "/ads/";
				File actual = new File(baseFile + deviceId);
				File nuevo = new File(tempFile);										
				try {
					FileUtil.copyDirectory(nuevo, actual);
																			
				} catch (IOException e) {					 
					e.printStackTrace();
				}			
				sincronizador = null;							
			}else if(sincronizador.isFinalizo()){
				sincronizador = null;
			}
		}				
	}
	public void logout(View view){
		forgetAccount();
		Intent intent = new Intent(getThis(), LoginActivity.class);		
		startActivity(intent);
	}
	private void forgetAccount() {		
		SharedPreferences.Editor editor2 = settings.edit();
		editor2.remove(Constants.PREF_ACCOUNT_NAME);
		editor2.commit();
	}
	
	public void reiniciar(View view){
		forgetAccount();
		String baseFile = Environment.getExternalStorageDirectory() + "/ads/";
		FileUtil.deleteRecursive(new File(baseFile));
		Intent intent = new Intent(this,  LoginActivity.class);
   		startActivity(intent);      
   		finish(); 
	}


}
