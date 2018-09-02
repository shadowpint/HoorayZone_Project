package com.horrayzone.horrayzone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.City;
import com.horrayzone.horrayzone.model.Event;

import java.util.List;

/**
 * Created by takusemba on 2017/08/03.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    private Context context;
    private List<Event> eventList;
    private HorizontalAdapterListener listener;

    public HorizontalAdapter(Context context, List<Event> eventList, HorizontalAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.eventList = eventList;

    }

    @Override
    public HorizontalAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.location_event_card, viewGroup, false);
        return new HorizontalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorizontalAdapter.ViewHolder holder, int position) {
        final Event event = eventList.get(position);
        holder.title.setText(event.getName());


        Glide.with(context)
                .load(event.getLeadImageUrl())

                .into(holder.thumbnail);


        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send selected city in callback
                listener.onEventSelected(eventList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface HorizontalAdapterListener {
        void onEventSelected(Event event);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        private TextView title;

        ViewHolder(final View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.title);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected city in callback
                    listener.onEventSelected(eventList.get(getAdapterPosition()));
                }
            });
        }
    }
}