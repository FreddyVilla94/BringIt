package com.example.sergioaraya.bringit.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sergioaraya.bringit.Adapters.AdapterShoppingLists;
import com.example.sergioaraya.bringit.Classes.ShoppingList;
import com.example.sergioaraya.bringit.Classes.Singleton;
import com.example.sergioaraya.bringit.Classes.User;
import com.example.sergioaraya.bringit.Dialogs.NewShoppingListDialog;
import com.example.sergioaraya.bringit.R;
import com.example.sergioaraya.bringit.Requests.Delete;
import com.example.sergioaraya.bringit.Requests.Post;
import com.example.sergioaraya.bringit.Requests.Put;
import com.example.sergioaraya.bringit.ShoppingListProductsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShoppingListsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShoppingListsFragment extends Fragment implements View.OnClickListener {

    Singleton singleton = Singleton.getInstance();

    @BindView(R.id.shopping_lists) ListView shoppingLists;

    private OnFragmentInteractionListener mListener;

    private AdapterShoppingLists adapterShoppingLists;

    private Delete delete; private Post post; private Put put;

    private User user;

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

        CharSequence[] options = {"Share", "Delete", "Modify"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.colorPrimary) + "'>" + "Options" + "</font>"));
        builder.setItems(options, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (item == 0){
                    //new taskParseJsonToGetUsers().execute(constants.getUrlGetUsers());
                    Toast.makeText(getContext(), "Calling share method", Toast.LENGTH_LONG).show();
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
     * Display a dialog to add a new product
     */
    private void createShoppingListDialogModify(){
        singleton.setControl(1);
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
                    throw new Exception("An error deleting the shopping list");
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Shopping list removed", Toast.LENGTH_LONG).show();
                singleton.getUser().getShoppingLists().remove(singleton.getShoppingList());
                singleton.getAdapterShoppingLists().notifyDataSetChanged();
            }
        }
    }

}
