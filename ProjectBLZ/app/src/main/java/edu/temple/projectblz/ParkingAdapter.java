package edu.temple.projectblz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.MyViewHolder> {

    private Context context;
    public ArrayList<LocationObject> listItem;

    public ParkingAdapter(Context context, ArrayList<LocationObject> listItem) {
        this.context = context;
        this.listItem = listItem;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.row_items, null, true);
        return new MyViewHolder(row);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.parkTimeTextView.setText(listItem.get(position).getCreatedAt());
    }


    @Override
    public int getItemCount() {
        return listItem.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView parkTimeTextView;
        ImageView deleteImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            parkTimeTextView = itemView.findViewById(R.id.parkTimeTextView);
            deleteImageView = itemView.findViewById(R.id.deleteImageView);

            deleteImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Delete Parking Location")
                    .setMessage("Do you want to delete this location?")
                    .setNegativeButton("Yes", (dialog, which) -> {
                        // Delete item from db first, before removing from list
                        deletePark(listItem.get(position).getPark_id(), listItem.get(position).getDriver_id(), listItem.get(position).getCreatedAt());
                        listItem.remove(position);
                        notifyDataSetChanged();
                    })
                    .setPositiveButton("No", (dialog, which) -> dialog.cancel())
                    .show();
            });

            // this handles the click of the item in the list view
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                String addressReturned = null;

                // get the current lat and lon for the item clicked on
                double latitude = listItem.get(position).getPark_lat();
                double longitude = listItem.get(position).getPark_lon();

                // call showAddress to convert the lat lon to geolocations, using geocoder
                try {
                    addressReturned = showAddress(latitude, longitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // confirm if the driver wants to navigate to the address found in the list
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_menu_directions)
                        .setTitle(addressReturned)
                        .setMessage("Do you want to navigate to this address?")
                        .setNegativeButton("Yes", (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(Constant.GOOGLE_MAP_URL + latitude + "," + longitude));
                            context.startActivity(intent);
                        })
                        .setPositiveButton("No", (dialog, which) -> dialog.cancel())
                        .show();
            });
        }

        // this function deletes item from database - use with caution
        // *** USE WITH CAUTION ***
        private void deletePark(int park_id, int driverId, String createdAt) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.DELETE_URL,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("status").equals("success")) {
                                Toast.makeText(context, "Parking Location Deleted", Toast.LENGTH_SHORT).show();
                                Log.d("JSON", "success: " + "parking location deleted");
                            } else if(jsonObject.getString("status").equals("error")) {
                                Log.d("JSON", "error: " + jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("ParkingAdapter", String.valueOf(e));
                        }
                    },
                    error -> VolleyLog.d("Error", "Error: " + error.getMessage())) {
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Constant.PARK_ID, String.valueOf(park_id));
                    params.put(Constant.DRIVER_ID, String.valueOf(driverId));
                    params.put(Constant.CREATED_AT, createdAt);
                    return params;
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        }
    }


    // this function gets the actual address from the lat and lon coordinates
    private String showAddress(double lat, double lon) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        String address = null;

        // try catch  - to prevent null exceptions
        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // double check if address is empty
        if(addresses == null || addresses.size() == 0) {
            Toast.makeText(context, "Sorry, no address found", Toast.LENGTH_SHORT).show();
        } else {
            // If any additional address line present than only 1, check with max available address lines by getMaxAddressLineIndex()
            address = addresses.get(0).getAddressLine(0);
        }

        return address;
    }
}
