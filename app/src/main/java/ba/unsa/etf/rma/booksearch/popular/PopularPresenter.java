package ba.unsa.etf.rma.booksearch.popular;

import android.content.Context;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.model.Book;
import ba.unsa.etf.rma.booksearch.search.IBookListView;

public class PopularPresenter implements PopularInteractor.PopularsFound, IPopularPresenter, BookSearchInteractor.BookFound{

    private Context context;
    private IBookListView view;

    public PopularPresenter(Context context, IBookListView view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void onDone(ArrayList<Book> items) {
        view.setBooks(items);
        view.notifyDataSetChanged();
    }

    public void searchPopularBooks() {
        new PopularInteractor(this).execute();
    }

    @Override
    public void searchFromDifferentApi(String id) {
        new BookSearchInteractor(this).execute(id);
    }

    @Override
    public void onFound(Book book) {
        view.receiveBook(book);
    }
}
