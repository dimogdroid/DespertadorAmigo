package difed.bd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import difed.entidades.Alarma;


public class ProveedorBdImpl implements ProveedorBd {

	private BdDespertadorAmigo bdAlarma;
	private static ProveedorBdImpl bd;

	@SuppressWarnings("static-access")
	String[] campos = new String[] { bdAlarma.COL_ID_ALARMA,
			bdAlarma.COL_NOMBRE, bdAlarma.COL_HORA, bdAlarma.COL_MINUTOS,
			bdAlarma.COL_REPETIR, bdAlarma.COL_POSPONER, bdAlarma.COL_TONO,
			bdAlarma.COL_VIBRACION, bdAlarma.COL_TIEMPO, bdAlarma.COL_TRAFICO,
			bdAlarma.COL_SANTORAL, bdAlarma.COL_RECORDATORIO,
			bdAlarma.COL_ACTIVA };

	@SuppressWarnings("static-access")
	String[] camposConfig = new String[] { bdAlarma.COL_ANTESHABLAR , bdAlarma.COL_MODOAVION};

	private ProveedorBdImpl(Context context) {
		bdAlarma = new BdDespertadorAmigo(context);
		bdAlarma.getWritableDatabase();
	}

	public static ProveedorBd getProveedor(Context context) {
		if (bd == null) {
			bd = new ProveedorBdImpl(context);
		}
		return bd;
	}

	@Override
	public Alarma getAlarma(int id) {

		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		Cursor c = db.query(bdAlarma.TABLA_ALARMAS, campos,
				bdAlarma.COL_ID_ALARMA + " = ? ",
				new String[] { Integer.toString(id) }, null, null, null);

		Alarma alarma = new Alarma();
		if (c != null) {
			if (c.moveToNext()) {
				alarma.setId(c.getInt(0));
				alarma.setNombre(c.getString(1));
				alarma.setHora(c.getString(2));
				alarma.setMinutos(c.getString(3));
				alarma.setRepetir(c.getString(4));
				alarma.setPosponer(c.getString(5));
				alarma.setTono(c.getString(6));
				alarma.setVibracion(c.getInt(7) > 0);
				alarma.setTiempo(c.getString(8));
				alarma.setTrafico(c.getString(9));
				alarma.setSantoral(c.getInt(10) > 0);
				alarma.setRecordatorio(c.getString(11));

				alarma.setActiva(c.getInt(12) > 0);
			}
		}
		closeResource(c);
		return alarma;
	}

	@Override
	public List<Alarma> getAlarmas() {

		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		Cursor c = db.query(bdAlarma.TABLA_ALARMAS, campos, null, null, null,
				null, "id");

		List<Alarma> lAlarmas = new ArrayList<Alarma>();
		if (c != null) {
			Alarma alarma;
			while (c.moveToNext()) {
				alarma = new Alarma();
				alarma.setId(c.getInt(0));
				alarma.setNombre(c.getString(1));
				alarma.setHora(c.getString(2));
				alarma.setMinutos(c.getString(3));
				alarma.setRepetir(c.getString(4));
				alarma.setPosponer(c.getString(5));
				alarma.setTono(c.getString(6));
				alarma.setVibracion(c.getInt(7) > 0);
				alarma.setTiempo(c.getString(8));
				alarma.setTrafico(c.getString(9));
				alarma.setSantoral(c.getInt(10) > 0);
				alarma.setRecordatorio(c.getString(11));

				alarma.setActiva(c.getInt(12) > 0);

				lAlarmas.add(alarma);
			}

		}
		closeResource(c);
		
		return lAlarmas;
	}

