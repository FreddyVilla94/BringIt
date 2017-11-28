package com.example.sergioaraya.bringit.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.sergioaraya.bringit.MainActivity;
import com.example.sergioaraya.bringit.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private CheckBox englishLanguage;
    private CheckBox spanishLanguage;

    private OnFragmentInteractionListener mListener;

    private Intent intent;
    private String languageToLoad;
    private Locale locale;
    private Configuration config;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        englishLanguage = (CheckBox) getActivity().findViewById(R.id.english_language);
        spanishLanguage = (CheckBox) getActivity().findViewById(R.id.spanish_language);

        englishLanguage.setOnCheckedChangeListener(this);
        spanishLanguage.setOnCheckedChangeListener(this);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.english_language:
                languageToLoad = "en";
                locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config,
                        getResources().getDisplayMetrics());
                intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                break;

            case R.id.spanish_language:
                languageToLoad = "es";
                locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config,
                        getResources().getDisplayMetrics());
                intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
