package ba.unsa.etf.rma.booksearch.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.model.Book;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Book> book = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> searchParamaters = new MutableLiveData<>();

    public void sendBook(Book sentBook) {
        book.setValue(sentBook);
    }

    public LiveData<Book> getBook() {
        return book;
    }


    public void sendSearch(String title, ArrayList<String> authors, String webLink) {
        //Had to add the to the back of the list
        authors.add(title);
        authors.add(webLink);
        searchParamaters.setValue(authors);
    }

    public LiveData<ArrayList<String>> getSearchParamaters() {
        return searchParamaters;
    }
}
