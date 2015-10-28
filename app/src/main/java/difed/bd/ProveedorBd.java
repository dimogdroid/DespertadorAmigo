package difed.bd;

import java.util.List;

import difed.entidades.Alarma;

public interface ProveedorBd {
	
	public Alarma getAlarma(int id);
	
	public List<Alarma> getAlarmas();
	
	public long guardarAlarma(Alarma alarma);
	
	public void modificarAlarma(int idAlarmaModificar, Alarma alarmaNueva);
	
	public void eliminarAlarma(int id);
	
	public void alarmaOn(int id);
	public void alarmaOff(int id);
	
	public int tiempoPosponer(Alarma alarma);
	
	public int repetirAl(Alarma alarma);
	
	public String obtenerIdProvinciaTiempo(String Provincia);
	
	public String obtenerIdComunidadTrafico(String Provincia);
	public String obtenerIdProvinciaTrafico(String Provincia);
	
	public String ontenerRecordatorio(int id);
	
	public int tiempoComenzarHablar();
	public void modificarTiempoComenzarHablas(int tiempo);
	
	public boolean vermodoavion();
	public void modificarModoavion(Boolean valor);
	
	public int diaEnInt(String dia);
	public String diaEnString(int dia);
	public List<Alarma> alarmaActivasNunca();
	public List<Alarma> alarmaActivasRepetir();
	public boolean existenActivas();

	public List<Alarma> alarmasActivas();
	
	
}
