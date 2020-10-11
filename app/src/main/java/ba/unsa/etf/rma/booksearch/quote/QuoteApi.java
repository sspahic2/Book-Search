package ba.unsa.etf.rma.booksearch.quote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuoteApi {
    private static String baseURL = "https://type.fit/api/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
