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

import ba.unsa.etf.rma.booksearch.StreamConverter;
import ba.unsa.etf.rma.booksearch.model.Book;
import ba.unsa.etf.rma.booksearch.model.VolumeInfo;

public class PopularInteractor extends AsyncTask<String, String, Void> {
    private PopularsFound caller;
    private ArrayList<Book> popularBooks = new ArrayList<>();

    public PopularInteractor(PopularsFound popularsFound) {
        caller = popularsFound;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        caller.onDone(popularBooks);
    }

    public interface PopularsFound {
        void onDone(ArrayList<Book> items);
    }
    @Override
    protected Void doInBackground(String... strings) {
        String url1 = "https://b-ok.herokuapp.com/";
        URL url;
        try {
            url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() != 200) {
                popularBooks.add(new Book("NO RESPONSE", null));
                return null;
            }
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            StreamConverter sc = new StreamConverter();
            String rezultat = sc.convertStreamToString(in);
            JSONObject jo = new JSONObject(rezultat);
            JSONArray data = jo.getJSONArray("model");
            for (int i = 0; i < data.length(); i++) {
                VolumeInfo volumeInfo = new VolumeInfo();
                JSONObject b = data.optJSONObject(i);
                volumeInfo.setTitle(b.optString("title"));
                volumeInfo.setImageLink(b.optString("img"));
                //Because I don't have a field for b-ok link, I'll put it in id
                String id = b.optString("href");
                Book book = new Book(id, volumeInfo);
                popularBooks.add(book);
                }
            } catch(IOException | JSONException e){
                e.printStackTrace();
            }

        return null;
    }

}
