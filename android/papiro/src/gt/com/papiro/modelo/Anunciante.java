package gt.com.papiro.modelo;

import java.io.Serializable;
import java.util.List;

import android.net.Uri;

public class Anunciante implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4199337908368184090L;
	
	String id;
	String nombre;
	
	String logo;
	
	List<Anuncio> anuncios;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public List<Anuncio> getAnuncios() {
		return anuncios;
	}

	public void setAnuncios(List<Anuncio> anuncios) {
		this.anuncios = anuncios;
	}
}
