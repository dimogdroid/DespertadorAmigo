package difed.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BdDespertadorAmigo extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "alarmcartime.db";
    private static final int DATABASE_VERSION = 1;
    
    public final static String TABLA_ALARMAS = "alarmas";
    public final static String TABLA_CONFIG = "configuracion";
    
    public static final String COL_ID_ALARMA ="id";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_HORA = "hora";
    public static final String COL_MINUTOS = "minutos";
    public static final String COL_REPETIR = "repetir";  //Cadena con valores L,M,X,J,V,S,D
    public static final String COL_POSPONER = "posponer"; //Tiempo en minutos a posponer
    public static final String COL_TONO = "tono";  //Direccino del tono a sonar
    public static final String COL_VIBRACION = "vibracion";  //SI O NO
    public static final String COL_TIEMPO = "tiempo";
    public static final String COL_TRAFICO = "trafico";
    public static final String COL_SANTORAL = "santoral";
    public static final String COL_RECORDATORIO = "recordatorio";  //FRASE DE MAX 250 CARACATERES.
    public static final String COL_ACTIVA = "activa";
    
   public static final String COL_ANTESHABLAR = "anteshablar";
   public static final String COL_MODOAVION = "modoavion";
    
   
	public BdDespertadorAmigo(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		 String sql = "CREATE TABLE IF NOT EXISTS " + TABLA_ALARMAS + " ("
                 + COL_ID_ALARMA        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                 + COL_NOMBRE    + " TEXT , " 
                 + COL_HORA      + " TEXT, "
                 + COL_MINUTOS      + " TEXT, " 
                 + COL_REPETIR      + " TEXT, "
                 + COL_POSPONER      + " TEXT, "
                 + COL_TONO      + " TEXT, "
                 + COL_TIEMPO      + " TEXT, "
                 + COL_TRAFICO      + " TEXT, "
                 + COL_SANTORAL      + " BOOLEAN, "
                 + COL_RECORDATORIO + " TEXT, "
                 + COL_VIBRACION      + " BOOLEAN, "
                 + COL_ACTIVA + " BOOLEAN);";
		 
	      db.execSQL(sql);
	      
	      sql = "CREATE TABLE IF NOT EXISTS " + TABLA_CONFIG + " ("
	    		  	 + COL_ANTESHABLAR      + " INTEGER, "
	                 + COL_MODOAVION    + " BOOLEAN);";
			 
		      db.execSQL(sql);
	      
		  
		 sql = "INSERT INTO  " + TABLA_CONFIG + " (anteshablar, modoavion) VALUES (20, 1)";
		 	  db.execSQL(sql);
	    
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				
	}

}
