package ba.unsa.etf.rma.booksearch;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.browser.BrowserFragment;
import ba.unsa.etf.rma.booksearch.data.Book;
import ba.unsa.etf.rma.booksearch.details.DetailsFragment;
import ba.unsa.etf.rma.booksearch.downloadSearch.DownloadFragment;
import ba.unsa.etf.rma.booksearch.reader.ReaderFragment;
import ba.unsa.etf.rma.booksearch.viewModel.FileViewModel;
import ba.unsa.etf.rma.booksearch.viewModel.SharedViewModel;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel viewModel;
    private FileViewModel fileViewModel;
    private Toolbar toolbar;
    private int id = 0;
    private TabLayoutFragment tabLayoutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        fileViewModel = new ViewModelProvider(this).get(FileViewModel.class);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayoutFragment = new TabLayoutFragment();
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

        fileViewModel.getFile().observe(this, new Observer<File>() {
            @Override
            public void onChanged(File file) {
                if(file != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("File", file);
                    ReaderFragment readerFragment = new ReaderFragment();
                    readerFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, readerFragment, "Reader").addToBackStack(null).commit();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        boolean handler = false;
        //Handling when the user is in one of the fragments of tab layout
        if(tabLayoutFragment.getSelectedItem() instanceof BrowserFragment) {
            handler = tabLayoutFragment.onBackPressed();
        }
        if(!handler) {
            super.onBackPressed();
        }
    }


}