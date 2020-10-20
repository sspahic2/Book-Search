package ba.unsa.etf.rma.booksearch.model;

public class Quote {
    private String text;
    private String author;

    public Quote(String quote, String author) {
        text = quote;
        this.author = author;
    }

    public String getQuote() {
        return text;
    }

    public void setQuote(String quote) {
        this.text=quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author=author;
    }
}
