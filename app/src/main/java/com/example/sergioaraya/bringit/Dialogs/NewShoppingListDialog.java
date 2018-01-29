package com.example.sergioaraya.bringit.Dialogs;

/**
 * Created by SergioAraya on 15/09/2017.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sergioaraya.bringit.Classes.ShoppingList;
import com.example.sergioaraya.bringit.Classes.Singleton;
import com.example.sergioaraya.bringit.Methods.Parse;
import com.example.sergioaraya.bringit.R;
import com.example.sergioaraya.bringit.Requests.Post;
import com.example.sergioaraya.bringit.Requests.Put;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import at.markushi.ui.CircleButton;


public class NewShoppingListDialog extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Singleton singleton = Singleton.getInstance();

    private EditText newShoppingListName;
    private Switch switchNotifications;
    private EditText newShoppingListDate;
    private EditText newShoppingListTime;
    private CircleButton buttonNewShoppingList;

    private int dd, mm, yyyy, HH, MM;

    private Context context;

    private ShoppingList shoppingList;
    private Parse parse;
    private Post post;
    private Put put;

    public NewShoppingListDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_shopping_list);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        newShoppingListDate = (EditText) findViewById(R.id.new_shopping_list_date);
        newShoppingListTime = (EditText) findViewById(R.id.new_shopping_list_time);
        newShoppingListName = (EditText) findViewById(R.id.new_shopping_list_name);
        switchNotifications = (Switch) findViewById(R.id.switch_notifications);
        buttonNewShoppingList = (CircleButton) findViewById(R.id.button_new_shopping_list);

        if (singleton.getControlUpdateShoppingList() == 1) {
            newShoppingListName.setText(singleton.getShoppingList().getName());
            newShoppingListDate.setText(singleton.getShoppingList().getDate());
            newShoppingListTime.setText(singleton.getShoppingList().getTime());
        }

        newShoppingListDate.setOnClickListener(this);
        newShoppingListTime.setOnClickListener(this);
        buttonNewShoppingList.setOnClickListener(this);
        switchNotifications.setOnCheckedChangeListener(this);

    }

    /**
     * Event to create a new shopping list
     * @param v the component in this case a cubodtbutton
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        final Calendar calendar = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.new_shopping_list_date:

                final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                try{
                    calendar.setTime(sdf.parse(newShoppingListDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dd = calendar.get(Calendar.DAY_OF_MONTH);
                mm = calendar.get(Calendar.MONTH);
                yyyy = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        Date date = calendar.getTime();

                        newShoppingListDate.setText(sdf.format(date));
                    }
                }, yyyy, mm, dd);
                datePickerDialog.getWindow().setBackgroundDrawableResource(R.drawable.app_dialog);
                datePickerDialog.show();
                break;

            case R.id.new_shopping_list_time:

                HH = calendar.get(Calendar.HOUR_OF_DAY);
                MM = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int hour = hourOfDay % 12;
                        if (hour == 0)
                            hour = 12;
                        newShoppingListTime.setText(String.format(Locale.ENGLISH, "%02d:%02d %s", hour, minute,
                                hourOfDay < 12 ? "am" : "pm"));
                    }
                }, HH, MM, false);
                timePickerDialog.getWindow().setBackgroundDrawableResource(R.drawable.app_dialog);
                timePickerDialog.show();
                break;

            case R.id.button_new_shopping_list:

                shoppingList = new ShoppingList();

                shoppingList.setName(newShoppingListName.getText().toString());
                shoppingList.setUsers(singleton.getUser().getId());

                if (! shoppingList.getName().equals("")) {

                    if (switchNotifications.isChecked()) {
                        shoppingList.setDate(newShoppingListDate.getText().toString());
                        shoppingList.setTime(newShoppingListTime.getText().toString());
                        createNotification(shoppingList.getName());
                    } else {
                        shoppingList.setDate(getContext().getResources().getString(R.string.shopping_list_no_date));
                        shoppingList.setTime(getContext().getResources().getString(R.string.shopping_list_no_time));
                    }
                    if (singleton.getControlUpdateShoppingList() == 1) {
                        this.dismiss();
                        new taskModifyShoppingList().execute();
                    } else {
                        this.dismiss();
                        new taskSaveShoppingList().execute();
                    }
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.shopping_list_invalid_name), Toast.LENGTH_SHORT).show();
                    newShoppingListName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert,0);
                }
                break;

            default:
                break;
        }

    }

    /**
     * Enable EditText components to select date and time for shopping list notifications
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            newShoppingListDate.setEnabled(true);
            newShoppingListTime.setEnabled(true);
        }
        else {
            newShoppingListDate.setEnabled(false);
            newShoppingListTime.setEnabled(false);
        }
    }

    /**
     * Create shopping list notifications according with date and time selected
     * @param title for notifications
     */
    public void createNotification(String title) {

        int day, month, year, hour, minute;

        String selectedDate = newShoppingListDate.getText().toString();
        String[] separatedDate = selectedDate.split("/");
        day = Integer.parseInt(separatedDate[0]);
        month = Integer.parseInt(separatedDate[1]) - 1;
        year = Integer.parseInt(separatedDate[2]);

        String selectedTime = newShoppingListTime.getText().toString();
        String stringHour = selectedTime.substring(0,2);
        hour = Integer.parseInt(stringHour);
        String stringMinute = selectedTime.substring(3,5);
        minute = Integer.parseInt(stringMinute);
        String midDay = selectedTime.substring(6);
        if (midDay.equals("pm"))
            hour += 12;
        System.out.println("Hour:"+hour+" Minute:"+minute+" "+midDay);

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, hour, minute);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        hour += 1;
        endTime.set(year, month, day, hour, minute);
        long endMillis = endTime.getTimeInMillis();

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_CALENDAR);

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title + " " + getContext().getResources().getString(R.string.shopping_list_pending));
        values.put(CalendarContract.Events.DESCRIPTION, "Bring It!");
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        System.out.println("Added the event: " + eventID);

        cr = context.getContentResolver();
        values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, 0);
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
    }

    /*
     * Asyns task to do a put request to modify shopping list data
     */
    private class taskModifyShoppingList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            put = new Put();
            put.modifyShoppingList(shoppingList.getName(), shoppingList.getDate(), shoppingList.getTime());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (singleton.getStatus() != 200) {
                try {
                    throw new Exception(getContext().getResources().getString(R.string.shopping_list_modify_error));
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.shopping_list_modify_success), Toast.LENGTH_LONG).show();
                singleton.getShoppingList().setName(shoppingList.getName());
                singleton.getShoppingList().setDate(shoppingList.getDate());
                singleton.getShoppingList().setTime(shoppingList.getTime());
                singleton.getAdapterShoppingLists().notifyDataSetChanged();
            }
        }
    }

    /**
     *  Async task to do a post request to insert a new shopping list
     */
    private class taskSaveShoppingList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            post = new Post();
            post.saveShopList(shoppingList.getName(), shoppingList.getDate(), shoppingList.getTime());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (singleton.getStatus() != 200) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.shopping_list_save_error), Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.shopping_list_save_success), Toast.LENGTH_LONG).show();
                parse = new Parse();
                parse.parseJsonToGetNewShoppingList(singleton.getBody());
                singleton.getAdapterShoppingLists().notifyDataSetChanged();
            }
        }
    }

}
