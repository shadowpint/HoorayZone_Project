package com.horrayzone.horrayzone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.Price;
import com.horrayzone.horrayzone.util.OnItemClickListener;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {

    private List<Price> mPriceEntries;

    private Context context;
    private int lastSelectedPosition = -1;

    public PriceAdapter(Context context, List<Price> entries) {
        this.context = context;
        mPriceEntries = entries;
    }

    @Override
    public PriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_price, parent, false);

        final PriceAdapter.ViewHolder holder = new PriceAdapter.ViewHolder(itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                lastSelectedPosition = position;


                notifyDataSetChanged();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(PriceAdapter.ViewHolder holder, int position) {
        Price entry = mPriceEntries.get(position);

        holder.mFullNameView.setText(WordUtils.capitalizeFully(entry.getName()));
        //holder.mDateView.setText();
        holder.mDescription.setText(entry.getDescription());
        holder.mPrice.setText("INR "+entry.getPrice());

    }

    @Override
    public int getItemCount() {
        return mPriceEntries != null ? mPriceEntries.size() : 0;
    }

    public void setPriceEntries(List<Price> entries) {
        mPriceEntries = entries;
        notifyDataSetChanged();
    }

    public Price getPrice(int position) {
        return mPriceEntries.get(position);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


        public RadioButton mRadioButton;
        public TextView mFullNameView;
        public TextView mDescription;
        public TextView mPrice;


        public ViewHolder(View itemView) {
            super(itemView);

            mFullNameView = (TextView) itemView.findViewById(R.id.full_name);
            mDescription = (TextView) itemView.findViewById(R.id.description);
            mPrice = (TextView) itemView.findViewById(R.id.price);
        }
    }

}