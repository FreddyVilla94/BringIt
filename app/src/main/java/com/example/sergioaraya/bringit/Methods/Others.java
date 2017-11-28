package com.example.sergioaraya.bringit.Methods;

import com.example.sergioaraya.bringit.Classes.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SergioAraya on 27/11/2017.
 */

public class Others {

    Constants constants = Constants.getInstance();

    /**
     * Validate input(email, password) according with a patther
     * @param string email or password
     * @param type defines if the input is a password or email
     * @return if input matches with pattern and false if not
     */
    private boolean validateFormat(String string, boolean type) {
        Pattern pattern; Matcher matcher;
        if (type){
            pattern = Pattern.compile(constants.getPatternPassword());
            matcher = pattern.matcher(string);
        } else{
            pattern = Pattern.compile(constants.getPatternEmail());
            matcher = pattern.matcher(string);
        }
        return matcher.matches();
    }

}
