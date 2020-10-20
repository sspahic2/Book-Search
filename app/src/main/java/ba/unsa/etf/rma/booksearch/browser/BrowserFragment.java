package ba.unsa.etf.rma.booksearch.browser;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ba.unsa.etf.rma.booksearch.R;

public class BrowserFragment extends Fragment {
    private RecyclerView recyclerView;
    private File currentFile;
    private MyRecyclerFileAdapter adapter;
    private List<File> fileList = new ArrayList<>();
    private TextView notFound;
    private SearchView searchView;
    private MenuItem searchItem;
    private final String PATH = "/storage/emulated/0";
    private int startingSize;
    private Context context;

    public BrowserFragment(Context context) {
        this.context = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.browser_fragment, container, false);
        recyclerView = fragmentView.findViewById(R.id.recycler_browser);
        currentFile = new File(PATH);
        notFound = fragmentView.findViewById(R.id.files_not_found_text);
        notFound.setVisibility(View.GONE);
        addFiles();
        startingSize = fileList.size();
        adapter = new MyRecyclerFileAdapter(fileList);
        adapter.setOnItemClickListener(position -> {
            File file = adapter.getItem(position);
            if(file.isDirectory()) {
                updateDirectory(file);
            }
            else {
                seeFile(file);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return fragmentView;
    }

    private void addFiles() {
        File[] files = currentFile.listFiles();
        List<File> dirs = new ArrayList<>();
        List<File> readables = new ArrayList<>();
        if(files != null) {
            for (File f : files) {
                if (f.getName().toLowerCase().matches("^.+\\.((pdf)|(epub)|(txt))$")) {
                    readables.add(f);
                }
                if(f.isDirectory()) {
                    dirs.add(f);
                }
            }
        }
        Collections.sort(dirs, comparator);
        Collections.sort(readables, comparator);
        fileList.addAll(dirs);
        fileList.addAll(readables);
    }

    private void updateDirectory(File file) {
        if(searchView.hasFocus()) {
            endSearch("");
        }
        currentFile = file;
        fileList.clear();
        addFiles();
        adapter.addNewFullList(fileList);
        runLayoutAnimation();
        recyclerView.scrollToPosition(0);
    }

    private void runLayoutAnimation() {
        LayoutAnimationController controller =AnimationUtils.loadLayoutAnimation(context, R.anim.layout_from_right_animation);
        recyclerView.setLayoutAnimation(controller);
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
        if(fileList.size() == 0) {
            notFound.setVisibility(View.VISIBLE);
        }
        else {
            notFound.setVisibility(View.GONE);
        }
    }

    private void seeFile(File file) {
        String name = file.getName();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
        Uri path = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        if(name.toLowerCase().trim().endsWith(".pdf")) {
            intent.setDataAndType(path, "application/pdf");
        }
        else if(name.toLowerCase().trim().endsWith(".epub")) {
            intent.setDataAndType(path, "application/epub");
        }
        else if(name.toLowerCase().trim().endsWith(".txt")) {
            intent.setDataAndType(path, "application/txt");
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No application found to read file.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onBackPressed() {
        File parent = null;
        if(currentFile != null && currentFile.getParent() != null) {
            parent = new File(currentFile.getParent());
        }
        if(parent != null) {
            if(!parent.getName().equals("emulated")) {
                updateDirectory(parent);
                return true;
            }

            if(startingSize > fileList.size() && parent.getName().equals("emulated")) {
                updateDirectory(new File(PATH));
                return true;
            }
        }

        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        EditText text = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        text.setTextColor(getResources().getColor(R.color.lightBlue, requireContext().getTheme()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                endSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()) {
                    adapter.getFilter().filter(newText);
                    return true;
                }
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
           if(!hasFocus) {
               endSearch("");
               if(adapter.getFilteredCount() == 0) {
                   notFound.setVisibility(View.VISIBLE);
               }
               else {
                   notFound.setVisibility(View.GONE);
               }
           }
           else {
               notFound.setVisibility(View.GONE);
           }
        });
    }

    private void endSearch(String query) {
        searchView.clearFocus();
        searchView.setIconified(true);
        searchItem.collapseActionView();
        searchView.setQuery(query, false);
    }

    private Comparator<File> comparator = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            long age1 = o1.lastModified();
            long age2 = o2.lastModified();
            return Long.compare(age2, age1);
        }
    };
}
