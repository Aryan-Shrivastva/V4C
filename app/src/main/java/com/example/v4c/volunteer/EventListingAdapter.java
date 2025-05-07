package com.example.v4c.volunteer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.v4c.R;
import com.example.v4c.volunteer.EventDetailActivity;
import com.example.v4c.volunteer.EventModel;
import com.google.gson.Gson;

import java.util.List;

public class EventListingAdapter extends RecyclerView.Adapter<EventListingAdapter.EventViewHolder> {

    Context context;
    List<EventModel> eventList;

    public EventListingAdapter(Context context, List<EventModel> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventModel event = eventList.get(position);


        holder.title.setText(event.getTitle());
        holder.date.setText(formatDate(event.getDate()));
        holder.description.setText(formatDate(event.getDescription()));

        holder.location.setText(event.getLoc());
        Glide.with(context).load(event.getImageUrl()).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("event", new Gson().toJson(event));
            context.startActivity(intent);

            Log.d("EventAdapter", "Raw date: " + event.getDate());

            Log.d("EventAdapter", "Date " + formatDate(event.getDate()));
            //animation
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, date,  location, description;
        ImageView image;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            date = itemView.findViewById(R.id.event_date);
            location = itemView.findViewById(R.id.event_loc);
            image = itemView.findViewById(R.id.event_image);
            description = itemView.findViewById(R.id.event_description);
        }
    }

    private String formatDate(String dateStr) {
        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("dd-MM-yyyy");
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMMM d, yyyy");
            java.util.Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr; // fallback
        }
    }


}
