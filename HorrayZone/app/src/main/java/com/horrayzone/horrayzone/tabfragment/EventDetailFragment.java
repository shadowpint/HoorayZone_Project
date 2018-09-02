package com.horrayzone.horrayzone.tabfragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.ui.AuthenticatorActivity;
import com.horrayzone.horrayzone.ui.CheckoutActivity;
import com.horrayzone.horrayzone.ui.EventDetailActivity;
import com.horrayzone.horrayzone.ui.widget.FontTextView;
import com.horrayzone.horrayzone.util.AccountUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okio.Buffer;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailFragment extends Fragment {


    private FontTextView mtitleView;
    private FontTextView mTimeView,mAddressView;
    private FontTextView mGoingView;
    private Button mBookButton;
    private EventDetailActivity mActivity;
    private Event event;
    private FontTextView mAboutView;

    public EventDetailFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (EventDetailActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);
        mtitleView = (FontTextView) rootView.findViewById(R.id.title_center);
        mTimeView = (FontTextView) rootView.findViewById(R.id.time);
        mAddressView = (FontTextView) rootView.findViewById(R.id.address);
        mGoingView = (FontTextView) rootView.findViewById(R.id.going);
        mAboutView = (FontTextView) rootView.findViewById(R.id.about_text);
        mBookButton = (Button) rootView.findViewById(R.id.ticket_button);
        event=mActivity.event;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        try {
            Date d = formatter.parse(event.getDate().replaceAll("Z$", "+0000"));
            DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat time = new SimpleDateFormat("hh:mm:ss a");
            System.out.println("Date: " + date.format(d));
            System.out.println("Time: " + time.format(d));
            mTimeView.setText(date.format(d)+", "+time.format(d));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        mtitleView.setText(event.getName());
        mAddressView.setText(event.getAddress());
        mGoingView.setText("2.5k Going");
        mAboutView.setText(event.getDescription());
        mBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountUtils.hasActiveAccount(getActivity())) {
                    Intent intent = new Intent(getActivity(), AuthenticatorActivity.class);

                    startActivity(intent);


                } else {
                    Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                    Log.e("event_detail", event.getName());
                    intent.putExtra("event", event);


                    startActivity(intent);
                }
            }
        });
        return rootView;
    }


}
