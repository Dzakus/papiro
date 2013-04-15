package gt.com.papiro.configuracion;

import java.io.Serializable;

import android.os.Environment;

public class Preferencias implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3597483861046367030L;
	
	private long checkInterval = 30000;
	private long anuncioChangeInterval = 120000;
	private long videoChangeInterval = 30000;
	private long inactivityInterval = 30000; 
	private String eventsLogFileLocation = Environment.getExternalStorageDirectory() + "/ads/logs/events.log";
	private String infoContactoFormLocation = "https://docs.google.com/spreadsheet/formResponse?formkey=dG15dU9UMVA1OS1OTHZEVVFVam5wUEE6MQ&amp;ifq";
	
	public Preferencias() {

	}

	public long getCheckInterval() {
		return checkInterval;
	}

	public void setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval;
	}

	public long getAnuncioChangeInterval() {
		return anuncioChangeInterval;
	}

	public void setAnuncioChangeInterval(long anuncioChangeInterval) {
		this.anuncioChangeInterval = anuncioChangeInterval;
	}

	public long getVideoChangeInterval() {
		return videoChangeInterval;
	}

	public void setVideoChangeInterval(long videoChangeInterval) {
		this.videoChangeInterval = videoChangeInterval;
	}

	public long getInactivityInterval() {
		return inactivityInterval;
	}

	public void setInactivityInterval(long inactivityInterval) {
		this.inactivityInterval = inactivityInterval;
	}

	public String getEventsLogFileLocation() {
		return eventsLogFileLocation;
	}

	public void setEventsLogFileLocation(String eventsLogFileLocation) {
		this.eventsLogFileLocation = eventsLogFileLocation;
	}

	public String getInfoContactoFormLocation() {
		return infoContactoFormLocation;
	}

	public void setInfoContactoFormLocation(String infoContactoFormLocation) {
		this.infoContactoFormLocation = infoContactoFormLocation;
	}
}
