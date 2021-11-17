package edu.temple.projectblz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ParkingItemsActivity extends AppCompatActivity {

    private ArrayList<String> itemsList;
    private RecyclerView recyclerView;
    ParkingAdapter parkingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_items);
        setTitle("Parking Location History");

        recyclerView = findViewById(R.id.parkingItemsRecyclerView);
        itemsList = new ArrayList<>();

        setAdapter();

        findViewById(R.id.closeItemBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParkingItemsActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void setAdapter() {
        parkingAdapter = new ParkingAdapter(this, itemsList);
        RecyclerView.LayoutManager layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(parkingAdapter);
    }
}
