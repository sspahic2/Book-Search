package ba.unsa.etf.rma.booksearch;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

import ba.unsa.etf.rma.booksearch.browser.BrowserFragment;
import ba.unsa.etf.rma.booksearch.connectivity.ConnectionStatus;
import ba.unsa.etf.rma.booksearch.details.DetailsFragment;
import ba.unsa.etf.rma.booksearch.downloadSearch.DownloadFragment;
import ba.unsa.etf.rma.booksearch.tabs.TabLayoutFragment;
import ba.unsa.etf.rma.booksearch.viewModel.SharedViewModel;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel viewModel;
    private Toolbar toolbar;
    private int id = 0;
    private TabLayoutFragment tabLayoutFragment;
    private ConnectionStatus status;
    private Button refreshButton;
    private final String DENIAL_MESSAGE = "Sorry, you rejected vital permissions.\n\nPlease turn on permissions at [Setting] > [Apps] > [Book Search]";

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if(status.isOnline()) {
                loadTabFragment();
            }
            else {
                refreshButton.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    //When everything is ok we can load the tab layout
    private void loadTabFragment() {
        refreshButton.setVisibility(View.GONE);
        tabLayoutFragment = new TabLayoutFragment(MainActivity.this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tabLayoutFragment, "Tabs").commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        toolbar = findViewById(R.id.toolbar);
        status = ConnectionStatus.getInstance(MainActivity.this);
        setSupportActionBar(toolbar);
        refreshButton = findViewById(R.id.refresh_button_network);
        refreshButton.setVisibility(View.GONE);
        refreshButton.setOnClickListener(refreshListener);

        TedPermission.with(MainActivity.this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage(DENIAL_MESSAGE)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE)
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
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .add(R.id.fragment_container, detailsFragment,"Details" + id++)
                        .addToBackStack(null).commit();
            }
        });

        viewModel.getSearchParamaters().observe(this, strings -> {
            if(!strings.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("Authors, title and link", strings);
                DownloadFragment downloadFragment = new DownloadFragment(MainActivity.this);
                downloadFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .add(R.id.fragment_container, downloadFragment, "Download")
                        .addToBackStack(null).commit();
            }
        });
    }

    private View.OnClickListener refreshListener =v -> check();

    //Checking if a connection is available
    private void check() {
        refreshButton.setVisibility(View.VISIBLE);
        if(status.isOnline()) {
            loadTabFragment();
        }
        else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
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