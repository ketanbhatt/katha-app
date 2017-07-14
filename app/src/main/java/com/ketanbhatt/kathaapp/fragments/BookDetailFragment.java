package com.ketanbhatt.kathaapp.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ketanbhatt.kathaapp.R;
import com.ketanbhatt.kathaapp.activities.BookDetailActivity;
import com.ketanbhatt.kathaapp.activities.BookToc;
import com.ketanbhatt.kathaapp.activities.MainActivity;
import com.ketanbhatt.kathaapp.books.AvailableBooks;
import com.ketanbhatt.kathaapp.books.BookItem;
import com.ketanbhatt.kathaapp.utils.Constants;
import com.ketanbhatt.kathaapp.utils.TocParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A fragment representing a single Book detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends Fragment {

    private static final String TAG = "BookDetailFragment";
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
    String pageSource;
    int playOrder;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    int pageNo;

    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_BOOK_NAME)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            Intent i = getActivity().getIntent();
            pageSource = i.getStringExtra("pageSource");
            playOrder = Integer.parseInt(i.getStringExtra("order"));

            Log.d(TAG, "onCreate: " + playOrder);

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

            final String book_dir_path = Environment.getExternalStorageDirectory() + "/" +
                    Constants.DIRECTORY_NAME + "/" + mItem.name;
            System.out.println(book_dir_path);

            File extStore = Environment.getExternalStorageDirectory();
            File myFile = new File(extStore.getAbsolutePath() + "/" + Constants.DIRECTORY_NAME + "/" + mItem.name + "/b1/toc.xml");


            //Parsing the XML
            StringBuilder sb = null;

            try {
                FileInputStream fis = new FileInputStream(myFile);
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(isr);
                sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            final List<TocParser.TOCItem> tocItems = TocParser.processXML(sb.toString());

//            mWebView.loadUrl("file://" + book_dir_path + "/b1/" + tocItems.get(0).source);
            mWebView.loadUrl("file://" + book_dir_path + "/b1/" + pageSource);

            pageNo = playOrder;
            rootView.findViewById(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pageNo++;
                    if (pageNo < tocItems.size()) {
                        mWebView.loadUrl("file://" + book_dir_path + "/b1/" + tocItems.get(pageNo).source);
                    } else {
                        pageNo--;
                    }
                }
            });
            rootView.findViewById(R.id.tv_previous).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pageNo--;
                    if (pageNo >= 0 && pageNo < tocItems.size()) {
                        mWebView.loadUrl("file://" + book_dir_path + "/b1/" + tocItems.get(pageNo).source);
                    } else {
                        pageNo++;
                    }
                }
            });
        }

        return rootView;
    }


//    public void onBackPressed() {
//        startActivity(new Intent(getActivity(), BookToc.class));
//        if (mWebView.canGoBack()) {
//            mWebView.goBack();
//            return true;
//        } else {
//            return false;
//        }
//
//    }

}
