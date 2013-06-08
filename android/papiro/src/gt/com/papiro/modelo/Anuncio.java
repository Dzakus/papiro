package gt.com.papiro.modelo;

import java.io.Serializable;
import java.util.List;

import android.net.Uri;

public class Anuncio implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6438438281779843011L;

	String id;	 
	
	transient Anunciante anunciante;
	
	String link;
	String thumbnail;
	
	List<Presentacion> presentaciones;

	String qrurl;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Anunciante getAnunciante() {
		return anunciante;
	}

	public void setAnunciante(Anunciante anunciante) {
		this.anunciante = anunciante;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public List<Presentacion> getPresentaciones() {
		return presentaciones;
	}

	public void setPresentaciones(List<Presentacion> presentaciones) {
		this.presentaciones = presentaciones;
	}

	public String getQrurl() {
		return qrurl;
	}

	public void setQrurl(String qrurl) {
		this.qrurl = qrurl;
	}

	
	
}
