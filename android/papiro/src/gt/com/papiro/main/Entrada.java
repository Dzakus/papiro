package gt.com.papiro.main;

import gt.com.papiro.interactivo.Exposicion;
import gt.com.papiro.util.SystemUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;

public class Entrada extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Entrada.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		SystemUtil.hideStatusBar();
		
		Intent intent = new Intent(this, Exposicion.class);		
		startActivity(intent);
	}

}
