package ba.unsa.etf.rma.booksearch.downloadSearch;

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


public class DownloadInteractor extends AsyncTask<ArrayList<String>, Integer, Void>{
    public String search;
    public OnParamatersFound caller;

    public DownloadInteractor(OnParamatersFound onParamatersFound) {
        caller = onParamatersFound;
    }

    public interface OnParamatersFound {
        public void done(String searchQuery);
    }


    @Override
    protected Void doInBackground(ArrayList<String>... arrayLists) {
        ArrayList<String> paramaters = arrayLists[0];
        //Web link is at the last place
        if(!paramaters.get(paramaters.size() - 1).equals("")) {
            this.search = "https://1lib.eu" + paramaters.get(paramaters.size() - 1);
            return null;
        }
        //Title is before the last place
        String title = paramaters.get(paramaters.size() - 2);
        ArrayList<String> authors = new ArrayList<>();
        for(int i = 0; i < paramaters.size() - 2; i++) {
            authors.add(paramaters.get(i));
        }
        //Finding books that match the paramaters
        //Need to find all the books that match the title
        String url1 = "https://b-ok.herokuapp.com/book/search?q=" + title;
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //200 means that everything is OK
            if (urlConnection.getResponseCode() == 200) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                StreamConverter sc = new StreamConverter();
                String query = sc.convertStreamToString(in);
                JSONObject jo = new JSONObject(query);
                JSONArray results = jo.getJSONArray("data");
                //A list of all the books the api is getting back
                for (int i = 0; i < results.length(); i++) {
                    JSONObject book = results.getJSONObject(i);
                    boolean check = false;
                    if (book.getString("title").toLowerCase().contains(title.toLowerCase().trim())) {
                        //Checking if the authors are the same
                        for (int j = 0; j < authors.size(); j++) {
                            if (authors.get(j).toLowerCase().trim().contains(book.getString("authors").toLowerCase().trim())) {
                                check = true;
                                break;
                            }
                        }
                    }
                    if (check) {
                        //Link to page
                        this.search = "https://1lib.eu" + book.getString("link");
                        break;
                    } else if (i > 5 && authors.size() > 0) {
                        //Prevent from searching for too long with no reason
                            this.search="https://1lib.eu/s/" + authors.get(0);
                            break;
                    } else {
                        //Backup
                        this.search = "https://1lib.eu";
                    }
                }

                if (results.length() == 0) {
                    this.search = "https://1lib.eu/book/4457273/9ca2b3";
                }
            }
            else {
                    //Just in case the server is down
                    this.search = "https://1lib.eu/book/4457273/9ca2b3";
            }

            } catch(IOException | JSONException e){
                e.printStackTrace();
            }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        caller.done(this.search);
    }
}
