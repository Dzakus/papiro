package gt.com.papiro.vista;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import gt.com.papiro.R;
import gt.com.papiro.modelo.Presentable;
import gt.com.papiro.modelo.Presentacion;
import gt.com.papiro.util.ImageUtil;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class VistaPresentacionImagen extends FrameLayout implements Presentable {
	private final static String TAG = "VistaPresentacionImagen";
	ImageView imagen;
	ViewGroup leyendaImagen;
	Presentacion presentacion;
	boolean vistaPrevia;
	boolean tieneLeyenda;

	TextView leyendaImagenTitulo;
	TextView leyendaImagenDescripcion;

	private void init(Context context, AttributeSet attrs, int defStyle,
			Presentacion presentacion, boolean vistaPrevia) {

		this.imagen = new ImageView(context, attrs, defStyle);
		this.leyendaImagen = (ViewGroup) inflate(context, R.layout.leyenda_imagen, null);
		this.presentacion = presentacion;
		this.vistaPrevia = vistaPrevia;

		leyendaImagenTitulo = (TextView) leyendaImagen.findViewById(R.id.leyenda_imagen_titulo);
		leyendaImagenDescripcion = (TextView) leyendaImagen.findViewById(R.id.leyenda_imagen_descripcion);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(248, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT);
		params.setMargins(0, 0, 100, 250);
		leyendaImagen.setLayoutParams(params);
		this.addView(this.imagen);
		this.addView(this.leyendaImagen);

		leyendaImagenTitulo.setText("");
		leyendaImagenDescripcion.setText("");

		if (presentacion != null && (presentacion.getTitulo() != null || presentacion.getDescripcion() != null)) {
			leyendaImagenTitulo.setText(presentacion.getTitulo());
			leyendaImagenDescripcion.setText(presentacion.getDescripcion());
			tieneLeyenda = true;
		}

		leyendaImagen.setVisibility(INVISIBLE);

		loadMedia();
	}

	public VistaPresentacionImagen(Context context, Presentacion presentacion,
			boolean vistaPrevia) {
		super(context);

		init(context, null, 0, presentacion, vistaPrevia);

	}

	public VistaPresentacionImagen(Context context, AttributeSet attrs,
			Presentacion presentacion, boolean vistaPrevia) {
		super(context, attrs);

		init(context, null, 0, presentacion, vistaPrevia);
	}

	public VistaPresentacionImagen(Context context, AttributeSet attrs,
			int defStyle, Presentacion presentacion, boolean vistaPrevia) {
		super(context, attrs, defStyle);

		init(context, null, 0, presentacion, vistaPrevia);
	}

	private void loadMedia() {
		
		if (presentacion == null) {
			return;
		}
		
		BitmapFactory.Options opts=new BitmapFactory.Options();
		opts.inDither=false;                     //Disable Dithering mode
		opts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
		opts.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
		opts.inTempStorage=new byte[32 * 1024]; 

		if (vistaPrevia) {
			Log.d(VistaPresentacionImagen.class.getSimpleName(), presentacion.getThumbnail().toString());
			//this.imagen.setImageBitmap(ImageUtil.getBitmapFromUri(presentacion.getThumbnail()));
			//this.imagen.setImageURI(presentacion.getThumbnail());
			this.imagen.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/ads/" + presentacion.getAnuncio().getAnunciante().getId() + "/anuncios/" + presentacion.getAnuncio().getId() + "/" + presentacion.getThumbnail(), opts));
			
		} else {
			Log.d(VistaPresentacionImagen.class.getSimpleName(), presentacion.getFullview().toString());
			//this.imagen.setImageBitmap(ImageUtil.getBitmapFromUri(presentacion.getFullview()));
			//this.imagen.setImageURI(presentacion.getFullview());			
			this.imagen.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/ads/" + presentacion.getAnuncio().getAnunciante().getId() + "/anuncios/" + presentacion.getAnuncio().getId() + "/" + presentacion.getFullview(), opts));
		}
	}

	public void presentar() {
		if (tieneLeyenda) {
			leyendaImagen.setVisibility(VISIBLE);
			Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_animation);
			leyendaImagen.startAnimation(fadeInAnimation);
		}
	}

	public void ocultar() {
		if (tieneLeyenda) {
			leyendaImagen.setVisibility(INVISIBLE);
			Animation fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_animation);
			leyendaImagen.startAnimation(fadeOutAnimation);
		}
	}

	public Presentacion getPresentacion() {
		return this.presentacion;
	}
}
