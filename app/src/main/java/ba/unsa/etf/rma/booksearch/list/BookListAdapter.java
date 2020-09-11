package ba.unsa.etf.rma.booksearch.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.Book;
import ba.unsa.etf.rma.booksearch.R;

public class BookListAdapter extends ArrayAdapter<Book> {
    private int resource;
    private TextView elementTitle;
    private ImageView elementImage;

    public BookListAdapter(@NonNull Context context, int resource, ArrayList<Book> items) {
        super(context, resource, items);

        this.resource = resource;
    }

    public void setBooks(ArrayList<Book> items) {
        this.clear();
        this.addAll(items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout newView;
        if(convertView == null) {
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        }
        else {
            newView = (LinearLayout) convertView;
        }

        Book book = getItem(position);
        elementTitle = newView.findViewById(R.id.inListElementTitle);
        elementImage = newView.findViewById(R.id.inListElementImage);

        elementTitle.setText(book.getVolumeInfo().getTitle());

        Glide.with(getContext()).load(book.getVolumeInfo().getImageLinks())
                .centerCrop().placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_background).into(elementImage);

        return newView;
    }
}
