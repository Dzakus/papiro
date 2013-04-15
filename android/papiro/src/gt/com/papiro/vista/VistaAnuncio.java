package gt.com.papiro.vista;

import gt.com.papiro.modelo.Anunciable;
import gt.com.papiro.modelo.Anuncio;
import gt.com.papiro.util.ImageUtil;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class VistaAnuncio extends FrameLayout implements Anunciable {

	Anuncio anuncio;
	ImageView imagen;

	private void init(Context context, AttributeSet attrs, int defStyle,
			Anuncio anuncio) {
		this.imagen = new ImageView(context, attrs, defStyle);
		this.anuncio = anuncio;

		this.addView(this.imagen);
		
		loadMedia();
	}

	public VistaAnuncio(Context context, AttributeSet attrs, int defStyle,
			Anuncio anuncio) {
		super(context, attrs, defStyle);

		init(context, attrs, defStyle, anuncio);
	}

	public VistaAnuncio(Context context, AttributeSet attrs, Anuncio anuncio) {
		super(context, attrs);

		init(context, attrs, 0, anuncio);
	}

	public VistaAnuncio(Context context, Anuncio anuncio) {
		super(context);

		init(context, null, 0, anuncio);
	}

	private void loadMedia() {
		if (anuncio == null) {
			return;
		}

		Uri uri =  Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/ads/" + anuncio.getAnunciante().getId() + "/anuncios/" + anuncio.getId() + "/" + anuncio.getThumbnail());
		
		Log.d(VistaPresentacionImagen.class.getSimpleName(), uri.toString());
		this.imagen.setImageBitmap(ImageUtil.getBitmapFromUri(uri));
	}

	public void anunciar() {

	}
	
	public Anuncio getAnuncio() {
		return this.anuncio;
	}
}
