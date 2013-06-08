package gt.com.papiro.sincronizador.model;

import java.util.Date;

public class DispositivoConfig {
	private String id;
	private Periodo periodoSincronizacion;
	private Date ultimaSincronizacion;
	private int horaPreferida;
	private String usuario;
	
	public DispositivoConfig(){
		this(null);
	}
	
	public DispositivoConfig(String id){
		this.id = id;
		periodoSincronizacion = Periodo.Diario;
		horaPreferida = 0;
		ultimaSincronizacion = null;
	}
	
	public enum Periodo{
		Diario, CadaHora, Semanal, Mensual, Quincenal
	}

	public Date getUltimaSincronizacion() {
		return ultimaSincronizacion;
	}

	public void setUltimaSincronizacion(Date ultimaSincronizacion) {
		this.ultimaSincronizacion = ultimaSincronizacion;
	}

	public Periodo getPeriodoSincronizacion() {
		return periodoSincronizacion;
	}

	public void setPeriodoSincronizacion(Periodo periodoSincronizacion) {
		this.periodoSincronizacion = periodoSincronizacion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getHoraPreferida() {
		return horaPreferida;
	}

	public void setHoraPreferida(int horaPreferida) {
		this.horaPreferida = horaPreferida;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
}
