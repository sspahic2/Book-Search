package ba.unsa.etf.rma.booksearch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Book> book = new MutableLiveData<>();

    public void sendBook(Book sentBook) {
        book.setValue(sentBook);
    }

    public LiveData<Book> getBook() {
        return book;
    }
}
