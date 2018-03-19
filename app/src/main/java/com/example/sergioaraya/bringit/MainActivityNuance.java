package com.example.sergioaraya.bringit;

/**
 * Created by Freddy on 19/3/2018.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.sergioaraya.bringit.Classes.Constants;
import com.nuance.speechkit.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivityNuance extends AppCompatActivity{

    /* State Logic: IDLE -> LISTENING -> PROCESSING -> repeat */
    private enum State {
        IDLE,
        LISTENING,
        PROCESSING
    }

    private Audio startEarcon;
    private Audio stopEarcon;
    private Audio errorEarcon;

    private RadioGroup detectionType;
    private ToggleButton progressiveResults;
    private Spinner spinLang;
    private ProgressBar volumeBar;
    private TextView selectedText, mostrar;
    private Button recordButton;
    private Button clearTextButton;
    private ListView resultList;
    private State state = State.IDLE;

    private Transaction recoTransaction;
    private Session speechSession;
    /* Audio Level Polling */
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nuance);

        detectionType = (RadioGroup)findViewById(R.id.detectionType);
        progressiveResults = (ToggleButton)findViewById(R.id.progressiveToggle);
        selectedText = (TextView)findViewById(R.id.selectedTextNuance);
        spinLang = (Spinner)findViewById(R.id.langSpinner);
        mostrar = (TextView)findViewById(R.id.mostrar);
        ArrayAdapter adapterForSpin = ArrayAdapter.createFromResource(this,R.array.supported_languajes,android.R.layout.simple_spinner_item);
        spinLang.setAdapter(adapterForSpin);
        volumeBar = (ProgressBar)findViewById(R.id.volumeBar);
        resultList = (ListView)findViewById(R.id.resultListNuance);
        recordButton = (Button) findViewById(R.id.recordButtonNuance);
        clearTextButton = (Button)findViewById(R.id.clearTextButtonNuance);

        speechSession = Session.Factory.session(this, Constants.SERVER_URI, Constants.APP_KEY);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toggleReco();
            }
        });
        clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultList.setAdapter(null);
                selectedText.setText("");
            }
        });

        loadEarcons();
    }

    /**
     * Helper method called from the record button on click listener, for check if user has intenet access
     * @return true if have internet access or false if don't
     */

    private boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /* Earcons */

    private void loadEarcons() {
        //Load all the earcons from disk
        startEarcon = new Audio(this, R.raw.sk_start, Constants.PCM_FORMAT);
        stopEarcon = new Audio(this, R.raw.sk_stop, Constants.PCM_FORMAT);
        errorEarcon = new Audio(this, R.raw.sk_error, Constants.PCM_FORMAT);
    }

    /* Record transactions */

    private void toggleReco() {
        switch (state) {
            case IDLE:
                recognize();
                break;
            case LISTENING:
                stopRecording();
                break;
            case PROCESSING:
                cancel();
                break;
        }
    }

    /**
     * Stop recording the user
     */
    private void stopRecording() {
        recoTransaction.stopRecording();
    }

    /**
     * Cancel the Reco transaction.
     * This will only cancel if we have not received a response from the server yet.
     */
    private void cancel() {
        recoTransaction.cancel();
        setState(State.IDLE);
    }

    /**
     * Start listening to the user and streaming their voice to the server.
     */
    private void recognize() {
        //Setup our Record transaction options.
        Transaction.Options options = new Transaction.Options();
        options.setRecognitionType(RecognitionType.DICTATION);
        options.setDetection(resourceIDToDetectionType(detectionType.getCheckedRadioButtonId()));
        String language = spinLang.getSelectedItem().toString();
        language = language.substring(language.lastIndexOf(" ")+1);
        language = language.replace("[", "");
        language = language.replace("]", "");
        options.setLanguage(new Language(language));
        options.setEarcons(startEarcon, stopEarcon, errorEarcon, null);

        if(progressiveResults.isChecked()) {
            options.setResultDeliveryType(ResultDeliveryType.PROGRESSIVE);
        }

        //Start listening
        recoTransaction = speechSession.recognize(options, recoListener);
    }

    private Transaction.Listener recoListener = new Transaction.Listener() {
        @Override
        public void onStartedRecording(Transaction transaction) {

            //We have started recording the users voice.
            //We should update our state and start polling their volume.
            setState(State.LISTENING);
            startAudioLevelPoll();
        }

        @Override
        public void onFinishedRecording(Transaction transaction) {

            //We have finished recording the users voice.
            //We should update our state and stop polling their volume.
            setState(State.PROCESSING);
            stopAudioLevelPoll();
        }

        @Override
        public void onRecognition(Transaction transaction, Recognition recognition) {

            //We have received a transcription of the users voice from the server.

            //Iterate through the NBest list
            //List<RecognizedPhrase> nBest = recognition.getDetails();
            Toast.makeText(getApplicationContext(),recognition.getText(),Toast.LENGTH_SHORT).show();
            showResponseFromNuanceService(recognition.getDetails());
        }

        @Override
        public void onSuccess(Transaction transaction, String s) {

            //Notification of a successful transaction.
            setState(State.IDLE);
        }

        @Override
        public void onError(Transaction transaction, String s, TransactionException e) {

            //Something went wrong. Check Configuration.java to ensure that your settings are correct.
            //The user could also be offline, so be sure to handle this case appropriately.
            //We will simply reset to the idle state.
            setState(State.IDLE);
        }
    };

    /**
     * Every 50 milliseconds we should update the volume meter in our UI.
     */
    private Runnable audioPoller = new Runnable() {
        @Override
        public void run() {
            float level = recoTransaction.getAudioLevel();
            volumeBar.setProgress((int)level);
            handler.postDelayed(audioPoller, 50);
        }
    };

    /**
     * Start polling the users audio level.
     */
    private void startAudioLevelPoll() {
        audioPoller.run();
    }

    /**
     * Stop polling the users audio level.
     */
    private void stopAudioLevelPoll() {
        handler.removeCallbacks(audioPoller);
        volumeBar.setProgress(0);
    }

    /**
     * Set the state and update the record button text.
     */
    private void setState(State newState) {
        state = newState;
        switch (newState) {
            case IDLE:
                recordButton.setText("Ready..");
                break;
            case LISTENING:
                recordButton.setText("Listening..");
                break;
            case PROCESSING:
                recordButton.setText("Processing..");
                break;
        }
    }

    private DetectionType resourceIDToDetectionType(int id) {
        if(id == R.id.longRadio) {
            return DetectionType.Long;
        }
        if(id == R.id.shortRadio) {
            return DetectionType.Short;
        }
        if(id == R.id.unlimitedRadio) {
            return DetectionType.None;
        }
        return null;
    }

    /**
     * Method used to show up the result in the List View
     * @param nBest response from the server
     */
    private void showResponseFromNuanceService(List<RecognizedPhrase> nBest){

        final ArrayList<String> finalResultList = new ArrayList<String>();
        for(RecognizedPhrase phrase : nBest) {
            finalResultList.add(phrase.getText() +" Confidence: "+ phrase.getConfidence());
        }
        mostrar.setText(finalResultList.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,finalResultList);
        resultList.setAdapter(adapter);
        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {     // on list item click append text to phrase text
                String newString = finalResultList.get(i).substring(0,finalResultList.get(i).indexOf("Confidence"));
                selectedText.setText(selectedText.getText()+" "+newString);
            }
        });
    }
}
