package com.ketanbhatt.kathaapp.activities;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ketanbhatt.kathaapp.R;
import com.ketanbhatt.kathaapp.adapters.BookTocAdapter;
import com.ketanbhatt.kathaapp.books.AvailableBooks;
import com.ketanbhatt.kathaapp.books.BookItem;
import com.ketanbhatt.kathaapp.fragments.BookDetailFragment;
import com.ketanbhatt.kathaapp.utils.Constants;
import com.ketanbhatt.kathaapp.utils.RecyclerItemClickListener;
import com.ketanbhatt.kathaapp.utils.TocParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class BookToc extends AppCompatActivity {

    public static final String TAG = "BookToc";
    String ARG_BOOK_NAME;
    BookItem mItem;

    RecyclerView recyclerView;
    BookTocAdapter adapter;
    List<TocParser.TOCItem> tocItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_toc);

        Intent i = getIntent();
        ARG_BOOK_NAME = i.getStringExtra("BookName");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        if(ARG_BOOK_NAME != null)   {
            mItem = AvailableBooks.ITEM_MAP.get(ARG_BOOK_NAME);
        }

        File extStore = Environment.getExternalStorageDirectory();
        File myFile = new File(extStore.getAbsolutePath() + "/" + Constants.DIRECTORY_NAME + "/" + mItem.name + "/b1/toc.ncx");

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
            Log.d(TAG, "onCreate: " + sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(sb != null) {
            tocItems = TocParser.processXML(sb.toString());
        }

        adapter = new BookTocAdapter(tocItems);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(BookToc.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        TocParser.TOCItem notice = tocItems.get(position);
                        Intent i = new Intent(getApplicationContext(), BookDetailActivity.class);
                        i.putExtra(BookDetailFragment.ARG_BOOK_NAME, ARG_BOOK_NAME);
                        i.putExtra("pageSource", notice.source);
                        i.putExtra("order", notice.playOrder);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }
}
