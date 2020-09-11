package ba.unsa.etf.rma.booksearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

public class DetailsFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private TextView description;
    private TextView authors;
    private TextView categories;
    private ImageView bookCover;
    private Book book;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.book_detail_fragment, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        description = fragmentView.findViewById(R.id.description);
        authors = fragmentView.findViewById(R.id.authors);
        categories = fragmentView.findViewById(R.id.categories);
        bookCover = fragmentView.findViewById(R.id.bookCover);

        sharedViewModel.getBook().observe(getViewLifecycleOwner(), new Observer<Book>() {
            @Override
            public void onChanged(Book sentBook) {
                book = sentBook;
                description.setText(book.getVolumeInfo().getDescription());
                StringBuilder builder = new StringBuilder();
                builder.append("Author/s: ");
                for(String name : book.getVolumeInfo().getAuthors()) {
                    builder.append(name);
                    if(name.equals(book.getVolumeInfo().getAuthors().get(book.getVolumeInfo().getAuthors().size() - 1))) {
                        builder.append(" ");
                    }
                    else {

                        builder.append(", ");
                    }
                }
                authors.setText(builder.toString());

                builder = new StringBuilder();
                builder.append("Category/ies: ");
                for(String cat : book.getVolumeInfo().getCategories()) {
                    builder.append(cat);
                    if(cat.equals(book.getVolumeInfo().getCategories().get(book.getVolumeInfo().getCategories().size() - 1))) {
                        builder.append(" ");
                    }
                    else {
                        builder.append(", ");
                    }
                }
                categories.setText(builder.toString());

                Glide.with(getContext()).load(book.getVolumeInfo().getImageLinks())
                        .centerCrop().placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_foreground)
                        .fallback(R.drawable.ic_launcher_background).into(bookCover);
            }
        });

        return fragmentView;
    }
}
