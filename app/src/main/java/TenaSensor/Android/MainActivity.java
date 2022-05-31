package TenaSensor.Android;

import android.bluetooth.le.ScanCallback;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static final int MENU_LIST = 0;
    public static final int MENU_BYE = 1;

    private static final long SCAN_PERIOD = 5000;

    private static final int REQUEST_ENABLE_BT = 1;

    private static final String TAG = "BLE_Myo";
    private static final String FORMAT = "%2d";
    public int gestureCounter = 0;
    TextView countdown;
    //An array containing your icons from the drawable directory
    final int[] ICONS = new int[]{
            R.drawable.bottom_nav_patient_messages,
            R.drawable.bottom_nav_patient_mycareplan,
            R.drawable.sensorwhitemdpi,
            R.drawable.resources
    };

    /***********************Below ADDED BY CHARLES FOR SWIPEABLE TABS***************************/

    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;
    TabLayout tabLayout;
    private ScanCallback scanCallback = new ScanCallback() {
    };
    private Button homeBtn, settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) this.findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        TabLayout.Tab EMGTab = tabLayout.newTab();
        TabLayout.Tab FeatureTab = tabLayout.newTab();
        TabLayout.Tab IMUTab = tabLayout.newTab();
        TabLayout.Tab ClassificationTab = tabLayout.newTab();

        tabLayout.addTab(EMGTab, 0, true);
        tabLayout.addTab(FeatureTab, 1, true);
        tabLayout.addTab(IMUTab, 2, true);
        tabLayout.addTab(ClassificationTab, 3, true);

        tabLayout.setupWithViewPager(mViewPager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);
        tabLayout.getTabAt(3).setIcon(ICONS[3]);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header);

        findViewById(R.id.settings).setVisibility(View.VISIBLE);

        homeBtn = (Button)findViewById(R.id.home_button);
        settingsBtn = (Button)findViewById(R.id.settings_button);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BluetoothSelection.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.connect:
            case MENU_LIST:
                Intent intent = new Intent(this, BluetoothSelection.class);
                startActivity(intent);
                return true;

        }
        return false;
    }
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class TabsAdapter extends FragmentStatePagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

        private final MainActivity mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        private final ArrayList<Fragment> mFrag = new ArrayList<Fragment>();

        public TabsAdapter(MainActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActionBar = activity.getSupportActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    Log.d("Tab", String.valueOf(i));
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            //set booleans to kill unseen processes
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            //set booleans to kill unseen processes
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return mFrag.get(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
            /*
            MenuInflater inflater = mContext.getSupportMenuInflater();
            mContext.menu.clear();
            inflater.inflate(R.menu.MainActivity, mContext.menu);
            */
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }
    }
}


