package com.horrayzone.horrayzone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.Commodity;

import java.util.List;

/**
 * Created by Neeraj on 19/03/17.
 */
public class StatsViewAdapter extends RecyclerView.Adapter<StatsViewAdapter.MyViewHolder> {
    private String TAG = StatsViewAdapter.class.getSimpleName();
    private Context mContext;
    private List<Commodity> commodityList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView year, production;


        public MyViewHolder(View view) {
            super(view);
           year = (TextView) view.findViewById(R.id.year);
            production = (TextView) view.findViewById(R.id.production);


        }
    }


    public StatsViewAdapter(Context mContext, List<Commodity> commodityList) {
        this.mContext = mContext;
        this.commodityList = commodityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_view_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Commodity stat = commodityList.get(position);
        holder.year.setText(stat.getCommodity_year());
        holder.production.setText(stat.getCommodity_production());


    }



    @Override
    public int getItemCount() {
        return commodityList.size();
    }
}
