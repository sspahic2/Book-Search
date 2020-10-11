package ba.unsa.etf.rma.booksearch;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.data.Book;
import ba.unsa.etf.rma.booksearch.details.DetailsFragment;
import ba.unsa.etf.rma.booksearch.downloadSearch.DownloadFragment;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel viewModel;
    private Toolbar toolbar;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        TabLayoutFragment tabLayoutFragment = new TabLayoutFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tabLayoutFragment, "Tabs").commit();
        viewModel.getBook().observe(this, new Observer<Book>() {
            @Override
            public void onChanged(Book book) {
                if(book != null) {
                    //This is the correct way to send data from fragment to activity then to next fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("Title", book.getVolumeInfo().getTitle());
                    bundle.putString("Description", book.getVolumeInfo().getDescription());
                    bundle.putString("Link", book.getVolumeInfo().getImageLink());
                    bundle.putString("ISBN13", book.getVolumeInfo().getIsbn13());
                    bundle.putStringArrayList("Authors", book.getVolumeInfo().getAuthors());
                    bundle.putStringArrayList("Categories", book.getVolumeInfo().getCategories());
                    bundle.putString("Publisher", book.getVolumeInfo().getPublisher());
                    bundle.putString("ID", book.getId());
                    bundle.putString("WebLink", book.getVolumeInfo().getWebLink());

                    DetailsFragment detailsFragment = new DetailsFragment();
                    detailsFragment.setArguments(bundle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_container, detailsFragment,"Details" + id++).addToBackStack(null).commit();
                }
            }
        });

        viewModel.getSearchParamaters().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                if(!strings.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("Authors, title and link", strings);
                    DownloadFragment downloadFragment = new DownloadFragment();
                    downloadFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, downloadFragment, "Download").addToBackStack(null).commit();
                }
            }
        });

    }
}