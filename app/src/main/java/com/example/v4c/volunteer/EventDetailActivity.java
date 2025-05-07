package com.example.v4c.volunteer;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.v4c.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView eventImage, orgImage;
    private TextView eventTitle, eventDateTime, eventDescription;
    private Button volunteerButton;
    private TextView orgName, orgLoc, orgDescription;
    private static final int CALENDAR_PERMISSION_REQUEST_CODE = 1001;
    private EventModel currentEvent;

    private static final String CHANNEL_ID = "event_channel";
    private static final int NOTIFICATION_ID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        createNotificationChannel();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventImage = findViewById(R.id.eventImage);
        eventTitle = findViewById(R.id.eventTitle);
        eventDateTime = findViewById(R.id.eventDateTime);
        eventDescription = findViewById(R.id.eventDescription);
        orgImage = findViewById(R.id.orgImage);
        orgName = findViewById(R.id.orgName);
        orgLoc = findViewById(R.id.orgLoc);
        orgDescription = findViewById(R.id.orgDescription);
        volunteerButton = findViewById(R.id.volunteerButton);

        String eventJson = getIntent().getStringExtra("event");
        currentEvent = new Gson().fromJson(eventJson, EventModel.class);

        if (currentEvent != null) {
            Glide.with(this).load(Uri.parse(currentEvent.getImageUrl())).into(eventImage);
            eventTitle.setText(currentEvent.getTitle());
            eventDateTime.setText(formatDate(currentEvent.getDate()) + " @ " + formatTime(currentEvent.getTime()));
            eventDescription.setText(currentEvent.getDescription());

            FirebaseFirestore.getInstance().collection("NGO")
                    .document(currentEvent.getOrganiserId())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            orgName.setText(doc.getString("name"));
                            orgLoc.setText(doc.getString("place"));
                            orgDescription.setText(doc.getString("about"));
                            Glide.with(this).load(doc.getString("sub_image")).into(orgImage);
                        }
                    });

            volunteerButton.setOnClickListener(v -> showAddEventDialog());
        }
    }

    private void showAddEventDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("How would you like to add this event?")
                .setCancelable(false)
                .setPositiveButton("Automatically", (dialog, id) -> {
                    // Manually open calendar to add event
                    addEventToCalendarAutomatically();
                })
                .setNegativeButton("Manually", (dialog, id) -> {
                    // Automatically add the event to calendar
                    addEventToCalendarManually();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void addEventToCalendarAutomatically() {
        if (currentEvent == null) return;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            Date eventDate = dateFormat.parse(currentEvent.getDate());
            Date eventTime = timeFormat.parse(currentEvent.getTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(eventDate);

            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(eventTime);

            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

            long startMillis = calendar.getTimeInMillis();
            calendar.add(Calendar.HOUR, 2);
            long endMillis = calendar.getTimeInMillis();

            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, currentEvent.getTitle());
            values.put(CalendarContract.Events.DESCRIPTION, currentEvent.getDescription());
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

            Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
            if (uri != null) {
                showEventAddedNotification(currentEvent.getTitle());
                Toast.makeText(this, "Event added to calendar", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show();
            }

        } catch (ParseException e) {
            Toast.makeText(this, "Error parsing date or time", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void addEventToCalendarManually() {
        if (currentEvent == null) return;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            Date eventDate = dateFormat.parse(currentEvent.getDate());
            Date eventTime = timeFormat.parse(currentEvent.getTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(eventDate);

            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(eventTime);

            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

            long startMillis = calendar.getTimeInMillis();
            calendar.add(Calendar.HOUR, 2);
            long endMillis = calendar.getTimeInMillis();

            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                    .putExtra(CalendarContract.Events.TITLE, currentEvent.getTitle())
                    .putExtra(CalendarContract.Events.DESCRIPTION, currentEvent.getDescription())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, orgLoc.getText().toString())
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    .putExtra(CalendarContract.Events.HAS_ALARM, true);

            startActivity(intent);

        } catch (ParseException e) {
            Toast.makeText(this, "Error parsing date or time", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Event Notifications";
            String description = "Notifications for volunteer events";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private void showEventAddedNotification(String eventTitle) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event", new Gson().toJson(currentEvent));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Event Added to Calendar")
                .setContentText("\"" + eventTitle + "\" has been added to your calendar")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build());
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1002);
            }
        } else {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addEventToCalendarAutomatically();
            } else {
                Toast.makeText(this, "Calendar permission is required to add events", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, yyyy");
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    private String formatTime(String timeStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a");
            Date date = inputFormat.parse(timeStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return timeStr;
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
