package gt.com.papiro.adaptador;

import gt.com.papiro.R;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class TextoConImagenAdapter<T> extends ArrayAdapter<T> {
	private final static String TAG = "TextoConImagenAdapter";

	private int imageViewResourceId;
	
	public TextoConImagenAdapter(Context context, int resource,
			int textViewResourceId, int imageViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);		
		this.imageViewResourceId = imageViewResourceId;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = super.getDropDownView(position, convertView, parent);
		return getCustomView(position, v, parent);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		return getCustomView(position, v, parent);
	}

	private View getCustomView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Log.i(TAG, "Mostrando posicion: " + position + " : " + getItem(position));
		ImageView imagen = (ImageView) v.findViewById(R.id.texto_con_imagen_imagen);
		
		if (getItem(position).equals("Facebook")) {
			imagen.setImageResource(R.drawable.facebook_32);
		} else if (getItem(position).equals("Teléfono")) {
			imagen.setImageResource(R.drawable.phone_32);
		} else if (getItem(position).equals("Twitter")) {
			imagen.setImageResource(R.drawable.twitter_32);
		} else if (getItem(position).equals("Gmail")) {
			imagen.setImageResource(R.drawable.google_32);
		} else if (getItem(position).equals("Hotmail")) {
			imagen.setImageResource(R.drawable.windows_32);
		}
		
		return v;
	}
}
