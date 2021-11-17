package edu.temple.projectblz;

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

    ArrayList<LocationObject> listItem = new ArrayList<>();
    ListView listView;
    BaseAdapter adapter;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_items);
        setTitle("Parking History");
        listView = findViewById(R.id.listView);
        imageView = findViewById(R.id.imageView);

        listItem = (ArrayList<LocationObject>) getIntent().getSerializableExtra(Constant.LOCATIONLIST);

        adapter = new ParkingAdapter(this, listItem);
        listView.setAdapter(adapter);

        /**this handles the click of the item in the list view*/
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String addressReturned = null;

            /**get the current lat and lon for the item clicked on*/
            double latitude = Double.valueOf(listItem.get(position).getPark_lat());
            double longitude = Double.valueOf(listItem.get(position).getPark_lon());

            /**call showAddress to convert the lat lon to geolocations, using geocoder*/
            try {
               addressReturned = showAddress(latitude, longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /**confirm if the driver wants to navigate to the address found in the list*/
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_directions)
                    .setTitle(addressReturned)
                    .setMessage("Do you want to navigate to this address?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                             Intent intent = new Intent(Intent.ACTION_VIEW,
                             Uri.parse(Constant.GOOGLE_MAP_URL + latitude + "," + longitude));
                             startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

            //ParkingItemsActivity.this.finish(); - I am not sure if we should finish here - leave like this for now
        });

        findViewById(R.id.closeItemBtn).setOnClickListener(v -> {
            startActivity(new Intent(ParkingItemsActivity.this, MainActivity.class));
            finish();
        });

    }

    /**this function gets the actual address form the lat and lon coordinates*/
    private String showAddress(double lat, double lon) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        String address = null;


        /**try catch  - to prevent null exceptions*/
        try {
            /** Here 1 represent max location result to returned, by documents it recommended 1 to 5*/
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        /**double check if address is empty*/
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "Sorry no address found ", Toast.LENGTH_SHORT).show();
        }
        else{
            /** If any additional address line present than only 1, check with max available address lines by getMaxAddressLineIndex()*/
            address = addresses.get(0).getAddressLine(0);
        }
        return address;
    }
}