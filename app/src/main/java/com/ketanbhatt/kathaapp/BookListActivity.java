package com.ketanbhatt.kathaapp;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;


import com.ketanbhatt.kathaapp.books.AvailableBooks;
import com.ketanbhatt.kathaapp.books.BookItem;

import java.io.File;
import java.util.List;

/**
 * An activity representing a list of Books. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.book_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (
                ContextCompat.checkSelfPermission(
                                BookListActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED  ||
                        ContextCompat.checkSelfPermission(
                        BookListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (BookListActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                    (BookListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Dont understand right now
            }

            ActivityCompat.requestPermissions(
                    BookListActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    Constants.READ_WRITE_EXTERNAL_STORAGE_PERMISSION
            );

        } else {
            View recyclerView = findViewById(R.id.book_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);
        }


    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        // Populate the recycler view with downloaded books
        File book_dir = new File(
                Environment.getExternalStorageDirectory() + "/" + Constants.DIRECTORY_NAME
        );

        if (book_dir.exists() && book_dir.isDirectory()) {
            // Get all books from inside the books directory
            File[] books = book_dir.listFiles();

            for (File book : books) {
                if (book.isDirectory()) {
                    AvailableBooks.setOffline(book.getName());
                }
            }

            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(AvailableBooks.ITEMS));

        } else {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(AvailableBooks.ITEMS));
            System.out.println("Couldnt find the directory");
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<BookItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<BookItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).name);

            String buttonText;
            if (holder.mItem.isOffline) {
                buttonText = "Read";
            } else {
                buttonText = "Download";
            }
            holder.mButtonView.setText(buttonText);

            if (holder.mItem.isOffline) {
                holder.mButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(BookDetailFragment.ARG_BOOK_NAME, holder.mItem.name);
                            BookDetailFragment fragment = new BookDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.book_detail_container, fragment)
                                    .commit();
                        } else {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, BookDetailActivity.class);
                            intent.putExtra(BookDetailFragment.ARG_BOOK_NAME, holder.mItem.name);

                            context.startActivity(intent);
                        }
                    }
                });
            } else {
                holder.mButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Your book will download now", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        downloadAndUnzipContent(holder.mItem.name);
                    }
                });
            }

        }

        private void downloadAndUnzipContent(String name){
            final String TAG ="downloadAndUnzipContent";
            final String book_dir_path = Environment.getExternalStorageDirectory() + "/" + Constants.DIRECTORY_NAME + "/";
            String url = "https://www.dropbox.com/s/p5vvud75wci87jq/book%201.zip?dl=1";

            DownloadFileAsync download = new DownloadFileAsync(book_dir_path + name + ".zip", getApplicationContext(), new DownloadFileAsync.PostDownload(){
                @Override
                public void downloadDone(File file) {
                    Log.i(TAG, "file download completed");

                    // check unzip file now
                    Decompress unzip = new Decompress(getApplicationContext(), file);
                    unzip.unzip(book_dir_path + "/");

                    Log.i(TAG, "file unzip completed");
                }
            });
            download.execute(url);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public final Button mButtonView;
            public BookItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
                mButtonView = (Button) view.findViewById(R.id.button);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.READ_WRITE_EXTERNAL_STORAGE_PERMISSION: {
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    View recyclerView = findViewById(R.id.book_list);
                    assert recyclerView != null;
                    setupRecyclerView((RecyclerView) recyclerView);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Enable Permissions from settings",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        }
    }
}
