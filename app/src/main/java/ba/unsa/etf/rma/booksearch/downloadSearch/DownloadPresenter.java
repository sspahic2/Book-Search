package ba.unsa.etf.rma.booksearch.downloadSearch;

import android.content.Context;
import java.util.ArrayList;



public class DownloadPresenter implements IDownloadPresenter, DownloadInteractor.OnParamatersFound {
    public IDownloadView view;
    public Context context;

    public DownloadPresenter(IDownloadView view, Context context) {
        this.view = view;
        this.context = context;
    }


    @Override
    public void done(String searchQuery) {
        view.setSearchURL(searchQuery);
    }


    @Override
    public void searchParamaters(ArrayList<String> search) {
        new DownloadInteractor(this).execute(search);
    }
}
