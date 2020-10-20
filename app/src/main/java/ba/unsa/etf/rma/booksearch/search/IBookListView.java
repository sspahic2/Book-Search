package ba.unsa.etf.rma.booksearch.search;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.model.Book;

public interface IBookListView {

    void setBooks(ArrayList<Book> items);
    void receiveBook(Book book);
    void notifyDataSetChanged();
}
