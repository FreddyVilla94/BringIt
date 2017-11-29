package com.example.sergioaraya.bringit.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cuboid.cuboidcirclebutton.CuboidButton;
import com.example.sergioaraya.bringit.Adapters.AdapterShoppingLists;
import com.example.sergioaraya.bringit.Classes.Constants;
import com.example.sergioaraya.bringit.Classes.ShoppingList;
import com.example.sergioaraya.bringit.Classes.Singleton;
import com.example.sergioaraya.bringit.Classes.User;
import com.example.sergioaraya.bringit.Dialogs.NewShoppingListDialog;
import com.example.sergioaraya.bringit.R;
import com.example.sergioaraya.bringit.Requests.Delete;
import com.example.sergioaraya.bringit.Requests.Put;
import com.example.sergioaraya.bringit.ShoppingListProductsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShoppingListsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShoppingListsFragment extends Fragment {

    Constants constants = Constants.getInstance();
    Singleton singleton = Singleton.getInstance();

    @BindView(R.id.shopping_lists) ListView shoppingLists;

    private OnFragmentInteractionListener mListener;

    private AdapterShoppingLists adapterShoppingLists;

    private Delete delete; private Put put;

    private User user;

    private ArrayList<User> usersList = new ArrayList<>();
    private ArrayList<String> emailsList = new ArrayList<>();

    private String selectedEmail;
    private String selectedId;

    public ShoppingListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_lists, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadShoppingLists();

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

    /**
     * Load all shopping list data from user
     */
    public void loadShoppingLists(){

        shoppingLists = (ListView) getActivity().findViewById(R.id.shopping_lists);
        adapterShoppingLists = new AdapterShoppingLists(this, singleton.getUser().getShoppingLists());
        singleton.setAdapterShoppingLists(adapterShoppingLists);
        shoppingLists.setAdapter(adapterShoppingLists);

        shoppingLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                singleton.setShoppingList((ShoppingList) parent.getAdapter().getItem(position));
                Intent intent = new Intent(getContext(), ShoppingListProductsActivity.class);
                startActivity(intent);

            }
        });

        shoppingLists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {
                singleton.setShoppingList((ShoppingList) parent.getAdapter().getItem(position));
                optionsElement(singleton.getShoppingList());
                return true;
            }
        });
    }

    /**
     * Load options for each shopping list
     * @param shoppingList current shopping list
     */
    public void optionsElement(final ShoppingList shoppingList){

        CharSequence[] options = {getResources().getString(R.string.shopping_list_share), getResources().getString(R.string.shopping_list_delete), getResources().getString(R.string.shopping_list_modify)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.colorPrimary) + "'>" + getResources().getString(R.string.shopping_list_options) + "</font>"));
        builder.setItems(options, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (item == 0){
                    new taskParseJsonToGetUsers().execute(constants.getUrlGetUsers());
                } else if (item == 1){
                    if (singleton.getUser().getShoppingLists().contains(singleton.getShoppingList())) {
                        new taskDeleteShopList().execute();
                    }
                } else {
                    createShoppingListDialogModify();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * Get data from all users to select someone to share the shopping list
     */
    private class taskParseJsonToGetUsers extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return loadContentFromNetwork(urls[0]);
        }

        protected void onPostExecute(String result) {

            JSONObject reader = null;
            try {
                reader = new JSONObject(result);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            JSONArray users = null;
            try {
                users = reader.getJSONArray("users");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            for (int i = 0; i < users.length(); i++) {

                JSONObject temporalUser = null;
                try {

                    temporalUser = users.getJSONObject(i);
                    user  = new User();

                    user.setId(temporalUser.getString("_id"));
                    user.setEmail(temporalUser.getString("email"));
                    emailsList.add(user.getEmail());

                    usersList.add(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            createShoppingListDialogShare();
        }

        // method to download the content
        private String loadContentFromNetwork(String url) {
            try {
                InputStream mInputStream = (InputStream) new URL(url).getContent();
                InputStreamReader mInputStreamReader = new InputStreamReader(mInputStream);
                BufferedReader responseBuffer = new BufferedReader(mInputStreamReader);
                StringBuilder strBuilder = new StringBuilder();
                String line = null;
                while ((line = responseBuffer.readLine()) != null) {
                    strBuilder.append(line);
                }
                return strBuilder.toString();

            } catch (Exception e) {
                Log.v(constants.getTagImg(), e.getMessage());
            }
            return null;
        }
    }

    /**
     * Display a dialog to share shopping list
     */
    private void createShoppingListDialogShare(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_share_shopping_list);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView shareShoppingListTitle = (TextView) dialog.findViewById(R.id.share_shopping_list_title);
        final AutoCompleteTextView shareShoppingListAutoComplete = (AutoCompleteTextView) dialog.findViewById(R.id.share_shopping_list_auto_complete);

        shareShoppingListTitle.setText(getResources().getString(R.string.shopping_list_share_title));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, emailsList);
        shareShoppingListAutoComplete.setThreshold(1);
        shareShoppingListAutoComplete.setAdapter(adapter);

        shareShoppingListAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long l) {
                selectedEmail = (String) parent.getItemAtPosition(pos);
            }
        });

        CuboidButton shareShoppingListButton = (CuboidButton) dialog.findViewById(R.id.button_share_shopping_list);
        shareShoppingListButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                if (! getIdUserShoppingListShare(selectedEmail).equals("")) {
                    dialog.dismiss();
                    selectedId = getIdUserShoppingListShare(selectedEmail);
                    new taskShareShoppingList().execute();
                } else {
                    shareShoppingListAutoComplete.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                    Toast.makeText(getContext(), getResources().getString(R.string.share_invalid_email), Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();

    }

    /**
     * Get the user id according email
     * @param email user
     * @return
     */
    private String getIdUserShoppingListShare(String email){

        String id = "";

        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getEmail().equals(email))
                id = usersList.get(i).getId();
        }

        return id;
    }

    /**
     * Async task to do a put request to share shopping list with other user
     */
    private class taskShareShoppingList extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            put = new Put();
            put.shareShoppingList(selectedId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (singleton.getStatus() != 200){
                Toast.makeText(getContext(), getResources().getString(R.string.shopping_list_share_error), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.shopping_list_share_success), Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * Display a dialog to add a new product
     */
    private void createShoppingListDialogModify(){
        singleton.setControlUpdateShoppingList(1);
        NewShoppingListDialog newShoppingListDialog = new NewShoppingListDialog(getContext());
        newShoppingListDialog.show();
    }

    /**
     * Async task to do a delete request to drop a shopping list
     */
    private class taskDeleteShopList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            delete = new Delete();
            delete.deleteShopList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (singleton.getStatus() != 200) {
                try {
                    throw new Exception(getResources().getString(R.string.shopping_list_delete_error));
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.shopping_list_share_success), Toast.LENGTH_LONG).show();
                singleton.getUser().getShoppingLists().remove(singleton.getShoppingList());
                singleton.getAdapterShoppingLists().notifyDataSetChanged();
            }
        }
    }

}
