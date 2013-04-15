package gt.com.papiro.test;

import gt.com.papiro.AdDetailActivity;
import gt.com.papiro.AdDetailFragment;
import gt.com.papiro.AdListFragment;
import gt.com.papiro.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TestActivity extends Activity implements AdListFragment.Callbacks {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.test_test_activity);
	}

	public void onItemSelected(String id) {
		// TODO Auto-generated method stub
		Bundle arguments = new Bundle();
		arguments.putString(AdDetailFragment.ARG_ITEM_ID, id);
		AdDetailFragment fragment = new AdDetailFragment();
		fragment.setArguments(arguments);
		getFragmentManager().beginTransaction()
				.replace(R.id.ad_detail_container, fragment).commit();

	}

}
