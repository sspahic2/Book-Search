package ba.unsa.etf.rma.booksearch.list;

import android.content.Context;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.data.Book;

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
        new BookListInteractor((BookListInteractor.OnBookSearchDone) this).execute(query, author);
    }

    @Override
    public void onDone(ArrayList<Book> items) {
        view.setBooks(items);
        view.notifyDataSetChanged();
    }
}
