package com.example.ycfelix.myapplication;

import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.speech.RecognizerIntent;
import java.util.ArrayList;
import java.util.Locale;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.language.v1.CloudNaturalLanguage;
import com.google.api.services.language.v1.CloudNaturalLanguageRequestInitializer;
import com.google.api.services.language.v1.model.AnnotateTextRequest;
import com.google.api.services.language.v1.model.AnnotateTextResponse;
import com.google.api.services.language.v1.model.Document;
import com.google.api.services.language.v1.model.Features;
import com.google.api.services.language.v1.model.Sentiment;
import com.projects.alshell.vokaturi.EmotionProbabilities;
import com.projects.alshell.vokaturi.Vokaturi;
import com.projects.alshell.vokaturi.VokaturiException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 5;

    private ProgressBar progressBarNeutrality;
    private ProgressBar progressBarHappiness;
    private ProgressBar progressBarSadness;
    private ProgressBar progressBarAnger;
    private ProgressBar progressBarFear;

    private TextView textViewNeutrality;
    private TextView textViewHappiness;
    private TextView textViewSadness;
    private TextView textViewAnger;
    private TextView textViewFear;

    private TextView actionStatus;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private Vokaturi vokaturiApi=null;


    private ProgressBar progresssentiment;

    //please use your own api key
    public static final String API_KEY = "AIzaSyCWDasfakslfasgaKmTMTTTwdxvACJc ";
    private CloudNaturalLanguage naturalLanguageService;
    private Document document;

    private Features features;

    private FloatingActionButton textinputer;

    private EditText emotioninput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mVoiceInputTv=findViewById(R.id.speechresult);


                try {
            vokaturiApi = Vokaturi.getInstance(MainActivity.this);
            Toast.makeText(MainActivity.this, "successful created!", Toast.LENGTH_SHORT).show();
        } catch (VokaturiException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "fail!", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);

        initializeViews();

        setListeners();

        naturalLanguageService = new CloudNaturalLanguage.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null
        ).setCloudNaturalLanguageRequestInitializer(
                new CloudNaturalLanguageRequestInitializer(API_KEY)
        ).build();


        document = new Document();
        document.setType("PLAIN_TEXT");
        document.setLanguage("en-US");

        features = new Features();
        features.setExtractEntities(true);
        features.setExtractSyntax(true);
        features.setExtractDocumentSentiment(true);

        final AnnotateTextRequest request = new AnnotateTextRequest();
        request.setDocument(document);
        request.setFeatures(features);

    }
    private void initializeViews() {

        progresssentiment=findViewById(R.id.sentiment);
        progressBarNeutrality = (ProgressBar) findViewById(R.id.progressBarNeutrality);
        progressBarHappiness = (ProgressBar) findViewById(R.id.progressBarHappiness);
        progressBarSadness = (ProgressBar) findViewById(R.id.progressBarSadness);
        progressBarAnger = (ProgressBar) findViewById(R.id.progressBarAnger);
        progressBarFear = (ProgressBar) findViewById(R.id.progressBarFear);

        textViewNeutrality = (TextView) findViewById(R.id.textViewNeutrality);
        textViewHappiness = (TextView) findViewById(R.id.textViewHappiness);
        textViewSadness = (TextView) findViewById(R.id.textViewSadness);
        textViewAnger = (TextView) findViewById(R.id.textViewAnger);
        textViewFear = (TextView) findViewById(R.id.textViewFear);
        actionStatus = (TextView) findViewById(R.id.actionStatus);
        textinputer= findViewById(R.id.textinputer);
        emotioninput=findViewById(R.id.textinput);
    }

    private void setListeners() {
        FloatingActionButton myFab = (FloatingActionButton)findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                    startVoiceInput();
                    startListening();
                    Toast.makeText(v.getContext(), "hi please say sth", Toast.LENGTH_LONG).show();

            }
        });

    textinputer.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {

            final AnnotateTextRequest request = new AnnotateTextRequest();
            Toast.makeText(v.getContext(), "Detecting direction text..", Toast.LENGTH_SHORT).show();
            //sentiment recognition using google api
            document.setContent(emotioninput.getText().toString());
            request.setDocument(document);
            request.setFeatures(features);

            new AsyncTask<Object, Void, AnnotateTextResponse>() {
                @Override
                protected AnnotateTextResponse doInBackground(Object... params) {
                    AnnotateTextResponse response = null;
                    try {
                        response = naturalLanguageService.documents().annotateText(request).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return response;
                }

                @Override
                protected void onPostExecute(AnnotateTextResponse response) {
                    super.onPostExecute(response);
                    if (response != null) {
                        Sentiment sent = response.getDocumentSentiment();
                        progresssentiment.setProgress((int)(sent.getScore()*100));
                    }
                }
            }.execute();

            Toast.makeText(v.getContext(), "done detection", Toast.LENGTH_SHORT).show();
        }
    });

    }

    private void startVoiceInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
               "enter your voice...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(result.get(0));
                    stopListening();
                    Toast.makeText(this.getApplicationContext(), "detecting!", Toast.LENGTH_LONG).show();


                    //use google api to recognize sentiment
                    final AnnotateTextRequest request = new AnnotateTextRequest();
                    document.setContent(mVoiceInputTv.getText().toString());
                    request.setDocument(document);
                    request.setFeatures(features);

                    new AsyncTask<Object, Void, AnnotateTextResponse>() {
                        @Override
                        protected AnnotateTextResponse doInBackground(Object... params) {
                            AnnotateTextResponse response = null;
                            try {
                                response = naturalLanguageService.documents().annotateText(request).execute();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return response;
                        }

                        @Override
                        protected void onPostExecute(AnnotateTextResponse response) {
                            super.onPostExecute(response);
                            if (response != null) {
                                Sentiment sent = response.getDocumentSentiment();
                                progresssentiment.setProgress((int)(sent.getScore()*100));
                            }
                        }
                    }.execute();

                }
                else if(resultCode == RESULT_OK)
                {
                    stopListening();
                }
                break;
            }

        }
    }


    private void startListening() {
        if (vokaturiApi != null) {
            try {
                //ui event
                setListeningUI();
                //call api
                vokaturiApi.startListeningForSpeech();
            } catch (VokaturiException e) {
                setNotListeningUI();
                if (e.getErrorCode() == VokaturiException.VOKATURI_DENIED_PERMISSIONS) {
                    Toast.makeText(this, "Grant Microphone and Storage permissions in the app settings to proceed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "There was some problem to start listening audio", Toast.LENGTH_SHORT).show();
                }
            } catch (IllegalStateException e) {
                setNotListeningUI();
                e.printStackTrace();
            }
        }
    }

    private void setListeningUI() {
        actionStatus.setText("Press again to stop listening and analyze emotions");
        progresssentiment.setIndeterminate(true);
        progressBarNeutrality.setIndeterminate(true);
        progressBarHappiness.setIndeterminate(true);
        progressBarSadness.setIndeterminate(true);
        progressBarAnger.setIndeterminate(true);
        progressBarFear.setIndeterminate(true);
    }


    private void stopListening() {
        if (vokaturiApi != null) {
            setNotListeningUI();

            try {
                showMetrics(vokaturiApi.stopListeningAndAnalyze());
            } catch (VokaturiException e) {
                if (e.getErrorCode() == VokaturiException.VOKATURI_NOT_ENOUGH_SONORANCY) {
                    Toast.makeText(this, "Please speak a more clear and avoid noise around your environment", Toast.LENGTH_LONG).show();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void setNotListeningUI() {
        actionStatus.setText("Press below button to start listening");
        progressBarNeutrality.setIndeterminate(false);
        progressBarHappiness.setIndeterminate(false);
        progressBarSadness.setIndeterminate(false);
        progressBarAnger.setIndeterminate(false);
        progressBarFear.setIndeterminate(false);
        progresssentiment.setIndeterminate(false);
    }


    //display result
    private void showMetrics(EmotionProbabilities emotionProbabilities) {
        emotionProbabilities.scaledValues(5);
        processData(emotionProbabilities);
        textViewNeutrality.setText("Neutrality: " + emotionProbabilities.Neutrality);
        textViewHappiness.setText("Happiness: " + emotionProbabilities.Happiness);
        textViewSadness.setText("Sadness: " + emotionProbabilities.Sadness);
        textViewAnger.setText("Anger: " + emotionProbabilities.Anger);
        textViewFear.setText("Fear: " + emotionProbabilities.Fear);

        progressBarNeutrality.setProgress(normalizeForProgressBar(emotionProbabilities.Neutrality));
        progressBarHappiness.setProgress(normalizeForProgressBar(emotionProbabilities.Happiness));
        progressBarSadness.setProgress(normalizeForProgressBar(emotionProbabilities.Sadness));
        progressBarAnger.setProgress(normalizeForProgressBar(emotionProbabilities.Anger));
        progressBarFear.setProgress(normalizeForProgressBar(emotionProbabilities.Fear));

    }
    private void processData(EmotionProbabilities emotionProbabilities)
    {
       //attempts to precess your data

    }

    //multiply the result to range 0 to 100
    private int normalizeForProgressBar(double val) {
        if (val < 1) {
            return (int) (val * 100);
        } else {
            return (int) (val * 10);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
