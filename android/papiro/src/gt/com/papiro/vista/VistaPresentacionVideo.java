package gt.com.papiro.vista;

import gt.com.papiro.modelo.Presentable;
import gt.com.papiro.modelo.Presentacion;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.VideoView;

public class VistaPresentacionVideo extends VideoView implements Presentable {

	Presentacion presentacion;
	boolean vistaPrevia;
	
	private void init(Presentacion presentacion, boolean vistaPrevia) {
		this.presentacion = presentacion;
		this.vistaPrevia = vistaPrevia;
		
		if (presentacion == null) {
			return;
		}
		
		if (!vistaPrevia) {
			setVideoURI(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/ads/" + presentacion.getAnuncio().getAnunciante().getId() + "/anuncios/" + presentacion.getAnuncio().getId() + "/" + presentacion.getFullview()));
		} else {
			setVideoURI(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/ads/" + presentacion.getAnuncio().getAnunciante().getId() + "/anuncios/" + presentacion.getAnuncio().getId() + "/" + presentacion.getThumbnail()));
		}
	}
	
	public VistaPresentacionVideo(Context context, Presentacion presentacion, boolean vistaPrevia) {
		super(context);

		init(presentacion, vistaPrevia);
	}

	public VistaPresentacionVideo(Context context, AttributeSet attrs, Presentacion presentacion, boolean vistaPrevia) {
		super(context, attrs);
		
		init(presentacion, vistaPrevia);
	}

	public VistaPresentacionVideo(Context context, AttributeSet attrs, int defStyle, Presentacion presentacion, boolean vistaPrevia) {
		super(context, attrs, defStyle);
		
		init(presentacion, vistaPrevia);
	}

	public void presentar() {
		this.start();
		
	}

	public void ocultar() {
		this.stopPlayback();		
	}

	public Presentacion getPresentacion() {		
		return this.presentacion;
	}

}
