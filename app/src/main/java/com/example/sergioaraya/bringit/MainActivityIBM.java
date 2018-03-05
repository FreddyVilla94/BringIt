package com.example.sergioaraya.bringit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sergioaraya.bringit.Classes.Constants;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Freddy on 27/2/2018.
 */

public class MainActivityIBM extends AppCompatActivity {

    // UI Components
    private Spinner spinLang;
    private TextView statusTextView;
    private Button recordButton;
    private TextView selectedText;
    private ListView resultList;
    private Button clearButton;

    // Miscellaneous Components
    private MicrophoneInputStream capture;
    private SpeechToText speechService;

    private boolean listening = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ibm);
        requestRecordAudioPermission();
        // Initialize UI Components
        spinLang = (Spinner) findViewById(R.id.spinLang);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        recordButton = (Button) findViewById(R.id.recordButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        selectedText = (TextView) findViewById(R.id.selectedText);
        resultList = (ListView) findViewById(R.id.resultList);

        // Initialize Miscellaneous Components
        speechService = initSpeechToTextService();
        // For language chooser check strings.xml for language array
        ArrayAdapter adapterForSpin = ArrayAdapter.createFromResource(this,
                R.array.supported_languajes, android.R.layout.simple_spinner_item);
        spinLang.setAdapter(adapterForSpin);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    if (!listening){
                        resetUIState();
                        startRecognition();
                        listening = true;
                    } else {
                        try {
                            capture.close();
                            listening = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedText.setText("");
                resultList.setAdapter(null);
            }
        });
    }

    private void startRecognition(){
        capture = new MicrophoneInputStream(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    speechService.recognizeUsingWebSocket(capture,
                            getRecognizeOptions(), new MicrophoneRecognizeDelegate());
                }catch (Exception e){
                    showError(e);
                }
            }
        }).start();
    }

    private void showError (final Exception e){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivityIBM.this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }
    /**
     * Method for setting up user authentication for the connection service
     * More info at: https://goo.gl/V7JXox
     * * @return Configured service
     */
    private SpeechToText initSpeechToTextService(){
        SpeechToText service = new SpeechToText();
        String username = Constants.APP_USERNAME;
        String password = Constants.APP_PASSWORD;
        service.setEndPoint(Constants.APP_ENDPOINT);
        service.setUsernameAndPassword(username, password);
        return service;
    }

    private RecognizeOptions getRecognizeOptions(){
        return new RecognizeOptions.Builder()
                .continuous(true)
                .contentType(ContentType.OPUS.toString())
                .model(getLanguage())
                .interimResults(true)
                .inactivityTimeout(2000)
                .build();
    }

    /**
     * Helper method for language chooser. Abstracts real name language models
     in IBM Watson API
     * All of the languages of this app use BroadbandModel. * More info at https://goo.gl/M2r0cy
     * @return chosen language
     */
    private String getLanguage(){
        String selectedItem = spinLang.getSelectedItem().toString();
        switch (selectedItem){
            case "Modern Standard Arabic":
                selectedItem = "ar-AR_BroadbandModel";
                break;
            case "UK English":
                selectedItem = "en-UK_BroadbandModel";
                break;
            case "US English":
                selectedItem =  "en-US_BroadbandModel";
                break;
            case "Spanish":
                selectedItem =  "es-ES_BroadbandModel";
                break;
            case "French":
                selectedItem =  "fr-FR_BroadbandModel";
                break;
            case "Japanese":
                selectedItem = "ja-JP_BroadbandModel";
                break;
            case "Brazilian":
                selectedItem = "pt-BR_BroadbandModel";
                break;
            case "Mandarin":
                selectedItem = "zh-CN_BroadbandModel";
                break;
        }
        return selectedItem;
    }


    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable() && net.isConnected()){
            return  true;
        }else {
            return false;
        }

    }

    /**
     * Delegate for managing the response from Watson API. * More info at: https://goo.gl/ZcRiok
     */
    private class MicrophoneRecognizeDelegate implements RecognizeCallback {

        @Override
        public void onTranscription(final SpeechResults speechResults) {
            final List<Transcript> totalResults = speechResults.getResults();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getApplicationContext(),totalResults.toString(),Toast.LENGTH_LONG).show();
                    showResponseFromWatsonService(totalResults);
                }
            });
        }

        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusTextView.setText("Listing ...");
                    // Toggle the text on our record button to indicate pressing it now will abort the search.
                    recordButton.setText("Stop Recording");
                }
            });
        }

        @Override
        public void onError(Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusTextView.setText("Failed.");
                }
            });
        }

        @Override
        public void onDisconnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusTextView.setText("Done.");
                    resetUIState();
                }
            });
        }
    }

    /**
     * Method for reset ui elements
     */
    private void resetUIState(){
        statusTextView.setText("");
        recordButton.setText("Start Recording");
    }

    /**
     * Method used to show up the result in the List View * @param response response from the server
     */
    private void showResponseFromWatsonService (List<Transcript> response){
        final ArrayList<String> finalResultList = new ArrayList<String>();
        String tempTranscript = null;
        Double tempConfidence = null;
        for (int i=0; i<response.size();i++){
            for (int j=0; j< response.get(i).getAlternatives().size();j++){
                tempTranscript = response.get(i).getAlternatives().get(j).getTranscript();
                tempConfidence = response.get(i).getAlternatives().get(j).getConfidence();
                finalResultList.add(tempTranscript + "Confidence: " + tempConfidence);
            }
        }
        //Toast.makeText(getApplicationContext(),"Texto: "+ tempTranscript,Toast.LENGTH_SHORT).show();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalResultList);
        resultList.setAdapter(adapter);
        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String newString = finalResultList.get(position).substring(
                        0,finalResultList.get(position).indexOf("Confidence"));
                selectedText.setText(selectedText.getText() +" "+ newString);

            }
        });
    }
    private void requestRecordAudioPermission() {
        //check API version, do nothing if API version < 23!
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Activity", "Granted!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Activity", "Denied!");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
