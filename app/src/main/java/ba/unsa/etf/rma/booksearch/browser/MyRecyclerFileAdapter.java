package ba.unsa.etf.rma.booksearch.browser;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.booksearch.R;

public class MyRecyclerFileAdapter extends RecyclerView.Adapter<MyRecyclerFileAdapter.FileViewHolder> implements Filterable {
    private List<File> files;
    private List<File> filesFull;
    private OnFileListener listener;
    private int filteredCount = -1;

    public MyRecyclerFileAdapter(List<File> files) {
        this.files = files;
        filesFull = new ArrayList<>(files);
    }

    public void setOnItemClickListener(OnFileListener listener) {
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredCount = -1;
            List<File> filterList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                filterList.addAll(filesFull);
            }
            else {
                String filter = constraint.toString().toLowerCase().trim();

                for(File f : filesFull) {
                    if(f.getName().toLowerCase().contains(filter)) {
                        filterList.add(f);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;
            results.count = filterList.size();
            files.clear();
            files.addAll(filterList);
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredCount = results.count;
            notifyDataSetChanged();
        }
    };


    public interface OnFileListener {
        void onFileClick(int position);
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_file_item, parent, false);
        return new MyRecyclerFileAdapter.FileViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        File file = files.get(position);
        holder.fileName.setText(file.getName());
        String text;
        int image;
        if(file.isDirectory()) {
            image = R.drawable.ic_baseline_folder_open_24;
            File[] files = file.listFiles();
            if(files != null) {
                text = files.length + " items";
            }
            else {
                text = 0 + " items";
            }
        }
        else {
            image = R.drawable.ic_baseline_description_24;
            DecimalFormat df = new DecimalFormat("0.00");
            double size =Double.parseDouble(df.format(((double) file.length())/1024));
            if(size >= 1024) {
                text = df.format(size/1024) + " MB";
            }
            else {
                text = size + " KB";
            }
        }

        Glide.with(holder.icon.getContext()).load(image).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                holder.icon.setImageDrawable(resource);
            }
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) { }
        });
        holder.fileSize.setText(text);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView fileName;
        private TextView fileSize;
        private ImageView icon;
        private OnFileListener onFileListener;

        public FileViewHolder(@NonNull View itemView, OnFileListener listener) {
            super(itemView);
            fileName = itemView.findViewById(R.id.recycler_file_text);
            fileSize = itemView.findViewById(R.id.text_file_size);
            icon = itemView.findViewById(R.id.file_icon);
            onFileListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFileListener.onFileClick(getAdapterPosition());
        }
    }

    public File getItem(int position) {
        return files.get(position);
    }

    public void addNewFullList(List<File> newList) {
        filesFull = new ArrayList<>(newList);
    }

    public int getFilteredCount() {
        System.out.println("//////////////////////////////////////////////  " + filteredCount);
        return filteredCount;
    }
}
