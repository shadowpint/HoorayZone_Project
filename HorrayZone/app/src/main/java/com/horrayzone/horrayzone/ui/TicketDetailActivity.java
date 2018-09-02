package com.horrayzone.horrayzone.ui;


import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Property;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.horrayzone.horrayzone.BuildConfig;
import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.Event;
import com.horrayzone.horrayzone.model.Ticket;
import com.horrayzone.horrayzone.sync.AccountAuthenticator;
import com.horrayzone.horrayzone.ui.widget.ViewBackgroundColorProperty;
import com.horrayzone.horrayzone.util.AccountUtils;
import com.github.sumimakito.awesomeqr.AwesomeQRCode;
import com.google.android.gms.location.LocationServices;
import com.mobapphome.mahencryptorlib.MAHEncryptor;
import com.vipul.hp_hp.library.Layout_to_Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketDetailActivity extends BaseActivity  {

    public static final String EXTRA_ENTRY_TITLE = "extra_entry_title";
    public static final String EXTRA_ENTRY_CONTENT = "extra_entry_content";
    public static final String EXTRA_ENTRY_IMAGE_URL = "extra_entry_image_url";
    public static final String STATE_ACTION_BAR_TRANSPARENT = "state_action_bar_transparent";
    private static final String TAG = "TicketActivity";
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

    private static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";
    private static final String SHARED_FOLDER = "Hoorayzone/data";

    //Define Any Layout
    /**
     * Custom property for background color transitions on View's.
     */
    private final int WRITE_STORAGE = 110;
    private static final Property<View, Integer> BACKGROUND_COLOR_PROPERTY =
            new ViewBackgroundColorProperty(Integer.TYPE, "backgroundColor");

    private Toolbar mToolbarActionBar;
    private ImageView mImageView;
    private ObjectAnimator mToolbarBackgroundColorAnimator;
    private int mMaxToolbarElevation;
    private boolean mToolbarActionBarTransparent = true;
    @BindView(R.id.event_title) TextView event_title;
    Layout_to_Image layout_to_image;  //Create Object of Layout_to_Image Class
    @BindView(R.id.event_genre) TextView event_genre;
    @BindView(R.id.event_duration) TextView event_duration;
    @BindView(R.id.event_date) TextView event_date;
    @BindView(R.id.event_time) TextView event_time;
    @BindView(R.id.event_venue) TextView event_venue;
    @BindView(R.id.event_qr) AppCompatImageView event_qr;
    //Fragments
    private Event event;

    private BottomNavigationView mBottomNavigationView;
    Bitmap bitmap;
    @BindView(R.id.constraint_root)
    ConstraintLayout constraintLayout;
    private String mAmount;
    private Ticket ticket;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {

                case R.id.action_share:


                    if (isStoragePermissionGranted()) {
                        bitmap = viewToBitmap(constraintLayout);
                        if (bitmap != null) {

                            File imgefile = getFile(bitmap);
                            Log.e("noerror", imgefile.getAbsolutePath());
                            final Uri uri = FileProvider.getUriForFile(TicketDetailActivity.this, SHARED_PROVIDER_AUTHORITY, imgefile);

                            // Create a intent
                            final ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(TicketDetailActivity.this)
                                    .setType("image/*")
                                    .addStream(uri);

                            // Start the intent
                            final Intent chooserIntent = intentBuilder.createChooserIntent();
                            startActivity(chooserIntent);
                        } else {
                            Log.e("error", "True");
                        }
                    } else {
                        manageDeniedPermission();
                    }














                    return true;
                case R.id.action_save:
//                    layout_to_image=new Layout_to_Image(TicketDetailActivity.this,frameLayout);

                    //now call the main working function ;) and hold the returned image in bitmap
                    if (isStoragePermissionGranted()) {
                        bitmap = viewToBitmap(constraintLayout);
                        if (bitmap != null) {
                            Log.e("noerror", "True");
                            storeImage(bitmap);
                        } else {
                            Log.e("error", "True");
                        }
                    } else {
                        manageDeniedPermission();
                    }



                    return true;
                case R.id.action_contact:


                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_ACTION_BAR_TRANSPARENT, mToolbarActionBarTransparent);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        mToolbarActionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbarActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tickets");
        Intent intent = getIntent();

        ticket = intent.getExtras().getParcelable("ticket");
        ButterKnife.bind(this);
        event_title.setText(ticket.getEventName());
        event_genre.setText(ticket.getEventGenre());
        Log.e("event_date", ticket.getEventTime());
        event_venue.setText(ticket.getEventVenue());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        try {
            Date d = formatter.parse(ticket.getEventTime().replaceAll("Z$", "+0000"));
            DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat time = new SimpleDateFormat("hh:mm:ss a");
            System.out.println("Date: " + date.format(d));
            System.out.println("Time: " + time.format(d));
            event_date.setText(date.format(d));
            event_time.setText(time.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Account account = new Account(AccountUtils.getActiveAccountName(TicketDetailActivity.this), AccountAuthenticator.ACCOUNT_TYPE);
        AccountManager manager = AccountManager.get(TicketDetailActivity.this);
        String authToken = manager.peekAuthToken(account, AccountAuthenticator.AUTHTOKEN_TYPE);
        System.out.println("name: " + account.name);
        MAHEncryptor mahEncryptor = null;
        try {
            mahEncryptor = MAHEncryptor.newInstance("Neeraj@8960Baba@8960");
            String encrypted = mahEncryptor.encode(account.name + " " + ticket.getTransactionId());
            System.out.println("encrypted: " + encrypted);
            Bitmap qrCode = AwesomeQRCode.create(encrypted, 800, 20, 0.3f, Color.BLACK, Color.WHITE, null, true, true);
            event_qr.setImageBitmap(qrCode);
            String decrypted = mahEncryptor.decode(encrypted);
            System.out.println("decrypted: " + decrypted);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }


        if (savedInstanceState != null) {
            mToolbarActionBarTransparent =
                    savedInstanceState.getBoolean(STATE_ACTION_BAR_TRANSPARENT);
        }




//        mImageView = (ImageView) findViewById(R.id.image);
//        Picasso.with(this)
//                .load(imageUrl)
//                .placeholder(R.drawable.image_placeholder)
//                .into(mImageView);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    private File getFile(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Toast.makeText(this, "Access Denied", Toast.LENGTH_LONG).show();
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();

//            Uri uriToImage = FileProvider.getUriForFile(
//                    getApplicationContext(), FILES_AUTHORITY, pictureFile);
//            Intent shareIntent = ShareCompat.IntentBuilder.from(TicketDetailActivity.this)
//                    .setStream(uriToImage)
//                    .getIntent();
//// Provide read access
//            shareIntent.setData(uriToImage);
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return pictureFile;


    }


    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Toast.makeText(this, "Access Denied", Toast.LENGTH_LONG).show();
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Hoorayzone/data/"
        );

        Log.e("file", String.valueOf(mediaStorageDir));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        Toast.makeText(this, "File Saved to " + mediaFile, Toast.LENGTH_LONG).show();
        return mediaFile;
        //if (Dexter.)


    }


    private boolean isStoragePermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(
                TicketDetailActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
    }


    private void manageDeniedPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(TicketDetailActivity.this),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Aquí muestras confirmación explicativa al usuario
            // por si rechazó los permisos anteriormente
        } else {
            ActivityCompat.requestPermissions(
                    TicketDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_STORAGE);
        }
    }
}
