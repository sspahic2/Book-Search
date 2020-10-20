package ba.unsa.etf.rma.booksearch.search;

import android.content.Context;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.model.Book;

public class BookListPresenter implements IBookListPresenter, BookListInteractor.OnBookSearchDone {
    private IBookListView view;
    private Context context;


    public BookListPresenter(IBookListView view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void refrestBooks() {

    }

    @Override
    public void searchBooks(String query, String author) {
        new BookListInteractor(this).execute(query, author);
    }

    @Override
    public void onDone(ArrayList<Book> items) {
        view.setBooks(items);
        view.notifyDataSetChanged();
    }
}
