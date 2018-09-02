package com.horrayzone.horrayzone.adapter;

/**
 * Created by DELL on 11/7/2017.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.horrayzone.horrayzone.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Neeraj on 19/03/17.
 */
public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.MyViewHolder> {
    private String TAG = ListViewAdapter.class.getSimpleName();
    private Context mContext;
    private List<String> DataList;
    public List<String> Selecteddata;
    int colorwhite;
    int colorprimary;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_data;


        public MyViewHolder(View view) {
            super(view);
            text_data = (TextView) view.findViewById(R.id.text_data);


        }
    }


    public ListViewAdapter(Context mContext, List<String> DataList) {
        this.mContext = mContext;
        this.DataList = DataList;
        Selecteddata=new ArrayList<>();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final String data = DataList.get(position);
        holder.text_data.setText(data);


        // loading Topic cover using Glide library



        holder.text_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getAlpha()==.5f){
                    view.setAlpha(1f);

                    Selecteddata.remove(data);
                }
                else {
                    view.setAlpha(.5f);

                    Selecteddata.add(data);
                }










            }
        });
    }



    @Override
    public int getItemCount() {
        return DataList.size();
    }

    public interface AdapterListener {
        void onListSelected(String data);
    }
}
