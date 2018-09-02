package com.horrayzone.horrayzone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.horrayzone.horrayzone.R;

import java.util.List;

/**
 * Created by Neeraj on 19/03/17.
 */
public class CountryStatsAdapter extends RecyclerView.Adapter<CountryStatsAdapter.MyViewHolder> {
    private String TAG = CountryStatsAdapter.class.getSimpleName();
    private Context mContext;
    private List<CountryStats> StatsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView line;


        public MyViewHolder(View view) {
            super(view);
         line = (TextView) view.findViewById(R.id.line);



        }
    }


    public CountryStatsAdapter(Context mContext, List<CountryStats> StatsList) {
        this.mContext = mContext;
        this.StatsList = StatsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_stats_view_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CountryStats stat = StatsList.get(position);
        holder.line.setText("\u25BA"+" "+stat.getInfo());



    }



    @Override
    public int getItemCount() {
        return StatsList.size();
    }
}
