package gt.com.papiro.util;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class HttpUtil {
	private static final String TAG = "HttpUtil";

	public HttpUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean postData(String uri, List<NameValuePair> nameValuePairs) {

		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(uri);

		    try {
		        // Add your data
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        Log.v(TAG, "Respuesta del informacion de contacto: " + response.getStatusLine());
		        return true;
		    } catch (Exception e) {
		    	e.printStackTrace();
		    } 
		    return false;
	}

}
