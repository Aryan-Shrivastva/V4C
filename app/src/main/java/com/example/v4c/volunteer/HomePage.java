package com.example.v4c.volunteer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.v4c.R;
import com.example.v4c.SignUp_As_Page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LinearLayout volunteer, events, rewards, explore;
    ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        volunteer = findViewById(R.id.btnVolunteer);
        events = findViewById(R.id.btnEvents);
        rewards = findViewById(R.id.btnRewards);
        explore = findViewById(R.id.btnExplore);
        menuIcon = findViewById(R.id.menu);


        menuIcon.setOnClickListener(view -> showPopupMenu(view));

        events.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, EventListing.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        explore.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, Explore.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> userData = documentSnapshot.getData();
            // Use userData if needed
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createNotificationChannel();

        RecyclerView recyclerView = findViewById(R.id.communityRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<CommunityModel> communityList = new ArrayList<>();
        CommunityAdapter adapter = new CommunityAdapter(this, communityList);
        recyclerView.setAdapter(adapter);

        try {
            db.collection("NGO").get().addOnSuccessListener(queryDocumentSnapshots -> {
                try {
                    communityList.clear();
                    for (var document : queryDocumentSnapshots.getDocuments()) {
                        CommunityModel model = document.toObject(CommunityModel.class);
                        communityList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        RecyclerView eventRecycler = findViewById(R.id.eventsRecycler);
        eventRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<EventModel> eventList = new ArrayList<>();
        EventAdapter eventAdapter = new EventAdapter(this, eventList);
        eventRecycler.setAdapter(eventAdapter);

        db.collection("events").get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();
            for (var doc : queryDocumentSnapshots.getDocuments()) {
                EventModel model = doc.toObject(EventModel.class);
                if (model != null) {
                    Log.d("EVENT_DEBUG", "Title: " + model.getTitle());
                    eventList.add(model);
                }
            }
            eventAdapter.notifyDataSetChanged();
        }).addOnFailureListener(Throwable::printStackTrace);
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(HomePage.this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.pop, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.profile) {
                // Handle profile logic here
                return true;
            } else if (item.getItemId() == R.id.signOut) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomePage.this, SignUp_As_Page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "My Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.v4c_logo)
                .setContentTitle("Task Complete!")
                .setContentText("Your task is done successfully.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}
