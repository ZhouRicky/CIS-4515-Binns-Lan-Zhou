package edu.temple.projectblz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> itemsList;

    public ParkingAdapter(Context context, ArrayList<String> mList) {
        this.context = context;
        itemsList = mList;

        itemsList.add("user1");
        itemsList.add("user2");
        itemsList.add("user3");
        itemsList.add("user4");
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTextView;
        private ImageButton deleteButton;

        public MyViewHolder(final View view) {
            super(view);

            timeTextView = view.findViewById(R.id.timeTextView);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }

    @NonNull
    @Override
    public ParkingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View placeholderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_items, parent, false);
        return new MyViewHolder(placeholderView);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingAdapter.MyViewHolder holder, int position) {
        int index = position;
        String date = itemsList.get(position);
        holder.timeTextView.setText(date);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete Parking Location")
                        .setMessage("Do you want to delete this location?")
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                itemsList.remove(index);
                                notifyDataSetChanged();
                            }
                        })
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}

