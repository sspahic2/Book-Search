package ba.unsa.etf.rma.booksearch.downloadSearch;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import ba.unsa.etf.rma.booksearch.R;
import ba.unsa.etf.rma.booksearch.model.Quote;
import ba.unsa.etf.rma.booksearch.quote.RandomQuote;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadFragment extends Fragment implements IDownloadView {
    private String search = "";
    private WebView webView;
    private ImageView imageView;
    private TextView textView;
    private IDownloadPresenter presenter;
    private Context context;

    public IDownloadPresenter getPresenter() {
        if(presenter == null) {
            presenter = new DownloadPresenter(this, getActivity());
        }
        return presenter;
    }

    public DownloadFragment(Context c) {
        context = c;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.download_fragment, container, false);
        webView = fragmentView.findViewById(R.id.webView);
        imageView = fragmentView.findViewById(R.id.download_loading_image);
        textView = fragmentView.findViewById(R.id.download_loading_text);

        webView.setVisibility(View.GONE);
        RandomQuote rq = RandomQuote.getInstance();
        Quote quote = rq.getQuote();
        textView.setText("\"" +quote.getQuote() + "\" \n - " + quote.getAuthor());
        Glide.with(context).load(R.drawable.load_book_flying).fitCenter().into(imageView);
        webView.setOnKeyListener(keyListener);

        if(getArguments() != null) {
            getPresenter().searchParamaters(getArguments().getStringArrayList("Authors, title and link"));
            webView.setVisibility(View.GONE);
        }

        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                DownloadManager manager = (DownloadManager) requireActivity().getSystemService(DOWNLOAD_SERVICE);
                manager.enqueue(request);
            }
            catch(Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Download stopped!", Toast.LENGTH_SHORT).show();
            }
        });

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setSearchURL(String searchDone) {
        this.search = searchDone;
        String[] split = search.split("/");
        //Checking if a book has been found
        //So thats https: as 0, "" as 1, 1lib.eu as 2, and s as 3
        if(split[3].equals("s")) {
            Toast.makeText(context, "Unable to find book. \nHere is the authors other work", Toast.LENGTH_LONG).show();
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
        //The page requests a new window pop up, when the download button is pressed
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress < 90) {
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }

                if(newProgress >= 90) {
                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(getActivity());
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setSupportZoom(true);
                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.getSettings().setSupportMultipleWindows(true);
                view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });

                return true;
            }
        });
        webView.loadUrl(search);
    }

    //When handling the back button while in webView
    private View.OnKeyListener keyListener =(v, keyCode, event) -> {

        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            WebView view = (WebView) v;

            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if(view.canGoBack()) {
                        view.goBack();
                        return true;
                    }
                    break;
            }
        }
        return false;
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
