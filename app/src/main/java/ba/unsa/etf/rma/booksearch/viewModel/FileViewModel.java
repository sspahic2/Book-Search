package ba.unsa.etf.rma.booksearch.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;

public class FileViewModel extends ViewModel {
    private MutableLiveData<File> file = new MutableLiveData<>();

    public LiveData<File> getFile() {
        return file;
    }

    public void sendFile(File file) {
        this.file.setValue(file);
    }
}
