package ba.unsa.etf.rma.booksearch.list;

public interface IBookListPresenter {
    void refrestBooks();
    void searchBooks(String query, String author);
}
