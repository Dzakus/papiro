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
import android.graphics.Rect;
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

	public void loadMedia() {
		
		if (presentacion == null) {
			return;
		}
		Rect rect = new Rect();
		StringBuilder path = new StringBuilder();
		
		path.append(Environment.getExternalStorageDirectory());
		path.append("/ads/");
		path.append(presentacion.getAnuncio().getAnunciante().getId());
		path.append("/anuncios/");
		path.append(presentacion.getAnuncio().getId());
		path.append("/");
		
		if (vistaPrevia) {
			Log.d(VistaPresentacionImagen.class.getSimpleName(), presentacion.getThumbnail().toString());
			path.append(presentacion.getThumbnail()); 
		} else {
			Log.d(VistaPresentacionImagen.class.getSimpleName(), presentacion.getFullview().toString());
			path.append(presentacion.getFullview());
		}
		this.imagen.setImageBitmap(ImageUtil.decodeSampledBitmapFromFile(path.toString(), this.getLayoutParams().width, this.getLayoutParams().height));
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
