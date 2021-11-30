package edu.temple.projectblz;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParkingItemsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    ParkingAdapter parkingAdapter;
    ArrayList<LocationObject> listItem = new ArrayList<>();
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_items);

        setTitle("Parking Location History");

        recyclerView = findViewById(R.id.parkingItemsRecyclerView);
        imageView = findViewById(R.id.imageView);

        /**Our Location Object list - gets its data from an intent, which got the items from mainactivity - getArraylist function*/
        listItem = (ArrayList<LocationObject>) getIntent().getSerializableExtra(Constant.LOCATIONLIST);
        setAdapter();


        /**this button closes the history of parking list*/
        findViewById(R.id.closeItemBtn).setOnClickListener(v -> {
            startActivity(new Intent(ParkingItemsActivity.this, MainActivity.class));
            finish();
        });
    }


    private void setAdapter() {
        parkingAdapter = new ParkingAdapter(this, listItem);
        RecyclerView.LayoutManager layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(parkingAdapter);
    }


}
