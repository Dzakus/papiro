package gt.com.papiro.main;

import gt.com.papiro.interactivo.Exposicion;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class Entrada extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Entrada.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		Intent intent = new Intent(this, Exposicion.class);		
		startActivity(intent);
	}

}
