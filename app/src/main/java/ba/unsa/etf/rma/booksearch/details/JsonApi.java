package ba.unsa.etf.rma.booksearch.details;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonApi {
    @GET("volumes")
    Call<JSONObject> getSimilarBooks(@Query("q") String query) ;
}
