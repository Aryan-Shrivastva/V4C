package com.example.v4c.ngo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.v4c.CreateEvent;
import com.example.v4c.R;
import com.example.v4c.SignUp_As_Page;
import com.example.v4c.volunteer.EventAdapter;
import com.example.v4c.volunteer.EventModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.List;

public class NgoDashboard extends AppCompatActivity {

    private PieChart chart;
    private ValueLineChart LineChart;
    private BarChart BarChart;
    private int i1 = 15;
    private int i2 = 40;
    private int i3 = 20;
    private FloatingActionButton addNewEvent;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ngo_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        menuIcon = findViewById(R.id.menu);
        menuIcon.setOnClickListener(this::showPopupMenu);

        addNewEvent = findViewById(R.id.addEventButton);
        addNewEvent.setOnClickListener(view -> {
            Intent intent = new Intent(NgoDashboard.this, CreateEvent.class);
            startActivity(intent);
        });

        chart = findViewById(R.id.pie_chart);
        addToPieChart();

        BarChart = findViewById(R.id.barChart);
        addToBarChart();

        LineChart = findViewById(R.id.lineChart);
        addToLineChart();

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
        PopupMenu popupMenu = new PopupMenu(NgoDashboard.this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.pop
                , popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.profile) {
                Intent intent = new Intent(NgoDashboard.this, SignUp_As_Page.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.signOut) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(NgoDashboard.this, SignUp_As_Page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void addToPieChart() {
        chart.addPieSlice(new PieModel(i1, Color.parseColor("#FFFFFF")));
        chart.addPieSlice(new PieModel(i2, Color.parseColor("#BA8DF3")));
        chart.addPieSlice(new PieModel(i3, Color.parseColor("#F8D6C0")));
        chart.startAnimation();
    }

    private void addToBarChart() {
        BarChart.addBar(new BarModel("2023", 3.0f, Color.parseColor("#DB3D3D")));
        BarChart.addBar(new BarModel("2023", 5.0f, Color.parseColor("#F5D6C0")));
        BarChart.addBar(new BarModel("2024", 6.0f, Color.parseColor("#DB3D3D")));
        BarChart.addBar(new BarModel("2024", 4.0f, Color.parseColor("#F5D6C0")));
        BarChart.startAnimation();
    }

    private void addToLineChart() {
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(Color.parseColor("#000000"));
        series.addPoint(new ValueLinePoint("Jan", 20f));
        series.addPoint(new ValueLinePoint("Feb", 30f));
        series.addPoint(new ValueLinePoint("Mar", 10f));
        series.addPoint(new ValueLinePoint("Apr", 40f));
        series.addPoint(new ValueLinePoint("May", 25f));
        LineChart.addSeries(series);
        LineChart.startAnimation();
    }
}
