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

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private List<Price> mPriceEntries;
    private OnItemClickListener mItemClickListener;
    private Context context;
    private int lastSelectedPosition = -1;

    public TicketAdapter(Context context, List<Price> entries) {
        this.context = context;
        mPriceEntries = entries;
    }

    @Override
    public TicketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ticket, parent, false);

        final TicketAdapter.ViewHolder holder = new TicketAdapter.ViewHolder(itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                lastSelectedPosition = position;
                if (mItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    mItemClickListener.onItemClick(holder.itemView, position);

                }

                notifyDataSetChanged();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(TicketAdapter.ViewHolder holder, int position) {
        Price entry = mPriceEntries.get(position);

        holder.mFullNameView.setText(WordUtils.capitalizeFully(entry.getName()));
        //holder.mDateView.setText();
        holder.mDescription.setText(entry.getDescription());
        holder.mPrice.setText(entry.getPrice());
        holder.mRadioButton.setChecked(lastSelectedPosition == position);
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public RadioButton mRadioButton;
        public TextView mFullNameView;
        public TextView mDescription;
        public TextView mPrice;


        public ViewHolder(View itemView) {
            super(itemView);
            mRadioButton = (RadioButton) itemView.findViewById(R.id.radio);
            mFullNameView = (TextView) itemView.findViewById(R.id.full_name);
            mDescription = (TextView) itemView.findViewById(R.id.description);
            mPrice = (TextView) itemView.findViewById(R.id.price);
        }
    }

}