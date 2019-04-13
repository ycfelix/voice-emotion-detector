package freeriders.emotionrecognizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button realTimeDetectionOptionButton;
    Button imageDetectionOptionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();


        //real time option button takes to the face detection activity
        realTimeDetectionOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FaceDetectionActivity.class);
                startActivity(intent);    //to start the activity specified by the Intent"
            }
        });

        //image detection option button takes to image detection activity
        imageDetectionOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(getApplicationContext(), ImageDetection.class);
                startActivity(gallery);
            }
        });

    }

    // initialising mode buttons, for the two modes of emotion detection
    protected void initialise(){
        realTimeDetectionOptionButton = (Button) findViewById(R.id.realTimeDetectionOptionButton);
        imageDetectionOptionButton = (Button) findViewById(R.id.imageDetectionOptionButton);
    }


}
