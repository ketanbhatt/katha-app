package com.ketanbhatt.kathaapp.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ketanbhatt.kathaapp.R;
import com.ketanbhatt.kathaapp.activities.BookDetailActivity;
import com.ketanbhatt.kathaapp.activities.MainActivity;
import com.ketanbhatt.kathaapp.books.AvailableBooks;
import com.ketanbhatt.kathaapp.books.BookItem;
import com.ketanbhatt.kathaapp.utils.Constants;

/**
 * A fragment representing a single Book detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends Fragment {

    private WebView mWebView;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_BOOK_NAME = "book_name";

    /**
     * The dummy content this fragment is presenting.
     */
    private BookItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_BOOK_NAME)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = AvailableBooks.ITEM_MAP.get(getArguments().getString(ARG_BOOK_NAME));

            AppCompatActivity activity = (AppCompatActivity) this.getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(mItem.name);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
             mWebView = rootView.findViewById(R.id.book_webview);

            // Enable Javascript
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setSupportZoom(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setDomStorageEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            // Force links and redirects to open in the WebView instead of in a browser
            mWebView.setWebViewClient(new WebViewClient());
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);

            String book_dir_path = Environment.getExternalStorageDirectory() + "/" +
                    Constants.DIRECTORY_NAME + "/" + mItem.name;
            System.out.println(book_dir_path);
            mWebView.loadUrl("file://" + book_dir_path + "/index.html");
        }

        return rootView;
    }

    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return false;
        }
    }

}
