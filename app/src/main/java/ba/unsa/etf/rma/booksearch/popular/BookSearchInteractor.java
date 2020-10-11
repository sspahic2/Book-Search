package ba.unsa.etf.rma.booksearch.popular;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import ba.unsa.etf.rma.booksearch.StreamConverter;
import ba.unsa.etf.rma.booksearch.data.Book;
import ba.unsa.etf.rma.booksearch.data.VolumeInfo;

public class BookSearchInteractor extends AsyncTask<String, String, Void> {
    private Book book;
    private BookFound caller;


    public interface BookFound {
        public void onFound(Book book);
    }

    public BookSearchInteractor(BookFound bookFound) {
        caller = bookFound;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        caller.onFound(book);
    }

    @Override
    protected Void doInBackground(String... strings) {
        String bookID = strings[0];
        String urlTemp = "https://b-ok.herokuapp.com/book/single" + bookID;
        URL url = null;
        try {
            url = new URL(urlTemp);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            StreamConverter sc = new StreamConverter();
            String rezultat = sc.convertStreamToString(in);
            JSONObject jsonObject = new JSONObject(rezultat);

            JSONObject data = jsonObject.getJSONObject("data");
            VolumeInfo volumeInfo = new VolumeInfo();

            volumeInfo.setTitle(data.optString("name"));
            volumeInfo.setDescription(data.optString("desc"));
            volumeInfo.setWebLink(data.optString("link"));

            String[] split = data.optString("category").split("\\\\");
            ArrayList<String> categories = new ArrayList<>(Arrays.asList(split));
            volumeInfo.setCategories(categories);
            volumeInfo.setIsbn13("");
            volumeInfo.setPublisher(data.optString("publisher"));

            //Getting authors from different url
            String getAuthors = "https://b-ok.herokuapp.com/book/search?q=" + data.optString("name") + "&page=1";
            url = new URL(getAuthors);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            rezultat = sc.convertStreamToString(in);
            jsonObject = new JSONObject(rezultat);
            JSONArray dataSet = jsonObject.getJSONArray("data");

            String s = bookID.replace("?dsource=mostpopular", "");
            for(int i = 0; i < dataSet.length(); i++) {
                JSONObject foundBook =(JSONObject) dataSet.get(i);
                if(s.trim().equals(foundBook.optString("link"))) {
                    ArrayList<String> authors = new ArrayList<>();
                    authors.add(foundBook.optString("authors"));
                    volumeInfo.setAuthors(authors);
                    volumeInfo.setImageLink(foundBook.optString("img").replace("covers100", "covers299"));
                    break;
                }
            }
            book = new Book(bookID, volumeInfo);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
