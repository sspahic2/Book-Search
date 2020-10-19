package ba.unsa.etf.rma.booksearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import ba.unsa.etf.rma.booksearch.browser.BrowserFragment;


public class TabLayoutFragment extends Fragment {
    private PageAdapter pageAdapter;
    private TabLayout tabLayout;
    private TabItem itemHome;
    private TabItem itemSearch;
    private ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_layout, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);
        tabLayout = view.findViewById(R.id.tab_layout);
        itemHome = view.findViewById(R.id.tab_home);
        itemSearch = view.findViewById(R.id.tab_search);
        pageAdapter = new PageAdapter(requireActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);
        requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.nightSkyDark));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        return view;
    }

    public Fragment getSelectedItem() {
        return pageAdapter.getItem(tabLayout.getSelectedTabPosition());
    }

    public boolean onBackPressed() {
        boolean handler = false;
        if(getSelectedItem() instanceof BrowserFragment) {
            handler = ((BrowserFragment) getSelectedItem()).onBackPressed();
        }
        return handler;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}