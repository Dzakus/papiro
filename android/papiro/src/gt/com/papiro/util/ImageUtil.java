package gt.com.papiro.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class ImageUtil {
    /**
     * Helper Functions
     * @throws IOException 
     */
    public static Bitmap getBitmapFromAsset(Context ctx, String strName) throws IOException
    {
    	if (ctx == null|| strName == null) {
    		return null;
    	}
        AssetManager assetManager = ctx.getAssets();

        InputStream istr = assetManager.open(strName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);

        return bitmap;
    }
    
    public static Bitmap getBitmapFromUri(Uri uri) {
    	if (uri == null) {
    		return null;
    	}
    	try {
			return BitmapFactory.decodeStream(new URL(uri.toString()).openStream());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	
    	return null;
    }
}
