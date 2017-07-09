package com.ketanbhatt.kathaapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ketanbhatt.kathaapp.R;
import com.ketanbhatt.kathaapp.activities.BookDetailActivity;
import com.ketanbhatt.kathaapp.books.AvailableBooks;
import com.ketanbhatt.kathaapp.books.BookItem;
import com.ketanbhatt.kathaapp.utils.Constants;
import com.ketanbhatt.kathaapp.utils.Decompress;
import com.ketanbhatt.kathaapp.utils.DownloadFileAsync;
import com.ketanbhatt.kathaapp.utils.GridSpacingItemDecoration;

import java.io.File;
import java.util.List;

/**
 * Created by Simar Arora on 08/07/17.
 */

public class BookListFragment extends Fragment {

    FrameLayout rootFL;
    ProgressBar loaderPB;
    private RecyclerView booksRV;

    private boolean onlyOfflineBooks;

    public static BookListFragment newInstance(boolean onlyOfflineBooks) {
        BookListFragment bookListFragment = new BookListFragment();
        bookListFragment.onlyOfflineBooks = onlyOfflineBooks;
        return bookListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        rootFL = view.findViewById(R.id.fl_root);
        loaderPB = view.findViewById(R.id.pb_loader);
        booksRV = view.findViewById(R.id.rv_books);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        booksRV.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        booksRV.addItemDecoration(new GridSpacingItemDecoration(2, getResources().getDimensionPixelSize(R.dimen.text_margin), true));
        setupRecyclerView();
    }

    private void setupRecyclerView() {

        new AsyncTask<Void, Void, List<BookItem>>() {

            @Override
            protected List<BookItem> doInBackground(Void... voids) {
                File booksDir = new File(Environment.getExternalStorageDirectory() + "/" + Constants.DIRECTORY_NAME);
                if (booksDir.exists() && booksDir.isDirectory()) {
                    // Get all books from inside the books directory
                    File[] books = booksDir.listFiles();
                    for (File book : books) {
                        if (book.isDirectory()) {
                            AvailableBooks.setOffline(book.getName());
                        }
                    }
                } else booksDir.mkdir();
                if (onlyOfflineBooks)
                    return AvailableBooks.getOfflineBooks();
                else return AvailableBooks.ITEMS;
            }

            @Override
            protected void onPostExecute(List<BookItem> books) {
                super.onPostExecute(books);
                booksRV.setAdapter(new BookListAdapter(getActivity(), books, rootFL));
                loaderPB.setVisibility(View.GONE);
                booksRV.setVisibility(View.VISIBLE);
            }
        }.execute();
    }


    static class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

        private Context context;
        private final List<BookItem> items;
        private View rootView;
        private OnItemClickListener onBookItemClickListsner = new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BookItem book = items.get(position);
                if (book.isOffline) {
                    openDetails(book);
                } else {
                    Snackbar.make(rootView, "Your book will download now", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    downloadAndUnzipContent(book, position);
                }
            }
        };

        public BookListAdapter(Context context, List<BookItem> items, View rootView) {
            this.context = context;
            this.items = items;
            this.rootView = rootView;
        }

        interface OnItemClickListener {
            void onItemClick(int position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_content, parent, false);
            return new ViewHolder(view, onBookItemClickListsner);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.nameTV.setText(items.get(position).name);
            holder.detailsTV.setText(items.get(position).details);
        }

        private void downloadAndUnzipContent(final BookItem bookItem, final int position) {
            final String TAG = "downloadAndUnzipContent";
            final String book_dir_path = Environment.getExternalStorageDirectory() + "/" + Constants.DIRECTORY_NAME + "/";
            String url = bookItem.url;

            DownloadFileAsync download = new DownloadFileAsync(book_dir_path + bookItem.name + ".zip", context, new DownloadFileAsync.PostDownload() {
                @Override
                public void onDownloadComplete(File file) {
                    Log.i(TAG, "file download completed");

                    // check unzip file now
                    Decompress unzip = new Decompress(context, file);
                    unzip.unzip(book_dir_path + "/");

                    Log.i(TAG, "file unzip completed");
                    bookItem.isOffline = true;
                    openDetails(bookItem);
                }

                @Override
                public void onDownloadFailure() {

                }
            });
            download.execute(url);
        }

        private void openDetails(BookItem book) {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra(BookDetailFragment.ARG_BOOK_NAME, book.name);
            context.startActivity(intent);
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView nameTV;
            TextView detailsTV;

            OnItemClickListener onItemClickListener;

            ViewHolder(View view, OnItemClickListener onItemClickListener) {
                super(view);
                this.onItemClickListener = onItemClickListener;
                nameTV = view.findViewById(R.id.tv_name);
                detailsTV = view.findViewById(R.id.tv_details);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
