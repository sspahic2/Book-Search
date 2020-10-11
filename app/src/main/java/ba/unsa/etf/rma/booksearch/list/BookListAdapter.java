package ba.unsa.etf.rma.booksearch.list;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import ba.unsa.etf.rma.booksearch.R;
import ba.unsa.etf.rma.booksearch.data.Book;

public class BookListAdapter extends ArrayAdapter<Book> {
    private int resource;

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
        Viewholder holder;
        if(convertView == null) {
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().getSystemService(inflater);
            holder = new Viewholder();
            li.inflate(resource, newView, true);

            holder.elementTitle = newView.findViewById(R.id.inListElementTitle);
            holder.elementImage = newView.findViewById(R.id.inListElementImage);
            newView.setTag(holder);
        }
        else {
            newView = (LinearLayout) convertView;
            holder = (Viewholder) convertView.getTag();
        }

        Book book = getItem(position);
        holder.elementTitle.setText(book.getVolumeInfo().getTitle());

        Glide.with(getContext()).load(book.getVolumeInfo().getImageLink())
                .placeholder(R.drawable.placeholder_book).error(R.drawable.placeholder_book)
                .fallback(R.drawable.placeholder_book).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                holder.elementImage.setImageDrawable(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

        return newView;
    }

    static class Viewholder {
        TextView elementTitle;
        ImageView elementImage;
    }
}
