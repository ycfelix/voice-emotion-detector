package com.emotion.emotion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by AS V5-573G on 2017/5/28.
 */

public class SLPost extends AsyncTask<String, String, Integer> {
    private static final int TIME_OUT = 1000;
    private static final String TAG_SUCCESS = "success";
    private static final String url_SL = "http://example.com/emotion/SL_manage.py";
    static String json = "";

    private JSONObject jsonObj;
    private String[][] data;
    private String resp;
    private Activity activity;
    private TextToSpeech tts;
    private ProgressDialog pDialog;

    public SLPost(Activity a, TextToSpeech tts){
        activity = a;
        this.tts = tts;
    }

    // loading...
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
    }

    // running...
    protected Integer doInBackground(String... args) {
        String postStr = args[0];

        // Building Parameters
        try{
            String up = "str=" + URLEncoder.encode(postStr, "UTF-8");
            URL url = new URL(url_SL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(up.getBytes().length));
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(up);
            wr.flush();
            wr.close();

            // 讀取資料
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            json = sb.toString();
            //Log.d("JSON", "J: " + json);
            reader.close();

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (json.equals("")) {
                Thread.sleep(1000);
            }
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            Log.e("JSON POST", "Error POST data " + e.toString());
            return -1;
        }

        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        try {
            return jsonObj.getInt(TAG_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // finish!
    protected void onPostExecute(Integer success) {
        switch (success){
            case 0:
                Toast.makeText(activity.getApplicationContext(), "操作失敗，請聯絡管理員", Toast.LENGTH_LONG).show();
                break;
            case 1:
                //Log.d("EmotionApp","Load successfully");
                //JSONArray jsonArray;
                try {
                    resp = jsonObj.getString("message");
                    int emotion_val = jsonObj.getInt("emotion_val");

                    String[] emotions = activity.getResources().getStringArray(R.array.emotions);
                    ImageView ivResp = (ImageView) activity.findViewById(R.id.ivResp);
                    TextView tv = (TextView) activity.findViewById(R.id.tvResp);

                    switch (emotion_val){
                        case 0:
                            tv.setText(emotions[emotion_val]);
                            break;
                        case 1:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 2:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 3:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 4:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 5:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 6:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 7:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 8:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 9:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 10:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 11:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 12:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 13:
                            tv.setText(emotions[emotion_val]);

                            break;
                        case 14:
                            tv.setText(emotions[emotion_val]);

                            break;
                        default:
                            tv.setText(R.string.tv_empty);
                            ivResp.setImageResource(R.mipmap.ic_launcher);
                            break;
                    }
                    Toast.makeText(activity.getApplicationContext(), resp, Toast.LENGTH_LONG).show();
                    tts.speak(resp, TextToSpeech.QUEUE_FLUSH, null, "TW-zh");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                Log.d("EmotionApp", "NULL");
                break;
        }
        pDialog.dismiss();
    }
}
