package ba.unsa.etf.rma.booksearch.popular;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.R;
import ba.unsa.etf.rma.booksearch.SharedViewModel;
import ba.unsa.etf.rma.booksearch.data.Book;
import ba.unsa.etf.rma.booksearch.data.Quote;
import ba.unsa.etf.rma.booksearch.list.IBookListView;
import ba.unsa.etf.rma.booksearch.quote.RandomQuote;

public class Popular extends Fragment implements IBookListView {
    private static String apiKey = "b3d8e25f18mshbe5952006717b25p13ad9bjsnbb47af6b1666";
    private static String apiHost = "andruxnet-random-famous-quotes.p.rapidapi.com";
    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private ImageView loadingImage;
    private TextView loadingText;
    private MyRecycleAdapter adapter;
    private IPopularPresenter popularPresenter;
    private RandomQuote randomQuote;

    private IPopularPresenter getPopularPresenter() {
        if(popularPresenter == null) {
            popularPresenter = new PopularPresenter(getContext(), this);
        }
        return  popularPresenter;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.popular_books, container, false);
            recyclerView = fragmentView.findViewById(R.id.recycler_view);
            loadingImage = fragmentView.findViewById(R.id.loading_image);
            loadingText = fragmentView.findViewById(R.id.loading_text);
            sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        //Show animation and quote while loading
        randomQuote = RandomQuote.getInstance();
        Quote quote = randomQuote.getQuote();
        loadingText.setText("\"" +quote.getQuote() + "\" \n - " + quote.getAuthor());

            getPopularPresenter().searchPopularBooks();

            loadingText.setVisibility(View.VISIBLE);
            loadingImage.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(R.drawable.book_page_flip).fitCenter().into(loadingImage);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
        return fragmentView;
    }

    @Override
    public void setBooks(ArrayList<Book> items) {
        if(items.size() > 0 && items.get(0).getId().trim().toLowerCase().equals("no response")) {
            //TODO Napravi pop up da ponovo pokusa
        }
        else {
            adapter = new MyRecycleAdapter(items);

            //Hide animation and quote after list is loaded
            loadingImage.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);

            //It wont be clicked until its loaded anyways
            adapter.setOnItemClickListener(new MyRecycleAdapter.ClickListener<Book>() {
                @Override
                public void onItemClick(Book data) {
                    getPopularPresenter().searchFromDifferentApi(data.getId());
                    recyclerView.setVisibility(View.GONE);
                    loadingImage.setVisibility(View.VISIBLE);
                    Quote quote = randomQuote.getQuote();
                    loadingText.setText("\"" +quote.getQuote() + "\" \n - " + quote.getAuthor());
                    loadingText.setVisibility(View.VISIBLE);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void recieveBook(Book book) {
        sharedViewModel.sendBook(book);
        loadingImage.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }
}
