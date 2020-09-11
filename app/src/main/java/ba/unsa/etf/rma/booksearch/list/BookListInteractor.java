package ba.unsa.etf.rma.booksearch.list;

import android.os.AsyncTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.Book;


public class BookListInteractor extends AsyncTask<String, Integer, Void>{

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

        try {
            query = URLEncoder.encode(strings[0], "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url1 = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=40";
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String rezultat = convertStreamToString(in);
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
                //JSONArray industryIdentifiers = vi.optJSONArray("industryIdentifiers");
//                if(industryIdentifiers != null) {
//                    for(int j = 0; j < industryIdentifiers.length(); j++) {
//                        JSONObject listObject = industryIdentifiers.optJSONObject(j);
//                        if(listObject.optString("type").equals("ISBN_13")) {
//                            String isbn = listObject.optString("identifier");
//                            String urlForCover = "http://covers.openlibrary.org/b/isbn/" + isbn + "-M.jpg";
//                            volumeInfo.setImageLinks(urlForCover);
//                        }
//                    }
//                }
               // else {
                    JSONObject thumbnailUrlObject = vi.optJSONObject("imageLinks");
                    if (thumbnailUrlObject != null && thumbnailUrlObject.has("smallThumbnail")) {
                        volumeInfo.setImageLinks(thumbnailUrlObject.getString("smallThumbnail"));
                    } else {
                        //Image not found link
                        volumeInfo.setImageLinks("https://softsmart.co.za/wp-content/uploads/2018/06/image-not-found-1038x576.jpg");
                    }
               // }
                volumeInfo.setDescription(vi.optString("description"));
                books.add(new Book(id, volumeInfo));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertStreamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while(((line = reader.readLine()) != null)) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
