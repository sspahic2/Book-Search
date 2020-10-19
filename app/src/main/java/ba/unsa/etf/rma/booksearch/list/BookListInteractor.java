package ba.unsa.etf.rma.booksearch.list;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.StreamConverter;
import ba.unsa.etf.rma.booksearch.data.Book;
import ba.unsa.etf.rma.booksearch.data.VolumeInfo;


public class BookListInteractor extends AsyncTask<String, String,Void>{

    private String apiKey = "";
    private ArrayList<Book> books;
    private OnBookSearchDone caller;

    public interface OnBookSearchDone {
        public void onDone(ArrayList<Book> items);
    }


    public BookListInteractor(OnBookSearchDone caller) {
        this.caller = caller;
        books = new ArrayList<>();
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        caller.onDone(books);
    }

    @Override
    protected Void doInBackground(String... strings) {
        String query = null;
        String authorForSimilarBooks = null;

        try {
            //Practically useless, because for detailed search google uses + as a separator
            query = URLEncoder.encode(strings[0], "utf-8");
            authorForSimilarBooks = URLEncoder.encode(strings[1], "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            query = "";
            authorForSimilarBooks = "";
        }
        String url1 = null;
        if(!authorForSimilarBooks.isEmpty()) {
            url1 = "https://www.googleapis.com/books/v1/volumes?q=" + strings[0]
                    + "+inauthor:" + strings[1] +"&maxResults=40";
        }
        else {
            url1 = "https://www.googleapis.com/books/v1/volumes?q=" + strings[0] + "&maxResults=40";
        }
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            StreamConverter sc = new StreamConverter();
            String rezultat = sc.convertStreamToString(in);
            JSONObject jo = new JSONObject(rezultat);
            JSONArray results = jo.getJSONArray("items");

            for(int i = 0; i < results.length(); i++) {

                JSONObject book = results.getJSONObject(i);
                String id = book.getString("id");
                JSONObject vi = book.getJSONObject("volumeInfo");
                VolumeInfo volumeInfo = new VolumeInfo();
                volumeInfo.setTitle(vi.optString("title"));

                volumeInfo.setPublisher(vi.optString("publisher"));
                JSONArray authors = vi.optJSONArray("authors");
                volumeInfo.setAuthors(new ArrayList<>());
                if(authors != null) {
                    for (int j = 0; j < authors.length(); j++) {
                        volumeInfo.addAuthors((String) authors.get(j));
                    }
                }

                JSONArray categories = vi.optJSONArray("categories");
                volumeInfo.setCategories(new ArrayList<>());
                if(categories != null) {

                    for (int j = 0; j < categories.length(); j++) {
                        volumeInfo.addCategories(categories.getString(j));
                    }
                }
                //What if I want better image links?
                //Implementing Open Library Covers API
                //It's too slow for list showing
                //But I will repurpouse it for viewing later down the line
                JSONArray industryIdentifiers = vi.optJSONArray("industryIdentifiers");
                if(industryIdentifiers != null) {
                    for(int j = 0; j < industryIdentifiers.length(); j++) {
                        JSONObject listObject = industryIdentifiers.optJSONObject(j);
                        if(listObject.optString("type").equals("ISBN_13")) {
                              volumeInfo.setIsbn13(listObject.getString("identifier"));
                              break;
                        }
                    }
                }

                    JSONObject thumbnailUrlObject = vi.optJSONObject("imageLinks");
                    if (thumbnailUrlObject != null && thumbnailUrlObject.has("smallThumbnail")) {
                        volumeInfo.setImageLink(thumbnailUrlObject.getString("smallThumbnail"));
                    } else {
                        //Image not found link
                        volumeInfo.setImageLink("https://d1uyjdd2vmpgct.cloudfront.net/public/defaults/default-book-cover.png");
                    }
                volumeInfo.setWebLink(vi.optString("infoLink"));

                volumeInfo.setDescription(vi.optString("description"));
                //Unnecessary books are being added that don't have the exact name of the author
                if(!strings[1].isEmpty()) {
                    for(String s : volumeInfo.getAuthors()) {
                        if(s.toLowerCase().trim().matches(strings[1].toLowerCase().trim())) {
                            books.add(new Book(id, volumeInfo));
                            break;
                        }
                    }
                }
                else {
                    books.add(new Book(id, volumeInfo));
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
