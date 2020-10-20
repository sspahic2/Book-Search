package ba.unsa.etf.rma.booksearch;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.util.List;
import ba.unsa.etf.rma.booksearch.browser.BrowserFragment;
import ba.unsa.etf.rma.booksearch.details.DetailsFragment;
import ba.unsa.etf.rma.booksearch.downloadSearch.DownloadFragment;
import ba.unsa.etf.rma.booksearch.viewModel.SharedViewModel;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel viewModel;
    private Toolbar toolbar;
    private int id = 0;
    private TabLayoutFragment tabLayoutFragment;

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            tabLayoutFragment = new TabLayoutFragment(MainActivity.this);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tabLayoutFragment, "Tabs").commit();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TedPermission.with(MainActivity.this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("Sorry, you rejected vital permissions.\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

        viewModel.getBook().observe(this, book -> {
            if(book != null) {
                //This is the correct way to send model from fragment to activity then to next fragment
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

                DetailsFragment detailsFragment = new DetailsFragment(MainActivity.this);
                detailsFragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, detailsFragment,"Details" + id++).addToBackStack(null).commit();
            }
        });

        viewModel.getSearchParamaters().observe(this, strings -> {
            if(!strings.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("Authors, title and link", strings);
                DownloadFragment downloadFragment = new DownloadFragment(MainActivity.this);
                downloadFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, downloadFragment, "Download").addToBackStack(null).commit();
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