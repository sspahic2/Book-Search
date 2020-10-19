package ba.unsa.etf.rma.booksearch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ba.unsa.etf.rma.booksearch.browser.BrowserFragment;
import ba.unsa.etf.rma.booksearch.list.SearchFragment;
import ba.unsa.etf.rma.booksearch.popular.Popular;

public class PageAdapter extends FragmentPagerAdapter {
    private int numberOfTabs;
    private BrowserFragment bFragment = null;
    private SearchFragment sFragment = null;
    private Popular pFragment = null;
    public PageAdapter(@NonNull FragmentManager fm, int size) {
        super(fm, size);
        numberOfTabs = size;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 2:
                if(bFragment == null) {
                    bFragment = new BrowserFragment();
                }
                return bFragment;
            case 1:
                if(sFragment == null) {
                    sFragment = new SearchFragment();
                }
                return sFragment;
            case 0:
                if(pFragment == null) {
                    pFragment = new Popular();
                }
                return pFragment;
            default:
                return pFragment;
        }
    }



    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
