package ba.unsa.etf.rma.booksearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.google.gson.Gson;

import ba.unsa.etf.rma.booksearch.list.MainPageFragment;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        MainPageFragment mainPageFragment = new MainPageFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainPageFragment).addToBackStack(null).commit();

        viewModel.getBook().observe(this, new Observer<Book>() {
            @Override
            public void onChanged(Book book) {
                if(book != null) {
                    DetailsFragment detailsFragment = new DetailsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detailsFragment).addToBackStack(null).commit();
                }
            }
        });
    }
}