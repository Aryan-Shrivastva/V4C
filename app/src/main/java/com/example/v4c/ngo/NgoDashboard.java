package com.example.v4c.ngo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Color;

import com.example.v4c.R;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

public class NgoDashboard extends AppCompatActivity {

    private PieChart chart;
    private ValueLineChart LineChart;
    private BarChart BarChart;
    private int i1 = 15;
    private int i2 = 40;
    private int i3 = 20;

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



        chart = findViewById(R.id.pie_chart);
        addToPieChart();

        BarChart = findViewById(R.id.barChart);
        addToBarChart();

        LineChart = findViewById(R.id.lineChart);
        addToLineChart();
    }

    private void addToPieChart() {
        chart.addPieSlice(new PieModel( i1, Color.parseColor("#FFFFFF")));
        chart.addPieSlice(new PieModel(i2, Color.parseColor("#BA8DF3")));
        chart.addPieSlice(new PieModel(i3, Color.parseColor("#F8D6C0")));

        chart.startAnimation();
    }

    private void addToBarChart(){
        BarChart.addBar(new BarModel("2023",3.0f, Color.parseColor("#DB3D3D")));
        BarChart.addBar(new BarModel("2023",5.0f, Color.parseColor("#F5D6C0")));
        BarChart.addBar(new BarModel("2024",6.0f, Color.parseColor("#DB3D3D")));
        BarChart.addBar(new BarModel("2024",4.0f, Color.parseColor("#F5D6C0")));

        BarChart.startAnimation();
    }

    private void addToLineChart(){
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