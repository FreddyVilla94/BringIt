package com.example.sergioaraya.bringit;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sergioaraya.bringit.Adapters.AdapterShoppingListProducts;
import com.example.sergioaraya.bringit.Classes.Product;
import com.example.sergioaraya.bringit.Classes.ShoppingList;
import com.example.sergioaraya.bringit.Classes.Singleton;
import com.example.sergioaraya.bringit.Dialogs.NewShoppingListProductDialog;
import com.example.sergioaraya.bringit.Methods.Parse;
import com.example.sergioaraya.bringit.Requests.Delete;
import com.example.sergioaraya.bringit.Requests.Post;
import com.example.sergioaraya.bringit.Requests.Put;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class ShoppingListProductsActivity extends AppCompatActivity {

    Singleton singleton = Singleton.getInstance();

    public String palabra;

    // Miscellaneous Components
    private MicrophoneInputStream capture;
    private SpeechToText speechService;

    private boolean listening = false;

    private Toolbar toolbar;
    private ListView shoppingListProducts;

    private AdapterShoppingListProducts adapterShoppingListProducts;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private String numbers;
    private String letters;
    private String temporalLetters;
    private String textSpeechInput;

    private Product product;

    private Hashtable<String, Integer> numbersDictionary;

    private Post post;
    private Delete delete;
    private Put put;
    private Parse parse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_products);

        toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        toolbar.setTitle(singleton.getShoppingList().getName());
        setSupportActionBar(toolbar);

        numbersDictionary = new Hashtable<String, Integer>();

        numbersDictionary.put(getResources().getString(R.string.one), 1); numbersDictionary.put(getResources().getString(R.string.two), 2);
        numbersDictionary.put(getResources().getString(R.string.three), 3); numbersDictionary.put(getResources().getString(R.string.four), 4);
        numbersDictionary.put(getResources().getString(R.string.five), 5); numbersDictionary.put(getResources().getString(R.string.six), 6);
        numbersDictionary.put(getResources().getString(R.string.seven), 7); numbersDictionary.put(getResources().getString(R.string.eight), 8);
        numbersDictionary.put(getResources().getString(R.string.nine), 9); numbersDictionary.put(getResources().getString(R.string.ten), 10);

        loadProductsShoppingList();
        setDownBarVariables();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shopping_list_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_product_shopping_list_text:
                singleton.setControlUpdateProduct(0);
                NewShoppingListProductDialog newShoppingListProductDialog = new NewShoppingListProductDialog(ShoppingListProductsActivity.this);
                newShoppingListProductDialog.show();
                return true;
            case R.id.add_product_shopping_list_speech_google:
                singleton.setControlUpdateProduct(0);
                promptSpeechInput(); //Start Google Speech Recognition Service
                return true;
            case R.id.add_product_shopping_list_speech_watson:
                //Toast.makeText(getApplicationContext(),"IBM WATSON",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,MainActivityIBM.class);
                startActivity(intent);
                return true;
            case R.id.add_product_shopping_list_speech_nuance:
                Intent intentNuance = new Intent(this, MainActivityNuance.class);
                startActivity(intentNuance);
                return true;
            case R.id.search_product:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK)
            singleton.setShoppingList(new ShoppingList());

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Load all products data from the shopping list
     */
    public void loadProductsShoppingList() {

        shoppingListProducts = (ListView) findViewById(R.id.shopping_list_products);
        adapterShoppingListProducts = new AdapterShoppingListProducts(this, singleton.getShoppingList().getProducts());
        singleton.setAdapterShoppingListProducts(adapterShoppingListProducts);
        shoppingListProducts.setAdapter(adapterShoppingListProducts);

        shoppingListProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                singleton.setProduct((Product) parent.getAdapter().getItem(position));

                if (singleton.getProduct().getIsInCart()) {
                    singleton.getProduct().setIsInCart(false);
                } else {
                    singleton.getProduct().setIsInCart(true);
                }
                new taskModifyStateProductShoppingList().execute();
            }
        });

        shoppingListProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {
                singleton.setProduct((Product) parent.getAdapter().getItem(position));
                optionsElement(singleton.getProduct());
                return true;
            }
        });
    }

    /**
     * Load options for each product of the shopping list
     * @param product
     */
    public void optionsElement(final Product product) {

        CharSequence[] options = {getResources().getString(R.string.product_delete), getResources().getString(R.string.product_modify)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.colorPrimary) + "'>" + getResources().getString(R.string.product_options) + "</font>"));
        builder.setItems(options, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (item == 0) {
                    if (singleton.getShoppingList().getProducts().contains(singleton.getProduct())) {
                        new taskDeleteProductShoppingList().execute();
                    }
                } else {
                    singleton.setControlUpdateProduct(1);
                    createProductShoppingListDialogModify();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * Starts Google Speech Recognition service
     */
    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, singleton.getLocale());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.speech_start));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.speech_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //Toast.makeText(getApplicationContext(),result.toString(),Toast.LENGTH_SHORT).show();
                    textSpeechInput = result.get(0);
                    String[] parse = textSpeechInput.split(" ");
                    confirmSpeechText(result.get(0), parse);
                }
                break;
            }
        }
    }

    /**
     * Display a dialog to confirm the input according with speech recognition
     * @param speechText speech recognition input
     * @param parse
     * @return
     */
    public AlertDialog confirmSpeechText(final String speechText, final String[] parse) {

        final String capText = speechText.substring(0, 1).toUpperCase() + speechText.substring(1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.colorPrimary) + "'>" + getResources().getString(R.string.product_title) + "</font>"))
                .setMessage(capText)
                .setPositiveButton(getResources().getString(R.string.speech_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adjustSpeechText(capText);
                                dialog.cancel();
                                new taskSaveProductShoppingList().execute();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.speech_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                promptSpeechInput();
                            }
                        });

        return builder.show();
    }

    /**
     * Check if the current character is a number
     * @param character current character
     * @return true if character is a number
     */
    public boolean isNumeric(char character) {

        try {
            Integer.parseInt(String.valueOf(character));
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    /**
     * Divide the text in numbers and letters or words
     * @param text from speech
     */
    public void adjustSpeechText(String text) {

        String [] parts = text.split(" "); letters = ""; numbers = "";

        for (int i = 0; i < parts.length; i++) {

            temporalLetters = "";

            for (int j = 0; j < parts[i].length(); j++) {

                char character = parts[i].charAt(j);

                if (isNumeric(character)) {
                    numbers += character;
                } else {
                    temporalLetters += character;
                }
            }

            if (checkNumberInNumbersList(temporalLetters)) {
                numbers = temporalLetters;
            } else if (temporalLetters.length() > 2){
                letters += temporalLetters + " ";
            }
        }

        product = new Product();

        if (! numbers.equals("")) {
            for (Map.Entry<String, Integer> entry : numbersDictionary.entrySet()) {
                if (entry.getKey().equals(numbers)) {
                    numbers = String.valueOf(entry.getValue());
                }
            }
        } else {
            numbers = "1";
        }

        product.setQuantity(Integer.parseInt(numbers));
        letters = letters.substring(0, 1).toUpperCase() + letters.substring(1);
        product.setName(letters);
        product.setPrice(0);

    }

    /**
     * Try to find the number in letters in the numbers array
     * @param number in letters
     * @return true if the number in letters exists in the numbers array
     */
    private boolean checkNumberInNumbersList(String number) {
        boolean control = false;
        for (Map.Entry<String, Integer> entry : numbersDictionary.entrySet()) {
            if (entry.getKey().equals(number)) {
                control = true;
            }
        }
        return control;
    }

    /**
     * Display a dialog to add a new product
     */
    private void createProductShoppingListDialogModify() {
        NewShoppingListProductDialog newShoppingListProductDialog = new NewShoppingListProductDialog(this);
        newShoppingListProductDialog.show();
    }

    /**
     * Set down variables like quantity, cart and total price
     */
    public void setDownBarVariables(){

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.footer_descriptions);
        TextView onList = (TextView) linearLayout.findViewById(R.id.footer_products_in_list);
        TextView inCart = (TextView) linearLayout.findViewById(R.id.footer_products_in_cart);
        TextView totalPrice = (TextView) linearLayout.findViewById(R.id.footer_total_price);

        int list = 0; int cart = 0; int total = 0;

        for (int i = 0; i < singleton.getShoppingList().getProducts().size(); i++) {
            list += 1;
            total += singleton.getShoppingList().getProducts().get(i).getQuantity() *
                    singleton.getShoppingList().getProducts().get(i).getPrice();
            if (singleton.getShoppingList().getProducts().get(i).getIsInCart()) {
                cart += 1;
            }
        }
        onList.setText(getResources().getString(R.string.product_on_list) + " " + String.valueOf(list));
        inCart.setText(getResources().getString(R.string.product_in_cart) + " " + String.valueOf(cart));
        totalPrice.setText(getResources().getString(R.string.product_total_price) + " " + String.valueOf(total));
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
                    throw new Exception(getResources().getString(R.string.product_save_error));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.product_save_success), Toast.LENGTH_LONG).show();
                parse = new Parse();
                parse.parseJsonToGetNewProduct(singleton.getBody());
                singleton.getAdapterShoppingListProducts().notifyDataSetChanged();
                setDownBarVariables();
            }
        }
    }

    /**
     * Async task to do a put request to change product state
     */
    private class taskModifyStateProductShoppingList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            put = new Put();
            put.updateStateProductShoppingList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (singleton.getStatus() != 200) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.product_modify_status_error), Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.product_modify_status_success), Toast.LENGTH_LONG).show();
                singleton.getAdapterShoppingListProducts().notifyDataSetChanged();
                setDownBarVariables();
            }
        }
    }

    /**
     * Async task to do a delete request to drop a product from the shopping list
     */
    private class taskDeleteProductShoppingList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            delete = new Delete();
            delete.deleteProductShopList();
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {

            if (singleton.getStatus() != 200) {
                try {
                    throw new Exception(getResources().getString(R.string.product_delete_error));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.product_delete_success), Toast.LENGTH_LONG).show();
                singleton.getShoppingList().getProducts().remove(singleton.getProduct());
                singleton.getAdapterShoppingListProducts().notifyDataSetChanged();
                setDownBarVariables();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setDownBarVariables();
    }

}
