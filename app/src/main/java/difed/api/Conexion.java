package difed.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import difed.util.CustomLog;


public class Conexion {
    
    public static final int POST_TYPE = 1;
    public static final int GET_TYPE = 2;
    
    private static final String TIEMPO = "http://www.aemet.es/es/eltiempo/prediccion/provincias?p=";
    private static final String TIEMPO2 = "&w=";
    private static final String TRAFICO = "http://infocar.dgt.es/etraffic/Incidencias?orden=fechahora_ini%20DESC&provIci=";
    private static final String TRAFICO2 = "&ca=";
    private static final String TRAFICO3 = "&IncidenciasRETENCION=IncidenciasRETENCION&IncidenciasPUERTOS=IncidenciasPUERTOS&" +
    										"IncidenciasMETEOROLOGICA=IncidenciasMETEOROLOGICA&IncidenciasOBRAS=IncidenciasOBRAS" +
    										"&IncidenciasOTROS=IncidenciasOTROS&IncidenciasEVENTOS=IncidenciasEVENTOS";
    private static final String SANTORAL = "http://www.santoral.com.es/santos-dia.php?dia=";
    private static final String SANTORAL2 = "&mes=";
    
    
    
    //private static final String HOME = "http://www.projectsexception.com:8443";
    //private static final String HOME2 = "";
   
    
    private String comunidad;
    private String provincia;
    private String tipo;
    
    String url;
    
    
    
    
    /**
     * 
     * @param provincia
     * @param tipo   tiempo, trafico , santoral
     */
    public Conexion(String provincia, String tipo) {
        
        
            this.provincia = provincia;
            this.tipo=tipo;
        
            url=TIEMPO + provincia + TIEMPO2;    
        
    }
    
    public Conexion(String tipo) {
        
        this.tipo=tipo;
        Calendar c = Calendar.getInstance();
		
		int dia = (c.get(Calendar.DATE));
		int mes = (c.get(Calendar.MONTH)) + 1;
		
		url=SANTORAL + pad(dia) + SANTORAL2 + pad(mes);    
    
    }
    
    
	public Conexion(String Comunidad, String provincia, String tipo) {
        
        this.setComunidad(Comunidad);
        this.provincia = provincia;
        this.tipo=tipo;
    
       	url=TRAFICO + provincia + TRAFICO2 + Comunidad + TRAFICO3;    
       
    }
    
    public void conexionEspecial(String provincia){
    	
    	if (provincia.equalsIgnoreCase("Sevilla")){
    		url="http://www.trajano.com/trafico.js";
    	}
    	if (provincia.equalsIgnoreCase("Granada")){
    		url="http://www.movilidadgranada.com/index.php";
    	}
    }
   
    private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	} 
    
   
    public String getProvincia() {
		return provincia;
	}



	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}



	public String getTipo() {
		return tipo;
	}



	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

    
     
    public static void checkConnection(Context context) throws ConnectionException {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!networkInfo.isConnected()) {
            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        }
        if (!networkInfo.isConnected()) {
            throw new ConnectionException(ConnectionException.ConnectionError.NO_CONNECTION);
        }
    }
    

    @SuppressWarnings("deprecation")
    public InputStream conectar(Context context) throws ConnectionException {
        
        checkConnection(context);
        
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        int timeoutConnection = 5000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        DefaultHttpClient client = new DefaultHttpClient(httpParameters);
        
        HttpRequestBase method = null;
        try {
            //method = new HttpPost(url);
            method = new HttpGet(url);
        } catch (IllegalArgumentException e) {
            CustomLog.error("conectar", e.getMessage());
        }
        
        InputStream result = null;
        if (method != null) {
            try {
                HttpResponse response = client.execute(method);
                StatusLine status = response.getStatusLine();
                int codigo = status.getStatusCode();
                CustomLog.debug("conectar", "Status code: " + codigo);
                if (codigo == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        try {
                            result = entity.getContent();
                        } catch (IOException e) {
                            CustomLog.error("conectar", e.getMessage());
                            throw new ConnectionException(ConnectionException.ConnectionError.READING_ERROR);
                        }
                    } else {
                        CustomLog.error("conectar", status.getReasonPhrase());
                        throw new ConnectionException(ConnectionException.ConnectionError.NO_DATA);
                    }
                } else {
                    throw new ConnectionException(ConnectionException.ConnectionError.getErrorByCode(codigo));
                }
            } catch (IOException e) {
                CustomLog.error("conectar", "Error: " + e.getMessage()); // Conexi�n rechazada
                throw new ConnectionException(ConnectionException.ConnectionError.CONNECTION_REJECTED);
            }
        }
        return result;

    }

	public String getComunidad() {
		return comunidad;
	}

	public void setComunidad(String comunidad) {
		this.comunidad = comunidad;
	}
    
    

}