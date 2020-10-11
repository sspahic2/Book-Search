package ba.unsa.etf.rma.booksearch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ba.unsa.etf.rma.booksearch.list.SearchFragment;
import ba.unsa.etf.rma.booksearch.popular.Popular;

public class PageAdapter extends FragmentPagerAdapter {
    private int numberOfTabs;
    public PageAdapter(@NonNull FragmentManager fm, int size) {
        super(fm, size);
        numberOfTabs = size;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new Popular();
        }
        else if(position == 1) {
            return new SearchFragment();
        }
        return new Popular();
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
