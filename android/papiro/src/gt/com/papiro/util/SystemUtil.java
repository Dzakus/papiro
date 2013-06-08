package gt.com.papiro.util;

import java.io.IOException;

import android.util.Log;

public class SystemUtil {

	public SystemUtil() {

	}

	public static void showStatusBar() {
		try {
			Process proc = Runtime.getRuntime().exec(new String[] { "am", "startservice", "-n", "com.android.systemui/.SystemUIService" });
			proc.waitFor();
		} catch (Exception e) {
			Log.e("SystemUtil", "Error al ocultar la barra de estado", e);
		}
	}

	public static void hideStatusBar() {
		Process proc;
		try {
			proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "service call activity 42 s16 com.android.systemui" });
			proc.waitFor();
		} catch (Exception e) {
			Log.e("SystemUtil", "Error al ocultar la barra de estado", e);
		}
	}

}