	@SuppressWarnings("static-access")
	@Override
	public void modificarAlarma(int idAlarmaModificar, Alarma alarmaNueva) {
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(bdAlarma.COL_NOMBRE, alarmaNueva.getNombre().trim());
		values.put(bdAlarma.COL_HORA, alarmaNueva.getHora().trim());
		values.put(bdAlarma.COL_MINUTOS, alarmaNueva.getMinutos().trim());
		values.put(bdAlarma.COL_REPETIR, alarmaNueva.getRepetir().trim());
		values.put(bdAlarma.COL_POSPONER, alarmaNueva.getPosponer().trim());
		values.put(bdAlarma.COL_TONO, alarmaNueva.getTono().trim());
		values.put(bdAlarma.COL_VIBRACION, alarmaNueva.isVibracion());
		values.put(bdAlarma.COL_TIEMPO, alarmaNueva.getTiempo().trim());
		values.put(bdAlarma.COL_TRAFICO, alarmaNueva.getTrafico().trim());
		values.put(bdAlarma.COL_SANTORAL, alarmaNueva.isSantoral());
		values.put(bdAlarma.COL_RECORDATORIO, alarmaNueva.getRecordatorio()
				.trim());
		values.put(bdAlarma.COL_ACTIVA, alarmaNueva.isActiva());

		long id = db.update(bdAlarma.TABLA_ALARMAS, values,
				bdAlarma.COL_ID_ALARMA + " = ?",
				new String[] { String.valueOf(idAlarmaModificar) });

	}

	@SuppressWarnings("static-access")
	@Override
	public void alarmaOn(int id) {
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(bdAlarma.COL_ACTIVA, true);

		long idUpdate = db.update(bdAlarma.TABLA_ALARMAS, values,
				bdAlarma.COL_ID_ALARMA + " = ?",
				new String[] { String.valueOf(id) });

	}

	@Override
	public void alarmaOff(int id) {
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(bdAlarma.COL_ACTIVA, false);

		long idUpdate = db.update(bdAlarma.TABLA_ALARMAS, values,
				bdAlarma.COL_ID_ALARMA + " = ?",
				new String[] { String.valueOf(id) });

	}

	@Override
	public void eliminarAlarma(int id) {

		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		 db.delete(bdAlarma.TABLA_ALARMAS, bdAlarma.COL_ID_ALARMA + " = ?",
		 new String[] { String.valueOf(id) });

		//db.delete(bdAlarma.TABLA_ALARMAS, null, null);
	}

	@SuppressWarnings({ "static-access" })
	@Override
	public long guardarAlarma(Alarma alarma) {

		SQLiteDatabase db = bdAlarma.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(bdAlarma.COL_NOMBRE, alarma.getNombre().trim());
		values.put(bdAlarma.COL_HORA, alarma.getHora().trim());
		values.put(bdAlarma.COL_MINUTOS, alarma.getMinutos().trim());
		values.put(bdAlarma.COL_REPETIR, alarma.getRepetir().trim());
		values.put(bdAlarma.COL_POSPONER, alarma.getPosponer().trim());
		values.put(bdAlarma.COL_TONO, alarma.getTono().trim());
		values.put(bdAlarma.COL_VIBRACION, alarma.isVibracion());
		values.put(bdAlarma.COL_TIEMPO, alarma.getTiempo().trim());
		values.put(bdAlarma.COL_TRAFICO, alarma.getTrafico().trim());
		values.put(bdAlarma.COL_SANTORAL, alarma.isSantoral());
		values.put(bdAlarma.COL_RECORDATORIO, alarma.getRecordatorio().trim());
		values.put(bdAlarma.COL_ACTIVA, alarma.isActiva());

		long id = db.insert(bdAlarma.TABLA_ALARMAS, null, values);

		return id;

	}

	@Override
	public int tiempoPosponer(Alarma alarma) {
		SQLiteDatabase db = bdAlarma.getWritableDatabase();
		
		int tiempoPost=0;
		
		Cursor c = db.query(bdAlarma.TABLA_ALARMAS, campos,
				bdAlarma.COL_ID_ALARMA + " = ? ",
				new String[] { Integer.toString(alarma.getId()) }, null, null,
				null);

		String tiempoPos = "";

		Alarma alarma1 = new Alarma();
		if (c != null) {
			if (c.moveToNext()) {
				tiempoPos = c.getString(5);
				if ((tiempoPos != null) && (!tiempoPos.equalsIgnoreCase(""))) {

					String tiempoP = tiempoPos.substring(0,
							tiempoPos.indexOf(" "));
					tiempoPost = Integer.valueOf(tiempoP);

				} 
			}
		}
		closeResource(c);
		return tiempoPost;

	}

