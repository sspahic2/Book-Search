package ba.unsa.etf.rma.booksearch.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.Book;
import ba.unsa.etf.rma.booksearch.R;
import ba.unsa.etf.rma.booksearch.SharedViewModel;

public class MainPageFragment extends Fragment implements IBookListView{
    private SharedViewModel sharedViewModel;
    private EditText bookSearch;
    private ListView bookList;
    private Button btnSearch;
    private Button btnFilter;
    private IBookListPresenter iBookListPresenter;
    private BookListAdapter bookListAdapter;

    public IBookListPresenter getPresenter() {
            if(iBookListPresenter == null) {
                iBookListPresenter = new BookListPresenter(this, getActivity());
            }
            return  iBookListPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.main_fragment_layout, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        bookListAdapter = new BookListAdapter(getActivity(), R.layout.list_element_layout, new ArrayList<Book>());
            bookList = fragmentView.findViewById(R.id.bookListVIew);
            bookSearch = fragmentView.findViewById(R.id.bookSearchEditText);
            btnSearch = fragmentView.findViewById(R.id.buttonSearch);
            //btnFilter = fragmentView.findViewById(R.id.buttonFilter);

            bookList.setAdapter(bookListAdapter);
            getPresenter().refrestBooks();
            bookList.setOnItemClickListener(listListener);

            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPresenter().searchBooks(bookSearch.getText().toString());
                    notifyDataSetChanged();
                }
            });
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    bookSearch.setText(sharedText);
                }
            }
        }


        return fragmentView;
    }

    private AdapterView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            System.out.println("/////Sending//////////////////// " + bookListAdapter.getItem(position).getVolumeInfo().getTitle());
            sharedViewModel.sendBook(bookListAdapter.getItem(position));
        }
    };

    @Override
    public void setBooks(ArrayList<Book> items) {
        bookListAdapter.setBooks(items);
    }

    @Override
    public void notifyDataSetChanged() {
        bookListAdapter.notifyDataSetChanged();
    }
}
