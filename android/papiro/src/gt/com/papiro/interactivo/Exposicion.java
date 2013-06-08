package gt.com.papiro.interactivo;

import gt.com.papiro.R;
import gt.com.papiro.adaptador.TextoConImagenAdapter;
import gt.com.papiro.configuracion.Preferencias;
import gt.com.papiro.events.EventMonitor;
import gt.com.papiro.modelo.Anunciante;
import gt.com.papiro.modelo.Anuncio;
import gt.com.papiro.modelo.Presentable;
import gt.com.papiro.modelo.Presentacion;
import gt.com.papiro.sincronizador.Sincronizador;
import gt.com.papiro.sincronizador.model.DispositivoConfig;
import gt.com.papiro.task.UnZipper;
import gt.com.papiro.util.Constants;
import gt.com.papiro.util.FileUtil;
import gt.com.papiro.util.HttpUtil;
import gt.com.papiro.util.MappingUtil;
import gt.com.papiro.util.QrUtil;
import gt.com.papiro.vista.ContactUtil;
import gt.com.papiro.vista.Paginador;
import gt.com.papiro.vista.VistaAnuncio;
import gt.com.papiro.vista.VistaPresentacionImagen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;
import android.graphics.Bitmap;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.papiro.Papiro;
import com.google.gson.Gson;

public class Exposicion extends Activity {
	private static final String TAG = "Exposicion";

	List<Anunciante> anunciantes;

	List<Anuncio> anuncios;

	Queue<Presentacion> videos;
	private GoogleAccountCredential credential;
	private SharedPreferences settings;
	private Papiro service;	
	private DispositivoConfig config;
	private String baseFile;

	// Servicios:
	private Preferencias preferencias;
	private EventMonitor eventMonitor;

	private ScheduledExecutorService scheduleTaskExecutor;
	private long ultimoAnuncioTime;
	private long ultimoToqueTime;
	private long ultimoVideoTime;

	boolean exposicionIniciada = false;
	boolean startingVideoPlayback = false;
	int indiceAnuncioActual = -1;
	private Sincronizador sincronizador;
	
	// Eventos:
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		iniciarSincronizadorServices();
		
		videos = new LinkedList<Presentacion>();

		this.setContentView(R.layout.exposicion_view);

		View exposicionView = findViewById(R.id.exposicion_view);

		exposicionView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		exposicionView.setKeepScreenOn(true);
		
		
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		if (exposicionIniciada) {
			return;
		}
		//guardarConfiguracion();
		cargarConfiguracion();
		cargarAnunciantes();
		// saveData();
		// saveConfiguration();
		// testUnZip();

		prepararVistaVideo();
		prepararVistaPresentacion();
		prepararBotones();
		prepararInfoContacto();

		mostrarSiguienteAnuncio();

		prepararTemporizadores();

		ocultarInfoContacto();