	@Override
	public int tiempoComenzarHablar() {
		
		int tiempoADevolver=10;
		
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		Cursor c = db.query(bdAlarma.TABLA_CONFIG, camposConfig, null, null,
				null, null, null);

		if (c != null) {
			if (c.moveToNext()) {
				tiempoADevolver = c.getInt(0);
			}
		}
		closeResource(c);
		return tiempoADevolver;
	}
	
	@Override
	public void modificarTiempoComenzarHablas(int tiempo) {
		SQLiteDatabase db = bdAlarma.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(bdAlarma.COL_ANTESHABLAR, tiempo);
		
		long id = db.update(bdAlarma.TABLA_CONFIG, values,
				null,
				null);

		
	}
	
	@Override
	public void modificarModoavion(Boolean valor) {
		SQLiteDatabase db = bdAlarma.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(bdAlarma.COL_MODOAVION, valor);
		
		long id = db.update(bdAlarma.TABLA_CONFIG, values,
				null,
				null);
		
	}
	
	@Override
	public boolean vermodoavion() {
		
		boolean modoAv=true;
		
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		Cursor c = db.query(bdAlarma.TABLA_CONFIG, camposConfig,
				null,null, null, null,
				null);

		

		if (c != null) {
			if (c.moveToNext()) {
				modoAv=c.getInt(1) >0;
			}
		}
		closeResource(c);
		return modoAv;
	}
	
	

	@Override
	public String ontenerRecordatorio(int id) {
		
		String recordatorio=null;
				
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		Cursor c = db.query(bdAlarma.TABLA_ALARMAS, campos,
				bdAlarma.COL_ID_ALARMA + " = ? ",
				new String[] { Integer.toString(id) }, null, null,
				null);

		

		if (c != null) {
			if (c.moveToNext()) {
				recordatorio= c.getString(11); 
			}
		}
		closeResource(c);
		return recordatorio;
	}
	
	

	@Override
	public int repetirAl(Alarma alarma) {
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		Cursor c = db.query(bdAlarma.TABLA_ALARMAS, campos,
				bdAlarma.COL_ID_ALARMA + " = ? ",
				new String[] { Integer.toString(alarma.getId()) }, null, null,
				null);

		String diasRepetir = "";
		
		String diaHoy = "";
		String diaReturn="";
		

		// fecha actual
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());

		int hoy = cal.get(Calendar.DAY_OF_WEEK );

		switch (hoy) {
		case 1:
			diaHoy= "Domingo";
			break;
		case 2:
			diaHoy=  "Lunes";
			break;
		case 3:
			diaHoy=  "Martes";
			break;
		case 4:
			diaHoy=  "Miércoles";
			break;
		case 5:
			diaHoy=  "Jueves";
			break;
		case 6:
			diaHoy=  "Viernes";
			break;
		case 7:
			diaHoy=  "Sábado";
			break;
		}

