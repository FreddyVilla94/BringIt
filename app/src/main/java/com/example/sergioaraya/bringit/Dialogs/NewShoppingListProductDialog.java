package com.example.sergioaraya.bringit.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sergioaraya.bringit.Classes.Constants;
import com.example.sergioaraya.bringit.Classes.Product;
import com.example.sergioaraya.bringit.Classes.Singleton;
import com.example.sergioaraya.bringit.Methods.Parse;
import com.example.sergioaraya.bringit.R;
import com.example.sergioaraya.bringit.Requests.Post;
import com.example.sergioaraya.bringit.Requests.Put;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.markushi.ui.CircleButton;

/**
 * Created by SergioAraya on 25/10/2017.
 */

public class NewShoppingListProductDialog extends Dialog implements View.OnClickListener {

    Constants constants = Constants.getInstance();
    Singleton singleton = Singleton.getInstance();

    private EditText newProductName;
    private EditText newProductQuantity;
    private EditText newProductPrice;
    private CircleButton buttonNewProduct;

    private Context context;
    private Product product;

    private Parse parse;
    private Post post;
    private Put put;

    public NewShoppingListProductDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_shopping_list_product);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        newProductName = (EditText) findViewById(R.id.new_product_name);
        newProductQuantity = (EditText) findViewById(R.id.new_product_quantity);
        newProductPrice = (EditText) findViewById(R.id.new_product_price);
        buttonNewProduct = (CircleButton) findViewById(R.id.button_new_product);

        if (singleton.getControlUpdateProduct() == 1) {
            newProductName.setText(singleton.getProduct().getName());
            newProductQuantity.setText(String.valueOf(singleton.getProduct().getQuantity()));
            newProductPrice.setText(String.valueOf(singleton.getProduct().getPrice()));
        }

        buttonNewProduct.setOnClickListener(this);

    }

    /**
     * Event to create a new product
     * @param v the component in this case a cubodtbutton
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.button_new_product:
                if (newProductName.getText().toString().equals("") || newProductName.getText().toString().length() < 3) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.product_invalid_name), Toast.LENGTH_SHORT).show();
                    newProductName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                } else if (! newProductQuantity.getText().toString().equals("") && ! isNumeric(newProductQuantity.getText().toString())) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.product_invalid_quantity), Toast.LENGTH_SHORT).show();
                    newProductQuantity.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                } else if (! newProductPrice.getText().toString().equals("") && ! isNumeric(newProductPrice.getText().toString())) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.product_invalid_price), Toast.LENGTH_SHORT).show();
                    newProductPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.app_alert, 0);
                } else {
                    product = new Product();
                    product.setName(newProductName.getText().toString());
                    if (newProductQuantity.getText().toString().equals("")) {
                        product.setQuantity(1);
                    } else {
                        product.setQuantity(Integer.parseInt(newProductQuantity.getText().toString()));
                    }
                    if (newProductPrice.getText().toString().equals("")) {
                        product.setPrice(0);
                    } else {
                        product.setPrice(Integer.parseInt(newProductPrice.getText().toString()));
                    }
                    if (singleton.getControlUpdateProduct() == 1) {
                        this.dismiss();
                        new taskModifyProductShoppingList().execute();
                    } else {
                        this.dismiss();
                        new taskSaveProductShoppingList().execute();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Check if input is a number
     * @param number
     * @return true if is number or false if not
     */
    public boolean isNumeric(String number) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(constants.getPatternNumber());
        matcher = pattern.matcher(number);
        return matcher.matches();
    }

    /**
     * Async task to do a post request to insert a new product
     */
    private class taskSaveProductShoppingList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            post = new Post();
            post.saveProductShopList(product.getName(), String.valueOf(product.getQuantity()), String.valueOf(product.getPrice()));

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (singleton.getStatus() != 200) {
                try {
                    throw new Exception(getContext().getResources().getString(R.string.product_save_error));
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.product_save_success), Toast.LENGTH_LONG).show();
                parse = new Parse();
                parse.parseJsonToGetNewProduct(singleton.getBody());
                singleton.getAdapterShoppingListProducts().notifyDataSetChanged();
            }
        }
    }

    /**
     * Async task to do a update request on modify product data
     */
    private class taskModifyProductShoppingList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            put = new Put();
            put.modifyProductShoppingList(product.getName(), String.valueOf(product.getQuantity()), String.valueOf(product.getPrice()));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (singleton.getStatus() != 200) {
                try {
                    throw new Exception(getContext().getResources().getString(R.string.product_modify_error));
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.product_modify_success), Toast.LENGTH_LONG).show();
                singleton.getProduct().setName(product.getName());
                singleton.getProduct().setQuantity(Integer.parseInt(String.valueOf(product.getQuantity())));
                singleton.getProduct().setPrice(Integer.parseInt(String.valueOf(product.getPrice())));
                singleton.getAdapterShoppingListProducts().notifyDataSetChanged();
            }
        }
    }
}
