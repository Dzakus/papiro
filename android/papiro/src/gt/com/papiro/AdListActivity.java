package gt.com.papiro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class AdListActivity extends Activity
        implements AdListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);

        if (findViewById(R.id.ad_detail_container) != null) {
            mTwoPane = true;
            ((AdListFragment) getFragmentManager()
                    .findFragmentById(R.id.ad_list))
                    .setActivateOnItemClick(true);
        }
    }

    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(AdDetailFragment.ARG_ITEM_ID, id);
            AdDetailFragment fragment = new AdDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.ad_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, AdDetailActivity.class);
            detailIntent.putExtra(AdDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
