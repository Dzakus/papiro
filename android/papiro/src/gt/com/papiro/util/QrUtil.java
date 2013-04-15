package gt.com.papiro.util;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.graphics.Bitmap;
import android.graphics.Color;

public class QrUtil {

	public QrUtil() {
	}
	
	public static Bitmap generateQrCodeFromString(String message, int width, int height) {
		com.google.zxing.Writer qrWriter = new QRCodeWriter();		
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		try {
			BitMatrix matrix = qrWriter.encode(message, com.google.zxing.BarcodeFormat.QR_CODE, width, height);			
			for (int i = 0; i < matrix.getHeight(); i++) {
				for (int j = 0; j < matrix.getWidth(); j++) {
					if (matrix.get(j, i)) {
						bitmap.setPixel(j, i, Color.BLACK);
					} else {
						bitmap.setPixel(j, i, Color.WHITE);
					}
				}
			}
		} catch (WriterException e) {		
			e.printStackTrace();
		}
		
		return bitmap;
	}

}
