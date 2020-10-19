package ba.unsa.etf.rma.booksearch.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.R;
import ba.unsa.etf.rma.booksearch.data.Book;
import ba.unsa.etf.rma.booksearch.popular.MyRecycleAdapter;
import ba.unsa.etf.rma.booksearch.viewModel.SharedViewModel;

public class SearchFragment extends Fragment implements IBookListView{
    private SharedViewModel sharedViewModel;
    private RecyclerView bookList;
    private SearchView searchView;
    private IBookListPresenter iBookListPresenter;
    private MyRecycleAdapter bookListAdapter;

    public IBookListPresenter getPresenter() {
            if(iBookListPresenter == null) {
                iBookListPresenter = new BookListPresenter(this, getActivity());
            }
            return  iBookListPresenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.main_fragment_layout, container, false);
        setHasOptionsMenu(true);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            bookList = fragmentView.findViewById(R.id.search_recycler_view);
            bookList.setAdapter(bookListAdapter);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            bookList.setLayoutManager(layoutManager);

        return fragmentView;
    }
    @Override
    public void setBooks(ArrayList<Book> items) {
        bookListAdapter = new MyRecycleAdapter(items);

        bookListAdapter.setOnItemClickListener(new MyRecycleAdapter.ClickListener<Book>() {
            @Override
            public void onItemClick(Book data) {
                sharedViewModel.sendBook(data);
            }
        });
        bookList.setAdapter(bookListAdapter);
    }

    @Override
    public void recieveBook(Book book) {
        //Do nothing
    }

    @Override
    public void notifyDataSetChanged() {
        bookListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        EditText text = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        text.setTextColor(getResources().getColor(R.color.lightGreen, requireContext().getTheme()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getPresenter().searchBooks(query, "");
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

}