		if (c != null) {
			if (c.moveToNext()) {
				diasRepetir = c.getString(4);
				String[] arrayDias=diasRepetir.split(",");
				if ((diasRepetir != null) && (!diasRepetir.equalsIgnoreCase("Nunca"))) {
				
					// 1. Solo hay 1, sin comas.
					if (arrayDias.length==1) {
						return diaEnInt(arrayDias[0]);
					}else{
						int i=0;
						boolean Encontrado=false;
						while ((i<arrayDias.length) && (!Encontrado)){
							if (diaHoy.equalsIgnoreCase(arrayDias[i])){
								Encontrado=true;
							}else{
								i++;								
							}
						}
						// 2. Existe el dia de hoy en la cadena, entonces buscar
						// el siguiente
						if (Encontrado){
							if (i<arrayDias.length){
								i++;
								diaReturn=arrayDias[i];
								closeResource(c);
								return diaEnInt(diaReturn.trim());
							}else{ //Era el ultimo de la cadena
								diaReturn=arrayDias[0];
								closeResource(c);
								return diaEnInt(diaReturn.trim());
							}	
						}else{
							//sino el primero.
							
							
						}
						
						
						
		
						// 3. No existe, mirar que dia es hoy y hacer un bucle
						// para encontrar el siguiente.
					}

				} else {
					closeResource(c);
					return -1;
				}
			}
		}
		closeResource(c);
		return -1;
	}
	
	
	public int diaEnInt(String dia){
		if (dia.equalsIgnoreCase("Dom")){
			return 1;
		}
		if (dia.equalsIgnoreCase("Lun")){
			return 2;
		}
		if (dia.equalsIgnoreCase("Mar")){
			return 3;
		}
		if (dia.equalsIgnoreCase("Mie")){
			return 4;
		}
		if (dia.equalsIgnoreCase("Jue")){
			return 5;
		}
		if (dia.equalsIgnoreCase("Vie")){
			return 6;
		}
		if (dia.equalsIgnoreCase("Sab")){
			return 7;
		}
		return -1;
	}
	
	public String diaEnString(int dia){
		dia --;
		
		if (dia==1){
			return "Domingo";
		}
		if (dia==2){
			return "Lunes";
		}
		if (dia==3){
			return "Martes";
		}
		if (dia==4){
			return "Miércoles";
		}
		if (dia==5){
			return "Jueves";
		}
		if (dia==6){
			return "Viernes";
		}
		if (dia==7){
			return "Sábado";
		}
		
		return null;
		
	}
	

	private static void closeResource(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

	@SuppressWarnings("unused")
	private String comillaSimple(String nombre) {

		String nombreComilla = nombre;

		if (nombreComilla.indexOf("'") >= 0) {
			nombreComilla = nombreComilla.replace("'", "''");
		}

		return nombreComilla;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String obtenerIdProvinciaTiempo(String Provincia) {

		HashMap provincias = new HashMap();

		provincias.put("Alava", "01");
		provincias.put("Albacete", "02");
		provincias.put("Alicante", "03");
		provincias.put("Almería", "04");
		provincias.put("Avila", "05");
		provincias.put("Badajoz", "06");
		provincias.put("Illes Baleares", "07");
		provincias.put("Barcelona", "08");
		provincias.put("Burgos", "09");
		provincias.put("Cáceres", "10");
		provincias.put("Cádiz", "11");
		provincias.put("Castellón", "12");
		provincias.put("Ciudad Real", "13");
		provincias.put("Córdoba", "14");
		provincias.put("A Coruña", "15");
		provincias.put("Cuenca", "16");
		provincias.put("Girona", "17");
		provincias.put("Granada", "18");
		provincias.put("Guadalajara", "19");
		provincias.put("Guipuzcoa", "20");
		provincias.put("Huelva", "21");
		provincias.put("Huesca", "22");
		provincias.put("Jaén", "23");
		provincias.put("León", "24");
		provincias.put("Lleida", "25");
		provincias.put("Rioja (La)", "26");
		provincias.put("Lugo", "27");
		provincias.put("Madrid", "28");
		provincias.put("Málaga", "29");
		provincias.put("Murcia", "30");
		provincias.put("Navarra", "31");
		provincias.put("Ourense", "32");
		provincias.put("Asturias", "33");
		provincias.put("Palencia", "34");
		provincias.put("Palmas (Las)", "35");
		provincias.put("Pontevedra", "36");
		provincias.put("Salamanca", "37");
		provincias.put("Santa Cruz de Tenerife", "38");
		provincias.put("Cantabria", "39");
		provincias.put("Segovia", "40");
		provincias.put("Sevilla", "41");
		provincias.put("Soria", "42");
		provincias.put("Tarragona", "43");
		provincias.put("Teruel", "44");
		provincias.put("Toledo", "45");
		provincias.put("Valencia", "46");
		provincias.put("Valladolid", "47");
		provincias.put("Vizcaya", "48");
		provincias.put("Zamora", "49");
		provincias.put("Zaragoza", "50");
		provincias.put("Ceuta", "51");
		provincias.put("Melilla", "52");

		return (String) provincias.get(Provincia);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String obtenerIdComunidadTrafico(String Provincia) {
		HashMap comunidad = new HashMap();

		comunidad.put("Almería", "1");
		comunidad.put("Cádiz", "1");
		comunidad.put("Córdoba", "1");
		comunidad.put("Granada", "1");
		comunidad.put("Huelva", "1");
		comunidad.put("Jaén", "1");
		comunidad.put("Málaga", "1");
		comunidad.put("Sevilla", "1");
		comunidad.put("Huesca", "2");
		comunidad.put("Teruel", "2");
		comunidad.put("Zaragoza", "2");
		comunidad.put("Asturias", "18");
		comunidad.put("Illes Baleares", "14");
		comunidad.put("Palmas (Las)", "3");
		comunidad.put("Santa Cruz de Tenerife", "3");
		comunidad.put("Cantabria", "4");
		comunidad.put("Albacete", "6");
		comunidad.put("Ciudad Real", "6");
		comunidad.put("Cuenca", "6");
		comunidad.put("Guadalajara", "6");
		comunidad.put("Toledo", "6");
		comunidad.put("Avila", "5");
		comunidad.put("Burgos", "5");
		comunidad.put("León", "5");
		comunidad.put("Palencia", "5");
		comunidad.put("Salamanca", "5");
		comunidad.put("Segovia", "5");
		comunidad.put("Soria", "5");
		comunidad.put("Valladolid", "5");
		comunidad.put("Zamora", "5");
		comunidad.put("Barcelona", "7");
		comunidad.put("Girona", "7");
		comunidad.put("Lleida", "7");
		comunidad.put("Tarragona", "7");
		comunidad.put("Alicante", "11");
		comunidad.put("Castellón", "11");
		comunidad.put("Valencia", "11");
		comunidad.put("Badajoz", "12");
		comunidad.put("Caceres", "12");
		comunidad.put("Coruña", "13");
		comunidad.put("Lugo", "13");
		comunidad.put("Orense", "13");
		comunidad.put("Pontevedra", "13");
		comunidad.put("Madrid", "9");
		comunidad.put("Murcia", "19");
		comunidad.put("Navarra", "10");
		comunidad.put("Alava", "17");
		comunidad.put("Guipuzcoa", "17");
		comunidad.put("Vizcaya", "17");
		comunidad.put("Rioja (La)", "15");
		comunidad.put("Ceuta", "8");
		comunidad.put("Melilla", "16");

		return (String) comunidad.get(Provincia);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String obtenerIdProvinciaTrafico(String Provincia) {
		HashMap provincias = new HashMap();

		provincias.put("Almería", "103");
		provincias.put("Cádiz", "107");
		provincias.put("Córdoba", "110");
		provincias.put("Granada", "112");
		provincias.put("Huelva", "114");
		provincias.put("Jaén", "25");
		provincias.put("Málaga", "118");
		provincias.put("Sevilla", "126");
		provincias.put("Huesca", "115");
		provincias.put("Teruel", "128");
		provincias.put("Zaragoza", "130");
		provincias.put("Asturias", "121");
		provincias.put("Illes Baleares", "105");
		provincias.put("Palmas (Las)", "122");
		provincias.put("Santa Cruz de Tenerife", "124");
		provincias.put("Cantabria", "125");
		provincias.put("Albacete", "3");
		provincias.put("Ciudad Real", "15");
		provincias.put("Cuenca", "17");
		provincias.put("Guadalajara", "20");
		provincias.put("Toledo", "47");
		provincias.put("Avila", "7");
		provincias.put("Burgos", "10");
		provincias.put("León", "56");
		provincias.put("Palencia", "36");
		provincias.put("Salamanca", "123");
		provincias.put("Segovia", "41");
		provincias.put("Soria", "43");
		provincias.put("Valladolid", "49");
		provincias.put("Zamora", "51");
		provincias.put("Barcelona", "106");
		provincias.put("Girona", "111");
		provincias.put("Lleida", "116");
		provincias.put("Tarragona", "127");
		provincias.put("Alicante", "102");
		provincias.put("Castellón", "108");
		provincias.put("Valencia", "48");
		provincias.put("Badajoz", "104");
		provincias.put("Cáceres", "11");
		provincias.put("A Coruña", "53");
		provincias.put("Lugo", "52");
		provincias.put("Ourense", "55");
		provincias.put("Pontevedra", "54");
		provincias.put("Madrid", "30");
		provincias.put("Murcia", "119");
		provincias.put("Navarra", "120");
		provincias.put("Alava", "101");
		provincias.put("Guipuzcoa", "113");
		provincias.put("Vizcaya", "129");
		provincias.put("Rioja (La)", "117");
		provincias.put("Ceuta", "109");
		provincias.put("Melilla", "131");

		return (String) provincias.get(Provincia);
	}
	
	
	@Override
	public boolean existenActivas() {
		boolean existenAct=false;
		
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		Cursor c = db.query(bdAlarma.TABLA_ALARMAS, campos,
				bdAlarma.COL_ACTIVA + " = 1",null, null, null,
				null);

		

		if (c != null) {
			if (c.moveToNext()) {
				existenAct=c.getInt(12) >0;
			}
		}
		closeResource(c);
		return existenAct;
		
	}


	@Override
	public List<Alarma> alarmaActivasNunca() {
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		Cursor c = db.query(bdAlarma.TABLA_ALARMAS, campos, 
				bdAlarma.COL_ACTIVA + " = 1 AND " + bdAlarma.COL_REPETIR + "='Nunca'",
				null, null,
				null, "hora, minutos Asc");

		List<Alarma> lAlarmas = new ArrayList<Alarma>();
		if (c != null) {
			Alarma alarma;
			while (c.moveToNext()) {
				alarma = new Alarma();
				alarma.setId(c.getInt(0));
				alarma.setNombre(c.getString(1));
				alarma.setHora(c.getString(2));
				alarma.setMinutos(c.getString(3));
				alarma.setRepetir(c.getString(4));
				alarma.setPosponer(c.getString(5));
				alarma.setTono(c.getString(6));
				alarma.setVibracion(c.getInt(7) > 0);
				alarma.setTiempo(c.getString(8));
				alarma.setTrafico(c.getString(9));
				alarma.setSantoral(c.getInt(10) > 0);
				alarma.setRecordatorio(c.getString(11));

				alarma.setActiva(c.getInt(12) > 0);

				lAlarmas.add(alarma);
			}

		}
		closeResource(c);
		
		return lAlarmas;
		
	}
	
	
	@Override
	public List<Alarma> alarmasActivas() {
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		Cursor c = db.query(bdAlarma.TABLA_ALARMAS, campos, 
				bdAlarma.COL_ACTIVA + " = 1 ",
				null, null,
				null, null);

		List<Alarma> lAlarmas = new ArrayList<Alarma>();
		if (c != null) {
			Alarma alarma;
			while (c.moveToNext()) {
				alarma = new Alarma();
				alarma.setId(c.getInt(0));
				alarma.setNombre(c.getString(1));
				alarma.setHora(c.getString(2));
				alarma.setMinutos(c.getString(3));
				alarma.setRepetir(c.getString(4));
				alarma.setPosponer(c.getString(5));
				alarma.setTono(c.getString(6));
				alarma.setVibracion(c.getInt(7) > 0);
				alarma.setTiempo(c.getString(8));
				alarma.setTrafico(c.getString(9));
				alarma.setSantoral(c.getInt(10) > 0);
				alarma.setRecordatorio(c.getString(11));

				alarma.setActiva(c.getInt(12) > 0);

				lAlarmas.add(alarma);
			}

		}
		closeResource(c);
		
		return lAlarmas;
		
	}
	

	@Override
	public List<Alarma> alarmaActivasRepetir() {
		SQLiteDatabase db = bdAlarma.getWritableDatabase();

		Cursor c = db.query(bdAlarma.TABLA_ALARMAS, campos, 
				bdAlarma.COL_ACTIVA + " = 1 AND " + bdAlarma.COL_REPETIR + "<>'Nunca'",
				null, null,
				null, bdAlarma.COL_REPETIR + " Asc");

		List<Alarma> lAlarmas = new ArrayList<Alarma>();
		if (c != null) {
			Alarma alarma;
			while (c.moveToNext()) {
				alarma = new Alarma();
				alarma.setId(c.getInt(0));
				alarma.setNombre(c.getString(1));
				alarma.setHora(c.getString(2));
				alarma.setMinutos(c.getString(3));
				alarma.setRepetir(c.getString(4));
				alarma.setPosponer(c.getString(5));
				alarma.setTono(c.getString(6));
				alarma.setVibracion(c.getInt(7) > 0);
				alarma.setTiempo(c.getString(8));
				alarma.setTrafico(c.getString(9));
				alarma.setSantoral(c.getInt(10) > 0);
				alarma.setRecordatorio(c.getString(11));

				alarma.setActiva(c.getInt(12) > 0);

				lAlarmas.add(alarma);
			}

		}
		closeResource(c);
		
		return lAlarmas;
	}

	
	

	




}
