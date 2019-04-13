package freeriders.emotionrecognizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ShowResultActivity extends AppCompatActivity {

    PieChart pieChart ;
    ArrayList<PieEntry> entries ;
    ArrayList<String> PieEntryLabels ;
    PieDataSet pieDataSet ;
    PieData pieData ;
    float results[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        initialise();

        entries = new ArrayList<>();
        PieEntryLabels = new ArrayList<String>();

        AddValuesToPIEENTRY();
        AddValuesToPieEntryLabels();

        pieDataSet = new PieDataSet((ArrayList<PieEntry>)entries, "");
        pieData = new PieData(pieDataSet);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(pieData);
        pieChart.animateY(3000);

    }

    protected void initialise(){
        results = getIntent().getExtras().getFloatArray("results");
        pieChart = (PieChart) findViewById(R.id.chart1);
    }

    public void AddValuesToPIEENTRY(){

        entries.add(new PieEntry(results[0]*100, 1));
        entries.add(new PieEntry(results[1]*100, 2));
        entries.add(new PieEntry(results[2]*100, 3));
        entries.add(new PieEntry(results[3]*100, 4));
        entries.add(new PieEntry(results[4]*100, 5));
        entries.add(new PieEntry(results[5]*100, 6));

    }

    public void AddValuesToPieEntryLabels(){

        PieEntryLabels.add("Smile");
        PieEntryLabels.add("Sadness");
        PieEntryLabels.add("Disgust");
        PieEntryLabels.add("Contempt");
        PieEntryLabels.add("Fear");
        PieEntryLabels.add("Surprise");

    }
}
