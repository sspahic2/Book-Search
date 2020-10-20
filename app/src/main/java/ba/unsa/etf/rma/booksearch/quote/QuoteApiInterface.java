package ba.unsa.etf.rma.booksearch.quote;

import java.util.List;

import ba.unsa.etf.rma.booksearch.model.Quote;
import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiInterface {

    @GET("quotes")
    Call<List<Quote>> getQuotes();
}
