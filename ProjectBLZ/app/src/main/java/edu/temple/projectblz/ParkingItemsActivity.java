package edu.temple.projectblz;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;

import java.util.ArrayList;

public class ParkingItemsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ParkingAdapter parkingAdapter;
    private ArrayList<LocationObject> listItem = new ArrayList<>();
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_items);

        setTitle("Parking Location History");

        recyclerView = findViewById(R.id.parkingItemsRecyclerView);
        imageView = findViewById(R.id.imageView);

        /**Our Location Object list - gets its data from an intent, which got the items from MainActivity - getArraylist function*/
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

