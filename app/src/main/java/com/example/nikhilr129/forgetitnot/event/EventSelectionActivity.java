package com.example.nikhilr129.forgetitnot.event;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nikhilr129.forgetitnot.Fragments.TimePickerFragment;
import com.example.nikhilr129.forgetitnot.R;
import com.example.nikhilr129.forgetitnot.action.ActionSelectionActivity;
import com.example.nikhilr129.forgetitnot.event.eventDialog.IncomingCallDialog;
import com.example.nikhilr129.forgetitnot.event.eventDialog.OutGoingCallDialog;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by kanchicoder on 4/10/17.
 */

public class EventSelectionActivity extends AppCompatActivity implements TimePickerFragment.OnDataPass {
    String title;
    String event;
    private int MY_PERMISSION_REQUEST_LOCATION=50;
    String a0,a1,a2,a3,a4,a5;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    LayoutInflater  inflater;
    private View viewRoot;

    //test done by nikhil

    private final int INCOMING_PICK_CONTACT = 1, OUTGOING_PICK_CONTACT= 2, SELECT_LOCATION=3;

    HashMap<String,String> hm;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode)
        {

            case INCOMING_PICK_CONTACT:
                if(resultCode==RESULT_OK) {
                    IncomingGetContact(data);
                }
                break;
            case OUTGOING_PICK_CONTACT:
                if(resultCode==RESULT_OK) {
                    OutGoingGetContact(data);
                }
                break;

            case SELECT_LOCATION:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(resultCode==RESULT_OK) {
                        Place place = PlacePicker.getPlace(this,data);
                        String toastMsg = String.format("Place: %s", place.getName());
                        adapter.data[3][0]=Double.toString(place.getLatLng().latitude);
                        adapter.data[3][1]=Double.toString(place.getLatLng().longitude);
                        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                    }
                    else {
                        eventList.get(3).setSelected();
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                        Snackbar.make(viewRoot,
                                "Needs Location permission",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (ActivityCompat.checkSelfPermission(EventSelectionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                            ActivityCompat.requestPermissions(EventSelectionActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_LOCATION);
                                        if (ActivityCompat.checkSelfPermission(EventSelectionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                            ActivityCompat.requestPermissions(EventSelectionActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_REQUEST_LOCATION);
                                    }
                                }).show();
                    }else{
                        if (ActivityCompat.checkSelfPermission(EventSelectionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(EventSelectionActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_LOCATION);
                        if (ActivityCompat.checkSelfPermission(EventSelectionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(EventSelectionActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_REQUEST_LOCATION);
                    }
                }

                break;
        }
    }
    //
    private void IncomingGetContact(Intent data) {
        Uri contactUri = data.getData();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver()
                .query(contactUri, projection, null, null, null);
        cursor.moveToFirst();
        int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String number = cursor.getString(column);
        IncomingCallDialog obj = new IncomingCallDialog(EventSelectionActivity.this, eventList.get(1), adapter);
        obj.create().show();
        View v = obj.getView();
        TextView textView = (TextView) v.findViewById(R.id.event_call_dialog_textView);
        textView.setText(number);
        adapter.data[1][0]=number;
    }
    private void OutGoingGetContact(Intent data) {
        Uri contactUri = data.getData();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver()
                .query(contactUri, projection, null, null, null);
        cursor.moveToFirst();
        int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String number = cursor.getString(column);
        OutGoingCallDialog obj = new OutGoingCallDialog(EventSelectionActivity.this, eventList.get(2), adapter);
        AlertDialog dialog = obj.create();
        dialog.show();
        View v = obj.getView();
        TextView textView = (TextView) v.findViewById(R.id.event_call_dialog_textView);
        textView.setText(number);
        adapter.data[2][0]=number;
    }

    @Override
    public void onDataPass(int data1,int data2) {
        Toast.makeText(this, data1+""+data2, Toast.LENGTH_SHORT).show();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity_main);
        setToolbar();
        initCollapsingToolbar();
        //initiate hasmap;
        inflater = LayoutInflater.from(this);
        viewRoot = inflater.inflate(R.layout.message_dialog_layout, null);
        hm=new HashMap<>();

        Intent intent=getIntent();
        title=intent.getStringExtra("TITLE");

        recyclerView = (RecyclerView) findViewById(R.id.event_recycler_view);

        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareEvents();

        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.event_backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * fonction for setting toolbar
     */
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.event_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Events");
        toolbar.setTitleTextColor(getResources().getColor(R.color.iconsTint));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.iconsTint));
    }
    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.event_collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.event_appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Adding few albums for testing
     */
    private void prepareEvents() {
        int[] covers = new int[]{
                R.drawable.time,
                R.drawable.incoming,
                R.drawable.outgoing,
                R.drawable.location,
                R.drawable.headset,
                R.drawable.bluetooth,
                R.drawable.battery,
                R.drawable.power
        };

        Event a = new Event("Time", covers[0]);
        eventList.add(a);

        a = new Event("Incoming Call", covers[1]);
        eventList.add(a);

        a = new Event("Outgoing Call", covers[2]);
        eventList.add(a);

        a = new Event("Location",  covers[3]);
        eventList.add(a);

        a = new Event("HeadSet",  covers[4]);
        eventList.add(a);

        a = new Event("Bluetooth",  covers[5]);
        eventList.add(a);

        a = new Event("Battery",  covers[6]);
        eventList.add(a);

        a = new Event("Power",  covers[7]);
        eventList.add(a);

        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.check_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.check:
                if(atLeastOneEventSelect()) {
                    Intent intent = new Intent(EventSelectionActivity.this, ActionSelectionActivity.class);
                    intent.putExtra("TASK_TITLE", title);
                    intent.putExtra("EVENT_NAME", event);
                    intent.putExtra("E0", a0);
                    intent.putExtra("E1", a1);
                    intent.putExtra("E2", a2);
                    startActivity(intent);
                }else {
                    new AlertDialog.Builder(EventSelectionActivity.this)
                            .setTitle("Error")
                            .setMessage("Please select at least one Event")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                break;
            default:
                break;
        }

        return true;
    }

    private boolean atLeastOneEventSelect() {
        for(int i = 0; i < eventList.size(); ++i) {
            if(eventList.get(i).getSelected()){
                event=eventList.get(i).getName();
                //Toast.makeText(this, ""+eventList.get(i).getName(), Toast.LENGTH_LONG).show();
                if(eventList.get(i).getName().equals("Time")){
                    //Toast.makeText(this, ""+adapter.data[0][1], Toast.LENGTH_LONG).show();
                    a1=""+adapter.data[0][1];
                }else if(eventList.get(i).getName().equals("Incoming Call")){
                    //Toast.makeText(this, ""+adapter.data[1][0], Toast.LENGTH_LONG).show();
                    a0=""+adapter.data[1][0];
                }else if(eventList.get(i).getName().equals("Outgoing Call")){
                    //Toast.makeText(this, ""+adapter.data[2][0], Toast.LENGTH_LONG).show();
                    a0=""+adapter.data[2][0];
                }else if(eventList.get(i).getName().equals("Location")){
                    //Toast.makeText(this, ""+adapter.data[3][0]+" "+adapter.data[3][1], Toast.LENGTH_LONG).show();
                    a0=""+adapter.data[3][0];
                    a1=""+adapter.data[3][1];
                }else if(eventList.get(i).getName().equals("HeadSet")){
                    //Toast.makeText(this, ""+adapter.data[4][0], Toast.LENGTH_LONG).show();
                    a0=""+adapter.data[4][0];
                }else if(eventList.get(i).getName().equals("Bluetooth")){
                    //Toast.makeText(this, ""+adapter.data[5][0], Toast.LENGTH_LONG).show();
                    a0=""+adapter.data[5][0];
                }else if(eventList.get(i).getName().equals("Battery")){
                    //Toast.makeText(this, ""+adapter.data[6][0], Toast.LENGTH_LONG).show();
                    a0=""+adapter.data[6][0];
                }else if(eventList.get(i).getName().equals("Power")){
                    //Toast.makeText(this, ""+adapter.data[7][0], Toast.LENGTH_LONG).show();
                    a0=""+adapter.data[7][0];
                }
                return true;

            }
        }
        return false;
    }

}