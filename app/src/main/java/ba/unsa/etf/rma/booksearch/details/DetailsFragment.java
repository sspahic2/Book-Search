package ba.unsa.etf.rma.booksearch.details;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.R;
import ba.unsa.etf.rma.booksearch.data.Book;
import ba.unsa.etf.rma.booksearch.data.VolumeInfo;
import ba.unsa.etf.rma.booksearch.list.BookListPresenter;
import ba.unsa.etf.rma.booksearch.list.IBookListPresenter;
import ba.unsa.etf.rma.booksearch.list.IBookListView;
import ba.unsa.etf.rma.booksearch.popular.MyRecycleAdapter;
import ba.unsa.etf.rma.booksearch.viewModel.SharedViewModel;

public class DetailsFragment extends Fragment implements IBookListView {
    private SharedViewModel sharedViewModel;
    private TextView description;
    private Button download;
    private TextView authors;
    private TextView categories;
    private ImageView bookCover;
    private RecyclerView similarBooks;
    private TextView title;
    private Book book;
    private MyRecycleAdapter adapter;
    private IBookListPresenter presenter;

    private IBookListPresenter getPresenter() {
        if(presenter == null) {
            presenter = new BookListPresenter(this, getActivity());
        }
        return presenter;
    }


    private View.OnClickListener dowloadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Sending search paramaters to help find the book on b-ok api
            sharedViewModel.sendSearch(book.getVolumeInfo().getTitle(), book.getVolumeInfo().getAuthors(), book.getVolumeInfo().getWebLink());
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.book_detail_fragment, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        description = fragmentView.findViewById(R.id.description);
        authors = fragmentView.findViewById(R.id.authors);
        categories = fragmentView.findViewById(R.id.categories);
        bookCover = fragmentView.findViewById(R.id.bookCover);
        title = fragmentView.findViewById(R.id.titleText);
        download = fragmentView.findViewById(R.id.btnDownload);
        similarBooks = fragmentView.findViewById(R.id.similarList);
        download.setOnClickListener(dowloadListener);


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        similarBooks.setLayoutManager(layoutManager);
        //The recyclerview's content can now be seen withouth scrolling
        similarBooks.setNestedScrollingEnabled(false);

        if(getArguments() != null) {
            VolumeInfo volumeInfo = new VolumeInfo();
            volumeInfo.setTitle(getArguments().getString("Title"));
            volumeInfo.setIsbn13(getArguments().getString("ISBN13"));
            volumeInfo.setAuthors(getArguments().getStringArrayList("Authors"));
            volumeInfo.setCategories(getArguments().getStringArrayList("Categories"));
            volumeInfo.setPublisher(getArguments().getString("Publisher"));
            volumeInfo.setDescription(getArguments().getString("Description"));
            volumeInfo.setImageLink(getArguments().getString("Link"));
            volumeInfo.setWebLink(getArguments().getString("WebLink"));
            book = new Book(getArguments().getString("ID"), volumeInfo);
            initializeFields();
        }
        return fragmentView;
    }

    private void initializeFields() {
        if(book.getVolumeInfo().getDescription().equals("")) {
            description.setText("No description available.");
        }
        else {
            description.setText(book.getVolumeInfo().getDescription());
        }
        title.setText(book.getVolumeInfo().getTitle());

        //Cant use StringJoiner beacuse the api is min 23 and it requires min 24
        authors.setText("Author/s: ");
        authors.append(TextUtils.join(", ", book.getVolumeInfo().getAuthors()));
        categories.setText("Category/ies: \n");
        categories.append(TextUtils.join(", ", book.getVolumeInfo().getCategories()));

        String coverImage = "";
        //If isbn is "" then there is no reason to search openlibrary
        if(book.getVolumeInfo().getIsbn13().isEmpty()) {
            coverImage = book.getVolumeInfo().getImageLink();
        }
         else {
             coverImage = "http://covers.openlibrary.org/b/isbn/" + book.getVolumeInfo().getIsbn13() +"-M.jpg?default=false";
        }

        Glide.with(getContext()).load(coverImage)
                .placeholder(R.drawable.placeholder_book)
                .error(Glide.with(getContext()).load(book.getVolumeInfo().getImageLink()))
                .fallback(R.drawable.placeholder_book).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                bookCover.setImageDrawable(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    //Have to start searching for similar books after the current book is loaded
    @Override
    public void onResume() {
        super.onResume();
        if(book != null) {
            //Also send the first author
            String author = "";
            if(!book.getVolumeInfo().getAuthors().isEmpty()) {
                author = book.getVolumeInfo().getAuthors().get(0);
            }
            getPresenter().searchBooks(book.getVolumeInfo().getTitle(), author);
        }
    }

    @Override
    public void setBooks(ArrayList<Book> items) {
        Book removing = null;
        //So that the book a user is currently viewing isn't show in similar books
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getVolumeInfo().getTitle().trim().equals(book.getVolumeInfo().getTitle())) {
                removing = items.get(i);
                break;
            }
        }
        if(removing != null) {
            items.remove(removing);
        }
        adapter = new MyRecycleAdapter(items);
        adapter.setOnItemClickListener(new MyRecycleAdapter.ClickListener<Book>() {
            @Override
            public void onItemClick(Book data) {
                sharedViewModel.sendBook(data);
            }
        });
        similarBooks.setAdapter(adapter);
    }

    @Override
    public void recieveBook(Book book) {
        //Do nothing
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.share_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Ok I have a share button now
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String link = "";
                //Test if its from google or b-ok
                if(book.getVolumeInfo().getWebLink().contains("books.google.com")) {
                    link = book.getVolumeInfo().getWebLink();
                }
                else {
                    link = "https://1lib.eu" + book.getVolumeInfo().getWebLink();
                }
                intent.putExtra(Intent.EXTRA_TEXT, link);
                startActivity(Intent.createChooser(intent, "Share with"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
