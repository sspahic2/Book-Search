package ba.unsa.etf.rma.booksearch.list;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.data.Book;

public interface IBookListView {

    void setBooks(ArrayList<Book> items);
    void recieveBook(Book book);
    void notifyDataSetChanged();
}
