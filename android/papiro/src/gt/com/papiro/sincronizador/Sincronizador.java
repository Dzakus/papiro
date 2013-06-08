package gt.com.papiro.sincronizador;

import gt.com.papiro.sincronizador.model.DispositivoConfig;
import gt.com.papiro.util.FileUtil;
import gt.com.papiro.util.MappingUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.os.AsyncTask;
import android.os.Environment;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.papiro.Papiro;
import com.google.api.services.papiro.model.AnuncioRs;
import com.google.api.services.papiro.model.DispositivoRs;
import com.google.api.services.papiro.model.PresentacionRs;
import com.google.api.services.papiro.model.UpdateInfoRs;

public class Sincronizador extends AsyncTask<Void, Void, DispositivoRs>{	
	private Papiro service;
	private GoogleAccountCredential credential;	
	private String baseFile;
	private int progress;
	private int numeroAnuncios;
	private boolean force;	
	private DispositivoConfig config;	
	private boolean finalizo;
	private boolean successful;
	private boolean sincronizar;	
	

	private String rootFile;
	
	public Sincronizador(Papiro service, GoogleAccountCredential credential, boolean forceSincronization, DispositivoConfig config){			
		this.credential = credential;		
		this.service = service;
		this.force = forceSincronization;		
		this.config = config;
		this.baseFile = Environment.getExternalStorageDirectory() + "/temp/ads/";
		this.rootFile = Environment.getExternalStorageDirectory() + "/ads/";
		sincronizar = false;
		finalizo = false;		
	}	
	

	@Override
	protected DispositivoRs doInBackground(Void... params) {						
		try {					
				if(force || listoSincronizar()){		
					UpdateInfoRs info = service.devices().updateInfo(config.getId(), config.getUsuario()).execute();					
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					
					Date fechaActualizacion = new Date();
					try {
						
						fechaActualizacion = sdf.parse(info.getUltimaFechaActualizacion());
						System.out.println("Fecha de la ultima actualizacion: " + info.getUltimaFechaActualizacion());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					fechaActualizacion = fechaActualizacion == null? new Date() : fechaActualizacion;
					System.out.println("Fecha sincronizacion: " + config.getUltimaSincronizacion());
					if(config.getUltimaSincronizacion() == null || fechaActualizacion.compareTo(config.getUltimaSincronizacion())>0){
						DispositivoRs dispo = service.devices().find(config.getId(), config.getUsuario()).execute();
						System.out.println("Procesando sincronizacion");
						processAnuncios(dispo.getAnuncios());
						MappingUtil.saveObjectToFile(dispo, new File(baseFile + "descriptor.json"));
						config.setUltimaSincronizacion(sdf.parse(info.getFechaSistema()));
						MappingUtil.saveObjectToFile(config, new File(rootFile + "sincronizador.json"));
						progress = 100;	
						finalizo = true;
						successful = true;
						sincronizar = true;
						return dispo;
					}
					else{
						finalizo = true;
						successful = true;
						sincronizar = false;
						return null;
					}					
				}else{					
					progress = 100;
					finalizo = true;
					successful = false;
					return null;			
				}
				
		} catch (IOException e) {
			progress = 100;			
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		} catch(Error error){
			error.printStackTrace();
		}
		finalizo = true;
		successful = false;
		sincronizar = false;
		return null;
	}

	private boolean listoSincronizar() {
		
		return false;
	}

	private void processAnuncios(List<AnuncioRs> anuncios) throws IOException {		
		numeroAnuncios = anuncios.size();
		File f = new File(baseFile);
		FileUtil.deleteRecursive(f);
		int anuncioActual = 1;
		for(AnuncioRs anuncio: anuncios){
			processAnuncio(anuncio, anuncioActual);	
			progress = (anuncioActual * 100) / (numeroAnuncios + 1);
			anuncioActual ++ ;				
		}
	}

	private void processAnuncio(AnuncioRs anuncio, int anuncioActual) throws IOException {
		int presentacionId = 0;							
		String directory = baseFile + "anuncios/" + anuncio.getId() + "/logo/";
		String outputFile = directory + "logo." + anuncio.getExtension();		
		File f = new File(directory);
		f.mkdirs();		
		try {
			downloadImage(anuncio.getThumbnail(), outputFile);
			anuncio.setThumbnail("logo/logo." + anuncio.getExtension());
		} catch (IOException e) {
			
		}
		int numeroPresentaciones = anuncio.getPresentaciones().size();
		int presentacionActual = 0;
		for(PresentacionRs presentacion:anuncio.getPresentaciones()){
			presentacion.setId(String.valueOf(presentacionId));			
			processPresentacion(presentacion, anuncio);
			int offset = ((anuncioActual - 1) * 100) / numeroAnuncios + 1;
			int perc = presentacionActual * 100 / numeroPresentaciones;
			progress = offset + (100 / (numeroAnuncios + 1)) * perc / 100;
			presentacionId++;
			presentacionActual ++;
		}		
	}

	private void processPresentacion(PresentacionRs presentacion, AnuncioRs anuncio) throws IOException {
		String url = presentacion.getFullview();
		String presentacionFile = "presentacion/" + presentacion.getId() + "_fv." + presentacion.getExtension();		
		String outputFile = baseFile + "anuncios/" + anuncio.getId() + "/" + presentacionFile;
		String directory = baseFile + "anuncios/" + anuncio.getId() + "/presentacion/";
		File f = new File(directory);
		f.mkdirs();
		presentacion.setFullview(presentacionFile);		
		downloadImage(url, outputFile);	
		url = presentacion.getThumbnail();
		String extension = presentacion.getExtension();
		if(presentacion.getTipo().equalsIgnoreCase("VIDEO")){
			extension = anuncio.getExtension();
		}
		presentacionFile = "presentacion/" + presentacion.getId() + "_thumb." + extension;		
		outputFile = baseFile + "anuncios/" + anuncio.getId() + "/" + presentacionFile;
		directory = baseFile + "anuncios/" + anuncio.getId() + "/presentacion/";
		f = new File(directory);
		f.mkdirs();
		presentacion.setThumbnail(presentacionFile);		
		
		downloadImage(url, outputFile);
			
	}

	public void downloadImage(String sUrl, String file) throws IOException{
		//TEMPORAL
		sUrl = sUrl.replace("0.0.0.0", "papirodev.appspot.com");
		System.out.println("Descargando el archivo: " + sUrl);
		System.out.println("Guardando en: " + file);
		URL url = new URL(sUrl);
        URLConnection connection = url.openConnection();
        connection.connect();
        // this will be useful so that you can show a typical 0-100% progress bar
        int fileLength = connection.getContentLength();

        // download the file
        InputStream input = new BufferedInputStream(url.openStream());
        OutputStream output = new FileOutputStream(file);

        byte data[] = new byte[1024];
        long total = 0;
        int count;
        while ((count = input.read(data)) != -1) {
            total += count;
            // publishing the progress....
            publishProgres((int) (total * 100 / fileLength));
            output.write(data, 0, count);
        }

        output.flush();
        output.close();
        input.close();
	}
	
	private int lastFileProgres = 0;
	private void publishProgres(int progress){
		lastFileProgres = progress;
	}
	
	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isFinalizo() {
		return finalizo;
	}

	public void setFinalizo(boolean finalizo) {
		this.finalizo = finalizo;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public boolean isSincronizar() {
		return sincronizar;
	}


	public void setSincronizar(boolean sincronizar) {
		this.sincronizar = sincronizar;
	}
	
}