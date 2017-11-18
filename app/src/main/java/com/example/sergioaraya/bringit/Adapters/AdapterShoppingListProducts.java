package com.example.sergioaraya.bringit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.sergioaraya.bringit.Classes.Product;
import com.example.sergioaraya.bringit.Classes.Singleton;
import com.example.sergioaraya.bringit.R;

import java.util.ArrayList;

/**
 * Created by SergioAraya on 06/10/2017.
 */

public class AdapterShoppingListProducts extends BaseAdapter implements Filterable {

    private Activity activity;
    private Singleton singleton = Singleton.getInstance();

    private static LayoutInflater inflater = null;
    private ArrayList<Product> originalItems;
    private ArrayList<Product> filteredItems;

    public AdapterShoppingListProducts(Activity activity, ArrayList<Product> items) {
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.originalItems = items;
        this.filteredItems = items;
    }

    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View view = convertView;

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_shopping_list_product, null);
        }

        final Product product = filteredItems.get(position);

        TextView productQuantity = (TextView) view.findViewById(R.id.shopping_list_product_quantity);
        productQuantity.setText(String.valueOf(product.getQuantity()));

        TextView productName = (TextView) view.findViewById(R.id.shopping_list_product_name);
        productName.setText(product.getName());

        TextView productPrice = (TextView) view.findViewById(R.id.shopping_list_product_price);
        if (product.getPrice() != 0) {
            productPrice.setText(String.valueOf(product.getPrice()));
        }
        else {
            productPrice.setText("0");
        }


        if (product.getIsInCart()) {
            view.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
        } else {
            view.setBackgroundColor(activity.getResources().getColor(R.color.colorWhite));
        }
        view.setPadding(25,25,25,25);
        return view;

    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
