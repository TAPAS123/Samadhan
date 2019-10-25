package groupmanager.samadhan;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by intel on 29-06-2017.
 */

public class Adapter_ViewPager extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabTitle = new ArrayList<>();


    public void addFragment(Fragment fragments, String tabTitle )
    {
        this.fragments.add(fragments);
        this.tabTitle.add(tabTitle);
    }

    public Adapter_ViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitle.get(position);
    }
}
