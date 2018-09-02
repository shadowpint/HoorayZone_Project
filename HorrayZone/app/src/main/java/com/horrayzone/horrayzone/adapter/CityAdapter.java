package com.horrayzone.horrayzone.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.City;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neeraj on 19/03/17.
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<City> cityList;
    private List<City> cityListFiltered;
    private CityAdapterListener listener;
    private String TAG = CityAdapter.class.getSimpleName();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.title);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected city in callback
                    listener.onCitySelected(cityListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public CityAdapter(Context context, List<City> cityList, CityAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.cityList = cityList;
        this.cityListFiltered = cityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final City city = cityListFiltered.get(position);
        holder.name.setText(city.getName());


        Glide.with(context)
                .load(city.getUrl())

                .into(holder.thumbnail);
        Log.e("image_url",city.getUrl());
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send selected city in callback
                listener.onCitySelected(cityListFiltered.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return cityListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    cityListFiltered = cityList;
                } else {
                    List<City> filteredList = new ArrayList<>();
                    for (City row : cityList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    cityListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = cityListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                cityListFiltered = (ArrayList<City>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CityAdapterListener {
        void onCitySelected(City city);
    }
}