		exposicionIniciada = true;
	}

	private void iniciarSincronizadorServices() {
		baseFile = Environment.getExternalStorageDirectory() + "/ads/";
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

	@Override
	protected void onPause() {
		Log.i(TAG, "Pausando la aplicacion");
		pararTemporizadores();
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "Resumiendo la aplicacion");
		if (scheduleTaskExecutor == null || scheduleTaskExecutor.isTerminated()) {
			prepararTemporizadores();
		}
		super.onResume();
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void onUserInteraction() {
		Log.i(TAG, "Actividad de usuario detectada");
		ultimoToqueTime = Calendar.getInstance().getTimeInMillis();
		super.onUserInteraction();
	}

	// Metodos de preparacion:
	private void prepararInfoContacto() {
		Log.i(TAG, "Preparando dialogo de contacto");
		Spinner infoContacto = (Spinner) findViewById(R.id.info_contacto_tipo_cuenta);
		TextoConImagenAdapter<String> adaptador = new TextoConImagenAdapter<String>(this, R.layout.texto_con_imagen_row, R.id.texto_con_imagen_texto, R.id.texto_con_imagen_imagen, getResources().getStringArray(R.array.array_tipos_de_contacto));
		infoContacto.setAdapter(adaptador);

	}

	private void prepararBotones() {
		Log.i(TAG, "Preparando botones de la aplicacion");
		
		View botonMostrarInfoContacto = findViewById(R.id.exposicion_view_boton_mostrar_info_contacto);
		View botonEnviarInfoContacto = findViewById(R.id.info_contacto_boton_enviar);
		View botonGmailInfoContacto = findViewById(R.id.info_contacto_boton_gmail);
		View botonFacebookInfoContacto = findViewById(R.id.info_contacto_boton_facebook);
		View botonTwitterInfoContacto = findViewById(R.id.info_contacto_boton_twitter);
		View botonHotmailInfoContacto = findViewById(R.id.info_contacto_boton_hotmail);
		View botonEmailInfoContacto = findViewById(R.id.info_contacto_boton_email);
		View botonTelefonoInfoContacto = findViewById(R.id.info_contacto_boton_telefono);

		OnClickListener enviarInfoContactoListener = new OnClickListener() {
			public void onClick(View v) {
				EditText edtCuenta = (EditText) findViewById(R.id.info_contacto_cuenta);
				final String idPresentacion = getIdPresentacionActual();
				final String cuenta = edtCuenta.getText().toString();
				final String tipoCuenta = ((Button)v).getText().toString();
				
				ocultarInfoContacto();
				Toast.makeText(Exposicion.this, "Gracias por su inter�s", Toast.LENGTH_LONG).show();
				new Thread(new Runnable() {
					public void run() {
						ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
						if (networkInfo != null && networkInfo.isConnected()) {
							List<NameValuePair> values = new ArrayList<NameValuePair>();
							values.add(new BasicNameValuePair("entry.0.single", getIdAnuncianteActual()));
							values.add(new BasicNameValuePair("entry.1.single", getIdAnuncioActual()));
							values.add(new BasicNameValuePair("entry.2.single", idPresentacion));
							values.add(new BasicNameValuePair("entry.3.single", ContactUtil.joinContactInfo(cuenta, tipoCuenta)));
							Log.i(TAG, "Enviando informacion de contacto");
							HttpUtil.postData(Exposicion.this.getPreferencias().getInfoContactoFormLocation(), values);
						} else {
							Log.e(TAG, "No se pudo enviar la informacion de contacto porque no hay conectividad de red");
						}
						Exposicion.this.logEvent("click", "lo_quiero", "producto " + idPresentacion, ContactUtil.joinContactInfo(cuenta, tipoCuenta));
					}

				}).start();
			}
		};

		botonMostrarInfoContacto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				logEvent("objetivo", "imagen", getIdPresentacionActual(), "");
				if (getAnuncioActual().getLink() == null || getAnuncioActual().getLink().trim().isEmpty() || getAnuncioActual().getLink().contains("@")) {					
					mostrarInfoContacto();
				} else {
					mostrarNavegador(getAnuncioActual().getLink());
				}
			}
		});

		botonEnviarInfoContacto.setOnClickListener(enviarInfoContactoListener);
		botonGmailInfoContacto.setOnClickListener(enviarInfoContactoListener);
		botonFacebookInfoContacto.setOnClickListener(enviarInfoContactoListener);
		botonTwitterInfoContacto.setOnClickListener(enviarInfoContactoListener);
		botonHotmailInfoContacto.setOnClickListener(enviarInfoContactoListener);
		botonEmailInfoContacto.setOnClickListener(enviarInfoContactoListener);
		botonTelefonoInfoContacto.setOnClickListener(enviarInfoContactoListener);
	}

	private void prepararVistaVideo() {
		Log.i(TAG, "Preparando vista de video");
		final View v = findViewById(R.id.fondo_video);
		final VideoView vv = (VideoView) findViewById(R.id.vista_video);

		vv.getHolder().addCallback(new Callback() {

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.d(TAG, "surfaceChanged: width: " + width + ", height: " + height);
			}

			public void surfaceCreated(SurfaceHolder holder) {
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}
			
		}) ;
		
		vv.setOnPreparedListener(new OnPreparedListener() {
			
			public void onPrepared(MediaPlayer mp) {
				mp.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
					public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
						Log.d(TAG, "onVideoSizeChanged: width: " + width + ", height: " + height);
						
					}
				});
			}
		});
		
		
		OnTouchListener touchListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Log.i(TAG, "Parando video");
					pararVideo();
					ocultarVistaVideo();
					ultimoVideoTime = Calendar.getInstance().getTimeInMillis();
				}
				return true;
			}
		};

		v.setOnTouchListener(touchListener);
		vv.setOnTouchListener(touchListener);
		vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.i(TAG, "Video completo");
				ocultarVistaVideo();
				ultimoVideoTime = Calendar.getInstance().getTimeInMillis();
				return true;
			}
		});
		vv.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				Log.i(TAG, "Video completo");
				ocultarVistaVideo();
				ultimoVideoTime = Calendar.getInstance().getTimeInMillis();
			}
		});
	}

	private void prepararVistaPresentacion() {
		Log.i(TAG, "Preparando vista interactiva");
		final Paginador vistaPresentacionScrollView = (Paginador) findViewById(R.id.vista_presentacion_scroll_view);
		final Paginador vistaPreviaPresentacionScrollView = (Paginador) findViewById(R.id.vista_previa_presentacion_scroll_view);

		ViewGroup vistaPreviaPresentacion = (ViewGroup) findViewById(R.id.vista_previa_presentacion);

		vistaPreviaPresentacionScrollView.setPageWidth(0);

		vistaPreviaPresentacion.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				Log.i("Exposicion", "action: " + event.getAction());
				Log.i("Exposicion", "History Size: " + event.getHistorySize());
				Log.i("Exposicion", "x: " + event.getX());

				if (event.getAction() == MotionEvent.ACTION_UP && event.getHistorySize() == 0) {
					Log.i("Exposicion", "click");
					Log.i("Exposicion", "scrollX: " + (float) vistaPreviaPresentacionScrollView.getScrollX());
					Log.i("Exposicion", "horizontalScrollRange: " + (float) vistaPreviaPresentacionScrollView.getHorizontalScrollRange());
					Log.i("Exposicion", "percent: " + (float) (vistaPreviaPresentacionScrollView.getScrollX() + event.getX()) / (float) vistaPreviaPresentacionScrollView.getHorizontalScrollRange() * 100f);

					// vistaPresentacionScrollView.smoothScrollToPercent((float)(event.getX())
					// /
					// (float)vistaPreviaPresentacionScrollView.getHorizontalScrollRange()
					// * 100f);
					vistaPresentacionScrollView.goToPage((int) (((float) (event.getX()) / (float) vistaPreviaPresentacionScrollView.getHorizontalScrollRange() * vistaPresentacionScrollView.getPageCount())));
				}

				return true;
			}
		});
		
		vistaPresentacionScrollView.setOnPresentarHijoListener(new Paginador.OnPresentarHijoListener() {
			
			public void onPresentarHijo(Presentable presentable) {
				logEvent("impresion", "imagen", presentable.getPresentacion().getId(), presentable.getPresentacion().getFullview());				
			}
		});
		
		vistaPresentacionScrollView.requestFocus();
	}

	private void prepararTemporizadores() {
		Log.i(TAG, "Preparando temporizadores");
		scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {

			public void run() {
				sincronizarTick();
				long now = Calendar.getInstance().getTimeInMillis();				
				Log.i(TAG, "Revisando inactividad");

				if (now - ultimoToqueTime >= getPreferencias().getInactivityInterval()) {
					Log.i(TAG, "Ocultando popups");
					runOnUiThread(new Runnable() {
						public void run() {
							ocultarPopups();
						}
					});
				}

				if (now - ultimoAnuncioTime >= getPreferencias().getAnuncioChangeInterval() && now - ultimoToqueTime >= getPreferencias().getInactivityInterval()) {
					runOnUiThread(new Runnable() {
						public void run() {
							VideoView vv = (VideoView) findViewById(R.id.vista_video);

							if (!vv.isPlaying() && !startingVideoPlayback) {
								probarSincronizacion();
								mostrarSiguienteAnuncio();
							} else {
								Log.i(TAG, "Hay un video reproduciendose, no se mostrara el siguiente anuncio hasta que acabe");
							}
						}

					});
				} else if (now - ultimoVideoTime >= getPreferencias().getVideoChangeInterval() && now - ultimoToqueTime >= getPreferencias().getInactivityInterval()) {
					runOnUiThread(new Runnable() {
						public void run() {
							VideoView vv = (VideoView) findViewById(R.id.vista_video);

							if (!vv.isPlaying() && !startingVideoPlayback) {	
								mostrarSiguienteVideo(true);
							} else {
								Log.i(TAG, "Hay un video reproduciendose, no se mostrara el siguiente video hasta que acabe");
							}

						}
					});
				}

				Log.i(TAG, "Finalizando la revision de inactividad");
			}
		}, getPreferencias().getCheckInterval(), getPreferencias().getCheckInterval(), TimeUnit.MILLISECONDS);

		Log.i(TAG, "Temporizadores iniciados");
	}

	// Acciones:
	public void pararTemporizadores() {
		Log.i(TAG, "Parando temporizadores");
		scheduleTaskExecutor.shutdownNow();
		Log.i(TAG, "Temporizadores parados");
	}

	public void pararVideo() {
		Log.i(TAG, "Parando video");
		VideoView vv = (VideoView) findViewById(R.id.vista_video);
		vv.stopPlayback();		
	}

	public void actualizarListaAnuncios() {
		Log.i(TAG, "Mostrando la lista de anuncios");
		ViewGroup contenedorAnuncios = (ViewGroup) findViewById(R.id.vista_anuncio);
		View v;

		contenedorAnuncios.removeAllViews();

		for (int i = 0; i < anuncios.size(); i++) {
			Anuncio anuncio = anuncios.get(i);

			if (i == indiceAnuncioActual) {
				continue;
			}

			v = new VistaAnuncio(this, anuncio);
			v.setTag(i);
			v.setLayoutParams(new ViewGroup.LayoutParams(120, 120));

			v.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					indiceAnuncioActual = (Integer) v.getTag();
					mostrarAnuncio(((VistaAnuncio) v).getAnuncio());
				}
			});

			contenedorAnuncios.addView(v);
		}
	}

	public void mostrarSiguienteAnuncio() {
		ultimoAnuncioTime = Calendar.getInstance().getTimeInMillis();
		if (anuncios.size() <= 0) {
			// No cambiara el anuncio
			Log.i(TAG, "No hay anuncio que mostrar");
			return;
		}

		int indiceAnuncioAnterior = indiceAnuncioActual;
		Log.i(TAG, "Mostrando el siguiente anuncio");
		// Se incrementa y se hace rotativo
		indiceAnuncioActual++;
		if (indiceAnuncioActual >= anuncios.size()) {
			indiceAnuncioActual = 0;
		}

		if (indiceAnuncioAnterior == indiceAnuncioActual) {
			Log.i(TAG, "No hay otro anuncio que mostrar");
			return;
		}

		mostrarAnuncio(getAnuncioActual());
	}

	public void mostrarAnuncio(Anuncio anuncio) {
		final Anuncio a = anuncio;
		final View v = findViewById(R.id.exposicion_view);
		Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_animation_100);

		fadeOutAnimation.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			public void onAnimationEnd(Animation animation) {
				cargarAnuncio(a);
				Animation fadeInAnimation = AnimationUtils.loadAnimation(Exposicion.this, R.anim.fade_in_animation_100);
				v.startAnimation(fadeInAnimation);
			}
		});
		v.startAnimation(fadeOutAnimation);
	}

	public void cargarAnuncio(Anuncio anuncio) {
		if (anuncio == null) {
			return;
		}

		System.gc();

		ultimoAnuncioTime = Calendar.getInstance().getTimeInMillis();
		ultimoVideoTime = Calendar.getInstance().getTimeInMillis();
		ultimoToqueTime = Calendar.getInstance().getTimeInMillis();

		ViewGroup contenedorVistas = (ViewGroup) findViewById(R.id.vista_presentacion);
		ViewGroup contenedorVistasPrevias = (ViewGroup) findViewById(R.id.vista_previa_presentacion);
		Paginador vistaPresentacionScrollView = (Paginador) findViewById(R.id.vista_presentacion_scroll_view);
		Paginador vistaPreviaPresentacionScrollView = (Paginador) findViewById(R.id.vista_previa_presentacion_scroll_view);
		ImageView logoAnunciante = (ImageView) findViewById(R.id.logo_anunciante);
		ImageView qrCodeAnuncio = (ImageView) findViewById(R.id.qr_code_anuncio);

		contenedorVistasPrevias.removeAllViews();
		contenedorVistas.removeAllViews();
		videos.clear();

		vistaPresentacionScrollView.goToPage(0);
		vistaPreviaPresentacionScrollView.goToPage(0);

		logoAnunciante.setImageURI(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/ads/" + anuncio.getAnunciante().getId() + "/" + anuncio.getAnunciante().getLogo()));
		if (anuncio.getQrurl() != null && !anuncio.getQrurl().isEmpty()) {
			qrCodeAnuncio.setImageBitmap(QrUtil.generateQrCodeFromString(anuncio.getQrurl(), 128, 128));
		}

		for (Presentacion presentacion : anuncio.getPresentaciones()) {
			View v;
			switch (presentacion.getTipo()) {
			case IMAGEN:
				v = new VistaPresentacionImagen(this, presentacion, true);
				v.setLayoutParams(new ViewGroup.LayoutParams(120, 120));
				contenedorVistasPrevias.addView(v);
				((VistaPresentacionImagen)v).loadMedia();
				v = new VistaPresentacionImagen(this, presentacion, false);
				Log.i(Exposicion.class.getName(), "width: " + getWidth());
				v.setLayoutParams(new ViewGroup.LayoutParams(getWidth(), ViewGroup.LayoutParams.MATCH_PARENT));
				contenedorVistas.addView(v);
				((VistaPresentacionImagen)v).loadMedia();
				
				break;
			case VIDEO:
				/*
				 * v = new VistaPresentacionVideo(this, presentacion, true);
				 * v.setLayoutParams(new ViewGroup.LayoutParams(120, 120));
				 * contenedorVistasPrevias.addView(v);
				 * 
				 * v = new VistaPresentacionVideo(this, presentacion, false);
				 * v.setLayoutParams(new ViewGroup.LayoutParams(getWidth(),
				 * ViewGroup.LayoutParams.MATCH_PARENT));
				 * contenedorVistas.addView(v);
				 */

				videos.add(presentacion);

				break;
			}
		}

		vistaPresentacionScrollView.presentarHijo(0);
		actualizarListaAnuncios();
	}

	public void mostrarSiguienteVideo(boolean principal) {
		Log.i(TAG, "Mostrando el siguiente video");

		Presentacion presentacionInicial = videos.peek();
		Presentacion presentacion;

		if (presentacionInicial == null) {
			return;
		}

		do {
			presentacion = videos.poll();
			videos.add(presentacion);

			if (presentacion.isPrincipal() || !principal) {
				Exposicion.this.logEvent("impresion", "video", presentacion.getId(), presentacion.getFullview());
				System.out.println("Video URI: " + "file://" + Environment.getExternalStorageDirectory() + "/ads/" + presentacion.getAnuncio().getAnunciante().getId() + "/anuncios/" + presentacion.getAnuncio().getId() + "/" + presentacion.getFullview());
				playVideoFromUri(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/ads/" + presentacion.getAnuncio().getAnunciante().getId() + "/anuncios/" + presentacion.getAnuncio().getId() + "/" + presentacion.getFullview()));
				break;
			}

			presentacion = videos.peek();
		} while (presentacion != presentacionInicial);
	}

	public void mostrarVistaVideo() {
		Log.i(TAG, "Mostrando la vista del video");
		View v = findViewById(R.id.fondo_video);
		VideoView vv = (VideoView) findViewById(R.id.vista_video);
		vv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

		vv.setVisibility(View.VISIBLE);
		v.setVisibility(View.VISIBLE);
		Exposicion.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	public void ocultarVistaVideo() {
		Log.i(TAG, "Ocultando la vista del video");
		View v = findViewById(R.id.fondo_video);
		VideoView vv = (VideoView) findViewById(R.id.vista_video);
		View fondo = findViewById(R.id.exposicion_view);

		vv.setVisibility(View.INVISIBLE);
		v.setVisibility(View.INVISIBLE);
		Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.dummy_animation);
		Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.dummy_animation);

		fondo.startAnimation(fadeInAnimation);
		v.startAnimation(fadeOutAnimation);
		Exposicion.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
	}

	public void mostrarInfoContacto() {
		Log.i(TAG, "Mostrando la informacion de contacto");
		View infoContacto = findViewById(R.id.info_contacto);
		EditText cuentaContacto = (EditText) findViewById(R.id.info_contacto_cuenta);
		Spinner tipoCuentaContacto = (Spinner) findViewById(R.id.info_contacto_tipo_cuenta);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		infoContacto.setVisibility(View.VISIBLE);
		cuentaContacto.setText(null);
		cuentaContacto.requestFocus();
		imm.showSoftInput(cuentaContacto, 0);
	}

	public void ocultarInfoContacto() {
		Log.i(TAG, "Ocultando la informacion de contacto");
		View infoContacto = findViewById(R.id.info_contacto);
		EditText cuentaContacto = (EditText) findViewById(R.id.info_contacto_cuenta);
		Spinner tipoCuentaContacto = (Spinner) findViewById(R.id.info_contacto_tipo_cuenta);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		try {
			// tipoCuentaContacto.performClick();
		} catch (Exception ex) {
			// Ignore
		}

		cuentaContacto.setText(null);
		cuentaContacto.clearFocus();
		imm.hideSoftInputFromWindow(cuentaContacto.getWindowToken(), 0);
		infoContacto.setVisibility(View.INVISIBLE);
	}
	
	private void mostrarNavegador(String link) {
		View v = findViewById(R.id.fondo_webview);
		WebView webView = (WebView) findViewById(R.id.webview);
		//TODO: ARREGLAR CODIGO
		webView.setWebViewClient(new WebViewClient() {
		    private String pendingUrl;

		    @Override
		    public void onPageStarted(WebView view, String url, Bitmap favicon) {
		        if (pendingUrl == null) {
		            pendingUrl = url;
		        }
		    }

		    @Override
		    public void onPageFinished(WebView view, String url) {
		        if (!url.equals(pendingUrl)) {
		            Log.d(TAG, "Detected HTTP redirect " + pendingUrl + "->" + url);
		            pendingUrl = null;
		        }
		    }
		});
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://"+link);
		webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		
		v.setVisibility(View.VISIBLE);
		webView.setVisibility(View.VISIBLE);		
		
	}
	
	private void ocultarNavegador() {
		WebView webView = (WebView) findViewById(R.id.webview);
		View v = findViewById(R.id.fondo_webview);
		
		
		v.setVisibility(View.INVISIBLE);
		webView.setVisibility(View.INVISIBLE);
		webView.loadUrl("about:blank");
	}
	
	private void ocultarPopups() {
		ocultarInfoContacto();
		ocultarNavegador();
	}

	private void guardarAnunciantes() {
		for (Anunciante anunciante : anunciantes) {
			guardarAnunciante(anunciante);
		}
	}

	private void guardarAnunciante(Anunciante anunciante) {
		saveObjectToFile(anunciante, new File(Environment.getExternalStorageDirectory() + "/ads/" + anunciante.getId() + "/descriptor.json"));
	}

	private void cargarAnunciantes() {
		Log.i(TAG, "Cargando los anunciantes");
		anunciantes = new ArrayList<Anunciante>();
		anuncios = new ArrayList<Anuncio>();

		File directorioAds = new File(Environment.getExternalStorageDirectory() + "/ads");

		Log.i(TAG, "Procesando directorio: " + directorioAds.getAbsolutePath());

		for (File directorioAnunciante : directorioAds.listFiles()) {
			Log.i(TAG, "Procesando ruta: " + directorioAnunciante.getAbsolutePath());
			if (directorioAnunciante.isDirectory()) {
				Anunciante anunciante = cargarAnuncianteFromFile(new File(directorioAnunciante.getAbsolutePath() + "/descriptor.json"));
				if (anunciante != null) {
					Log.i(TAG, "Agregando anunciante");
					anunciantes.add(anunciante);
					cargarAnuncios(anunciante);
				} else {
					Log.e(TAG, "No se pudo cargar la informacion del anunciante");
				}
			} else {
				Log.i(TAG, "La ruta no es un directorio, saltando");
			}
		}
	}

	private void cargarAnuncios(Anunciante anunciante) {
		if (anunciante == null || anunciante.getAnuncios() == null) {
			Log.w(TAG, "No se pudieron cargar los anuncios de un anunciante");
			return;
		}

		// Carga los anunciones del anunciante:
		for (Anuncio anuncio : anunciante.getAnuncios()) {
			anuncio.setAnunciante(anunciante);
			anuncios.add(anuncio);

			// Coloca el padre de las presentaciones:
			if (anuncio.getPresentaciones() != null) {
				for (Presentacion presentacion : anuncio.getPresentaciones()) {
					presentacion.setAnuncio(anuncio);
				}
			}
		}
	}

	private Anunciante cargarAnuncianteFromFile(File file) {
		return (Anunciante) loadObjectFromFile(file, Anunciante.class);
	}

	private Preferencias cargarPreferenciasFromFile(File file) {
		return (Preferencias) loadObjectFromFile(file, Preferencias.class);
	}

	private void cargarConfiguracion() {
		Log.i(TAG, "Cargando configuracion");
		preferencias = cargarPreferenciasFromFile(new File(Environment.getExternalStorageDirectory() + "/ads/preferencias.json"));
	}

	private void guardarConfiguracion() {
		Log.i(TAG, "Guardando configuracion");
		saveObjectToFile(getPreferencias(), new File(Environment.getExternalStorageDirectory() + "/ads/preferencias.json"));
	}

	private boolean saveObjectToFile(Object obj, File file) {
		Gson gson = new Gson();

		try {
			Log.i(TAG, gson.toJson(obj));
			FileUtil.writeToFile(new ByteArrayInputStream(gson.toJson(obj).getBytes()), file);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private Object loadObjectFromFile(File file, Class clazz) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(new FileReader(file), clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private void testUnZip() {
		UnZipper unZipper = new UnZipper(new File(Environment.getExternalStorageDirectory() + "/1.zip"), new File(Environment.getExternalStorageDirectory() + "/ads/1"));
		unZipper.unzip();
	}

	public void playVideoFromUri(Uri uri) {
		Log.i(TAG, "Empezando la reproduccion del video: " + uri.toString());
		View v = findViewById(R.id.fondo_video);
		final VideoView vv = (VideoView) findViewById(R.id.vista_video);
		View fondo = findViewById(R.id.exposicion_view);
		
		Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.dummy_animation);
		Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.dummy_animation);

		startingVideoPlayback = true;		
		
		vv.setVideoURI(uri);

		fadeOutAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			public void onAnimationEnd(Animation animation) {				
				mostrarVistaVideo();
				vv.requestFocus();
				vv.start();
				startingVideoPlayback = false;
			}
		});

		fondo.startAnimation(fadeOutAnimation);
		v.startAnimation(fadeInAnimation);
	}
	
	public void logEvent(String eventType, String eventName, String eventDescription, String eventInfo) {
		Exposicion.this.getEventMonitor().logEvent(getAnuncioActual().getAnunciante().getId(), "anuncio", getAnuncioActual().getId(), eventType, eventName, eventDescription, eventInfo);
	}

	// Propiedades:
	private Point getSize() {
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		return size;
	}

	private int getWidth() {
		return getSize().x;
	}

	private int getHeight() {
		return getSize().y;
	}

	public Anuncio getAnuncioActual() {
		if (indiceAnuncioActual < 0 || indiceAnuncioActual >= anuncios.size()) {
			return null;
		}
		return anuncios.get(indiceAnuncioActual);
	}
	
	public String getIdAnuncioActual() {
		return getAnuncioActual().getId();
	}
	
	public String getIdAnuncianteActual() {
		return getAnuncioActual().getAnunciante().getId();
	}
	
	public String getIdPresentacionActual() {
		View activeChild = ((Paginador) findViewById(R.id.vista_presentacion_scroll_view)).getViewAtCenter();
		String idPresentacion = (activeChild == null ? null : !(activeChild instanceof Presentable) ? null : ((Presentable)activeChild).getPresentacion() == null? null : ((Presentable)activeChild).getPresentacion().getId());
		return idPresentacion; 
	}

	public Preferencias getPreferencias() {
		// Si no se pueden obtener las preferencias se cargan las default:
		if (preferencias == null) {
			preferencias = new Preferencias();
		}
		return preferencias;
	}

	public EventMonitor getEventMonitor() {
		if (eventMonitor == null) {
			eventMonitor = new EventMonitor(new File(getPreferencias().getEventsLogFileLocation()));
		}

		return eventMonitor;
	}
	
	public void sincronizarTick(){		
		if(sincronizador == null || (sincronizador.isFinalizo() && !sincronizador.isSincronizar())){									
			try{
				config = (DispositivoConfig)MappingUtil.loadObjectFromFile(new File(baseFile + "sincronizador.json"), DispositivoConfig.class);
			}catch(Exception ex){
				config = new DispositivoConfig();
			}
			sincronizador = new Sincronizador(service, credential, true, config);		
			sincronizador.execute();
		}	
	}
	public void probarSincronizacion(){
		if(sincronizador.isFinalizo() && sincronizador.isSuccessful() && sincronizador.isSincronizar()){						
			String tempFile = Environment.getExternalStorageDirectory() + "/temp/ads/";
			File actual = new File(baseFile + config.getId());
			File nuevo = new File(tempFile);										
			try {				
				FileUtil.deleteRecursive(actual);
				FileUtil.copyDirectory(nuevo, actual);
				System.out.println("Sincronizacion exitosa, sincronizado: ");										
				cargarAnunciantes();
				FileUtil.deleteRecursive(nuevo);
			} catch (IOException e) {					 
				e.printStackTrace();
			}			
			sincronizador = null;							
		}else if(sincronizador.isFinalizo()){
			sincronizador = null;
		}		
	}

}
