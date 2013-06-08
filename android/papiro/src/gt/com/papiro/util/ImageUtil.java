package gt.com.papiro.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;

public class ImageUtil {
	/**
	 * Helper Functions
	 * 
	 * @throws IOException
	 */
	public static Bitmap getBitmapFromAsset(Context ctx, String strName)
			throws IOException {
		if (ctx == null || strName == null) {
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
			return BitmapFactory.decodeStream(new URL(uri.toString())
					.openStream());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static int getSampleSize(String path, Rect rect) {
		float ratio;
		int imageWidth, imageHeight, k;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		imageHeight = opts.outHeight;
		imageWidth = opts.outWidth;

		ratio = imageWidth / rect.width();

		k = Integer.highestOneBit((int) Math.floor(ratio));

		if (k == 0)
			return 1;
		else
			return 1;
	}
	
	public static Bitmap decodeSampledBitmapFromFile(String path,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		System.out.println("Verificando height and height: " + height + "=" + reqHeight + ", width: " + width + "=" + reqWidth);
		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		System.out.println("Sample size: " + inSampleSize);
		return inSampleSize == 0? 1: inSampleSize;
	}
}
