package TenaSensor.Android;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 4;
    private String[] tabTitles = new String[]{"Sensor", "FEATURES", "IMU", "CLASSIFIER"};

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                BluetoothConnect tab1 = new BluetoothConnect();
                return tab1;
            case 1:
                BluetoothConnect tab2 = new BluetoothConnect();
                return tab2;
            case 2:
                BluetoothConnect tab3 = new BluetoothConnect();
                return tab3;
            case 3:
                BluetoothConnect tab4 = new BluetoothConnect();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
