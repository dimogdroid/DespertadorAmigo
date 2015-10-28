package difed.entidades;


public class Alarma{

	private int id;
	private String nombre;
	private String hora;
	private String minutos;
	private String repetir;  //Cadena con valores L,M,X,J,V,S,D
	private String posponer; //Tiempo en minutos a posponer
	private String tono;  //Direccino del tono a sonar
	private boolean vibracion;  //SI O NO
	private String tiempo;
	private String trafico;
	private boolean santoral;
	private String recordatorio;
	
	
	private boolean activa;  //Si o No

   //TODO Cambiar para construir con todos los campos
    public Alarma(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Alarma(int id, String nombre, String hora, String minutos, String repetir,
                  String posponer, String tono, boolean vibracion, String tiempo,
                  String trafico, boolean santoral, String recordatorio, boolean activa) {
        this.id = id;
        this.nombre = nombre;
        this.hora = hora;
        this.minutos = minutos;
        this.repetir = repetir;
        this.posponer = posponer;
        this.tono = tono;
        this.vibracion = vibracion;
        this.tiempo = tiempo;
        this.trafico = trafico;
        this.santoral = santoral;
        this.recordatorio = recordatorio;
        this.activa = activa;
    }

    public Alarma() {
    }

    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public String getRepetir() {
		return repetir;
	}
	public void setRepetir(String repetir) {
		this.repetir = repetir;
	}
	public String getPosponer() {
		return posponer;
	}
	public void setPosponer(String posponer) {
		this.posponer = posponer;
	}
	public String getTono() {
		return tono;
	}
	public void setTono(String tono) {
		this.tono = tono;
	}
	
	public String getTiempo() {
		return tiempo;
	}
	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}
	public String getTrafico() {
		return trafico;
	}
	public void setTrafico(String trafico) {
		this.trafico = trafico;
	}
	
	public String getRecordatorio() {
		return recordatorio;
	}
	public void setRecordatorio(String recordatorio) {
		this.recordatorio = recordatorio;
	}
	public String getMinutos() {
		return minutos;
	}
	public void setMinutos(String minutos) {
		this.minutos = minutos;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	public boolean isVibracion() {
		return vibracion;
	}
	public void setVibracion(boolean vibracion) {
		this.vibracion = vibracion;
	}
	public boolean isSantoral() {
		return santoral;
	}
	public void setSantoral(boolean santoral) {
		this.santoral = santoral;
	}  
	
	
	
	
}
