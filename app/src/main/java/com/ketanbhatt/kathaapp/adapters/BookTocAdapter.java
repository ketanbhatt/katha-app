package com.ketanbhatt.kathaapp.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ketanbhatt.kathaapp.R;
import com.ketanbhatt.kathaapp.utils.TocParser;
import java.util.List;

/**
 * Created by uddishverma22 on 02/05/17.
 */

public class BookTocAdapter extends RecyclerView.Adapter<BookTocAdapter.detailsViewHolder> {

    public static final String TAG = "BookTocAdapter";

    List<TocParser.TOCItem> list;

    public BookTocAdapter(List<TocParser.TOCItem> list) {
        this.list = list;
    }

    public class detailsViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        public detailsViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    @Override
    public BookTocAdapter.detailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_toc_layout, parent, false);
        return new detailsViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(BookTocAdapter.detailsViewHolder holder, int position) {

        TocParser.TOCItem facultyObj = list.get(position);
        Log.d(TAG, "onBindViewHolder: " + facultyObj.title);
        holder.title.setText(facultyObj.title);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
