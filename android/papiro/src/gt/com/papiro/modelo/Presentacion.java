package gt.com.papiro.modelo;

import java.io.Serializable;

import android.net.Uri;

public class Presentacion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7494940944413325711L;
	String id;
	String titulo;
	String descripcion;
	Tipo tipo;
	boolean especial;
	boolean principal;

	String thumbnail;
	String fullview;

	transient Anuncio anuncio;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isEspecial() {
		return especial;
	}

	public void setEspecial(boolean especial) {
		this.especial = especial;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getFullview() {
		return fullview;
	}

	public void setFullview(String fullview) {
		this.fullview = fullview;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Anuncio getAnuncio() {
		return anuncio;
	}

	public void setAnuncio(Anuncio anuncio) {
		this.anuncio = anuncio;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	public enum Tipo {
		IMAGEN, VIDEO
	}

}
