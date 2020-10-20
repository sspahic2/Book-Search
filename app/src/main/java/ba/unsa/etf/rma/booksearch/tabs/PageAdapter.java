package ba.unsa.etf.rma.booksearch.tabs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ba.unsa.etf.rma.booksearch.browser.BrowserFragment;
import ba.unsa.etf.rma.booksearch.popular.Popular;
import ba.unsa.etf.rma.booksearch.search.SearchFragment;

public class PageAdapter extends FragmentPagerAdapter {
    private int numberOfTabs;
    private BrowserFragment bFragment = null;
    private SearchFragment sFragment = null;
    private Popular pFragment = null;
    private Context context;
    public PageAdapter(@NonNull FragmentManager fm, int size, Context context) {
        super(fm, size);
        numberOfTabs = size;
        this.context = context;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 2:
                if(bFragment == null) {
                    bFragment = new BrowserFragment(context);
                }
                return bFragment;
            case 1:
                if(sFragment == null) {
                    sFragment = new SearchFragment(context);
                }
                return sFragment;
            case 0:
                if(pFragment == null) {
                    pFragment = new Popular(context);
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
