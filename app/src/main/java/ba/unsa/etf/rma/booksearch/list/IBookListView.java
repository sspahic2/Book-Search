package ba.unsa.etf.rma.booksearch.list;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.Book;

public interface IBookListView {

    void setBooks(ArrayList<Book> items);
    void notifyDataSetChanged();
}
