package ba.unsa.etf.rma.booksearch.popular;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
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
import ba.unsa.etf.rma.booksearch.model.Book;
import ba.unsa.etf.rma.booksearch.model.Quote;
import ba.unsa.etf.rma.booksearch.quote.RandomQuote;
import ba.unsa.etf.rma.booksearch.search.IBookListView;
import ba.unsa.etf.rma.booksearch.viewModel.SharedViewModel;

public class Popular extends Fragment implements IBookListView {
    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private ImageView loadingImage;
    private TextView loadingText;
    private MyRecycleAdapter adapter;
    private IPopularPresenter popularPresenter;
    private RandomQuote randomQuote;
    private Context context;
    private Button refresh;
    private View.OnClickListener refreshListener = v -> {
        getPopularPresenter().searchPopularBooks();
        refresh.setVisibility(View.GONE);
    };

    public Popular(Context context) {
        this.context = context;
    }

    private IPopularPresenter getPopularPresenter() {
        if(popularPresenter == null) {
            popularPresenter = new PopularPresenter(context, this);
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
        refresh = fragmentView.findViewById(R.id.refresh_button);
        refresh.setOnClickListener(refreshListener);
        refresh.setVisibility(View.GONE);
        //Show animation and quote while loading
        randomQuote = RandomQuote.getInstance();
        Quote quote = randomQuote.getQuote();
        loadingText.setText("\"" +quote.getQuote() + "\" \n - " + quote.getAuthor());

        getPopularPresenter().searchPopularBooks();
        loadLoading();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        return fragmentView;
    }

    private void loadLoading() {
        loadingText.setVisibility(View.VISIBLE);
        Glide.with(context).load(R.drawable.book_page_flip).into(loadingImage);
        loadingImage.setVisibility(View.VISIBLE);
    }

    private void runLayoutAnimation() {
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_fall_down_animation);
        recyclerView.setLayoutAnimation(controller);
        if(adapter != null) {
            notifyDataSetChanged();
        }
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void setBooks(ArrayList<Book> items) {
        adapter = new MyRecycleAdapter(items);
        if(items.size() > 0 && items.get(0).getId().trim().toLowerCase().equals("no response")) {
            //TODO Napravi pop up da ponovo pokusa
            refresh.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            loadingText.setText("Oops something went wrong. \n Please try to refresh.");
        }
        else {
            //Hide animation and quote after list is loaded
            loadingImage.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
            refresh.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            runLayoutAnimation();

            //It wont be clicked until its loaded anyways
            adapter.setOnItemClickListener(data -> {

                getPopularPresenter().searchFromDifferentApi(data.getId());
                recyclerView.setVisibility(View.GONE);
                loadingImage.setVisibility(View.VISIBLE);
                Quote quote = randomQuote.getQuote();
                loadingText.setText("\"" +quote.getQuote() + "\" \n - " + quote.getAuthor());
                loadingText.setVisibility(View.VISIBLE);

            });
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void receiveBook(Book book) {
        if(book == null) {
            refresh.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            loadLoading();
            loadingText.setText("Oops something went wrong. \n Please try to refresh.");
        }
        else {
            sharedViewModel.sendBook(book);
            loadingImage.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

}
