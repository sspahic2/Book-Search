package ba.unsa.etf.rma.booksearch.popular;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import ba.unsa.etf.rma.booksearch.R;
import ba.unsa.etf.rma.booksearch.data.Book;

public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleAdapter.ThisViewHolder> {
    private List<Book> bookList;
    private ClickListener<Book> bookListener;

    public interface ClickListener<T> {
        void onItemClick(T data);
    }

    public void setOnItemClickListener(ClickListener<Book> listener) {
        bookListener = listener;
    }

    public MyRecycleAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ThisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new ThisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThisViewHolder holder, int position) {
        Book book = bookList.get(position);
        StringBuilder builder = new StringBuilder();
        //The title may get too large
        for(int i = 0; i < book.getVolumeInfo().getTitle().length() && i < 40; i++) {
            builder.append(book.getVolumeInfo().getTitle().charAt(i));
        }
        if(book.getVolumeInfo().getTitle().length() > 40) {
            builder.append("...");
        }

        holder.title.setText(builder.toString());
        Glide.with(holder.image.getContext()).load(book.getVolumeInfo().getImageLink())
                .placeholder(R.drawable.placeholder_book).error(R.drawable.placeholder_book)
                .fallback(R.drawable.placeholder_book).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                holder.image.setImageDrawable(resource);
            }
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                holder.image.setImageDrawable(placeholder);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookListener.onItemClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class ThisViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView image;
        private CardView cardView;
        public ThisViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recycler_view_text);
            image = itemView.findViewById(R.id.recycler_view_image);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

}
