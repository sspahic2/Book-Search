package ba.unsa.etf.rma.booksearch.search;

public interface IBookListPresenter {
    void refrestBooks();
    void searchBooks(String query, String author);
}
