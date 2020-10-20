package ba.unsa.etf.rma.booksearch.quote;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ba.unsa.etf.rma.booksearch.model.Quote;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RandomQuote {
    private List<Quote> quotes = null;
    private static final RandomQuote instance = new RandomQuote();

    private RandomQuote() {
        QuoteApiInterface apiInterface = QuoteApi.getRetrofit().create(QuoteApiInterface.class);
        Call<List<Quote>> call = apiInterface.getQuotes();
        call.enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                quotes = response.body();
            }

            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {

            }
        });
    }

    public static RandomQuote getInstance() {
        return instance;
    }
    public Quote getQuote() {
        int random = new Random().nextInt(800);
        if(quotes == null) {
            quotes = new ArrayList<>();
            Quote quote = new Quote("One glance at a book and you hear the voice of another person, perhaps someone dead for 1,000 years.To read is to voyage through time.",
                    "Carl Sagan");
            quotes.add(quote);
            random = 0;
        }

        if(quotes.get(random).getAuthor() == null) {
            quotes.get(random).setAuthor("unknown");
        }

        return quotes.get(random);
    }
}
