package ba.unsa.etf.rma.booksearch.reader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import ba.unsa.etf.rma.booksearch.R;
import in.nashapp.epublibdroid.EpubReaderView;

public class ReaderFragment extends Fragment {
    private File file;
    private PDFView pdfView;
    private EpubReaderView epubView;
    private EpubReaderView.EpubReaderListener listener = new EpubReaderView.EpubReaderListener() {
        @Override
        public void OnPageChangeListener(int ChapterNumber, int PageNumber, float ProgressStart, float ProgressEnd) {

        }

        @Override
        public void OnChapterChangeListener(int ChapterNumber) {

        }

        @Override
        public void OnTextSelectionModeChangeListner(Boolean mode) {

        }

        @Override
        public void OnLinkClicked(String url) {

        }

        @Override
        public void OnBookStartReached() {

        }

        @Override
        public void OnBookEndReached() {

        }

        @Override
        public void OnSingleTap() {
            epubView.NextPage();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pdf_reader, container, false);
        pdfView = view.findViewById(R.id.pdfView);
        epubView = view.findViewById(R.id.epub_reader);
        if(getArguments() != null) {
            file =(File) getArguments().getSerializable("File");
        }
        if(file != null) {
            String name = file.getName();
            if(name.endsWith(".pdf")) {
                pdfStarter();
            }
            else if(name.endsWith(".epub")) {
                epubStarter();
            }
        }
        return view;
    }

    private void epubStarter() {
        pdfView.setVisibility(View.GONE);
        String path = file.getPath();
        epubView.OpenEpubFile(path);
        epubView.GotoPosition(0, 0);
        epubView.setEpubReaderListener(listener);
    }

    private void pdfStarter() {
        epubView.setVisibility(View.GONE);
        pdfView.fromFile(file).enableSwipe(true)
                .swipeHorizontal(false)
                .defaultPage(0)
                .spacing(0)
                .load();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }


}
