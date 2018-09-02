package com.horrayzone.horrayzone.ui;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Property;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.adapter.ViewPagerAdapter;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.tabfragment.EventDetailFragment;
import com.horrayzone.horrayzone.tabfragment.EventPriceFragment;
import com.horrayzone.horrayzone.tabfragment.EventRatingFragment;
import com.horrayzone.horrayzone.ui.widget.SynchronizedScrollView;
import com.horrayzone.horrayzone.ui.widget.ViewBackgroundColorProperty;
import com.horrayzone.horrayzone.util.AccountUtils;
import com.squareup.picasso.Picasso;

public class EventDetailActivity extends BaseActivity implements SynchronizedScrollView.OnScrollListener {

    public static final String EXTRA_ENTRY_TITLE = "extra_entry_title";
    public static final String EXTRA_ENTRY_CONTENT = "extra_entry_content";
    public static final String EXTRA_ENTRY_IMAGE_URL = "extra_entry_image_url";
    public static final String STATE_ACTION_BAR_TRANSPARENT = "state_action_bar_transparent";
    private static final String TAG = "BlogEntryReaderActivity";
    /**
     * The default interpolator for animations.
     */
    private static final long ANIMATION_DURATION = 250L;

    /**
     * The default interpolator for animations.
     */
    private static final DecelerateInterpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();

    /**
     * ARGB evaluator for color transition animations.
     */
    private static final ArgbEvaluator COLOR_EVALUATOR = new ArgbEvaluator();

    /**
     * Custom property for background color transitions on View's.
     */
    private static final Property<View, Integer> BACKGROUND_COLOR_PROPERTY =
            new ViewBackgroundColorProperty(Integer.TYPE, "backgroundColor");
    EventPriceFragment eventPriceFragment;
    EventDetailFragment eventDetailFragment;
    EventRatingFragment eventRatingFragment;
    private Toolbar mToolbarActionBar;
    private ImageView mImageView;
    private ObjectAnimator mToolbarBackgroundColorAnimator;
    private int mMaxToolbarElevation;
    private boolean mToolbarActionBarTransparent = true;

    //Fragments
    public Event event;
    private TabLayout tabLayout;
    //This is our viewPager
    private ViewPager viewPager;
    private BottomNavigationView mBottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {

                case R.id.action_contact:


                    return true;
                case R.id.action_book:
                    if (!AccountUtils.hasActiveAccount(EventDetailActivity.this)) {
                        Intent intent = new Intent(EventDetailActivity.this, AuthenticatorActivity.class);

                        startActivity(intent);


                    } else {
                        Intent intent = new Intent(EventDetailActivity.this, CheckoutActivity.class);
                        Log.e("event_detail", event.getName());
                        intent.putExtra("event", event);


                        startActivity(intent);
                    }



                    return true;
                case R.id.action_direction:
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/search/?api=1&query=" + event.getLat() + "," + event.getLng()));
                    startActivity(intent);

                    return true;

            }
            return false;
        }
    };

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        mToolbarActionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbarActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        String title;
        String content;
        String imageUrl;

        Intent intent = getIntent();

        event = intent.getExtras().getParcelable("event");
        title = event.getName();
        content = event.getDescription();
        imageUrl = event.getLeadImageUrl();


        if (savedInstanceState != null) {
            mToolbarActionBarTransparent =
                    savedInstanceState.getBoolean(STATE_ACTION_BAR_TRANSPARENT);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position, false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);


        mImageView = (ImageView) findViewById(R.id.image);
        Picasso.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(mImageView);

        mMaxToolbarElevation = getResources().getDimensionPixelSize(R.dimen.appbar_elevation);

        mToolbarBackgroundColorAnimator = ObjectAnimator.ofInt(
                mToolbarActionBar, BACKGROUND_COLOR_PROPERTY, 0);
        mToolbarBackgroundColorAnimator.setInterpolator(ANIMATION_INTERPOLATOR);
        mToolbarBackgroundColorAnimator.setEvaluator(COLOR_EVALUATOR);
        mToolbarBackgroundColorAnimator.setDuration(ANIMATION_DURATION);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_ACTION_BAR_TRANSPARENT, mToolbarActionBarTransparent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        eventDetailFragment = new EventDetailFragment();
        eventPriceFragment = new EventPriceFragment();
        eventRatingFragment = new EventRatingFragment();
        adapter.addFragment(eventDetailFragment, "Details");
        adapter.addFragment(eventPriceFragment, "Price");
//        adapter.addFragment(eventRatingFragment, "Reviews");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onScrollChanged(int scrollX, int scrollY, int deltaX, int deltaY) {
        boolean shouldGoTransparent = scrollY < 20;

        if (shouldGoTransparent != mToolbarActionBarTransparent) {
            //int startColor = shouldGoTransparent ? Color.WHITE : Color.TRANSPARENT;
            int endColor = shouldGoTransparent ? Color.TRANSPARENT : Color.WHITE;
            int desiredElevation = shouldGoTransparent ? 0 : mMaxToolbarElevation;

            mToolbarBackgroundColorAnimator.setIntValues(endColor);
            mToolbarBackgroundColorAnimator.start();

            /*ObjectAnimator animator = ObjectAnimator.ofInt(mToolbarActionBar,
                    "backgroundColor", startColor, endColor);
            animator.setEvaluator(new ArgbEvaluator());
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(ANIMATION_DURATION);
            animator.start();*/

            ViewCompat.animate(mToolbarActionBar).translationZ(desiredElevation)
                    .setDuration(ANIMATION_DURATION)
                    .setInterpolator(ANIMATION_INTERPOLATOR)
                    .start();
        }

        mToolbarActionBarTransparent = shouldGoTransparent;
    }
}
