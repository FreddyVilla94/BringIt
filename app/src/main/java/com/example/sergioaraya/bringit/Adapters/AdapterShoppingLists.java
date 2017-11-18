package com.example.sergioaraya.bringit.Adapters;

/**
 * Created by SergioAraya on 15/09/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.example.sergioaraya.bringit.Classes.ShoppingList;
import com.example.sergioaraya.bringit.Classes.Singleton;
import com.example.sergioaraya.bringit.R;

import java.util.ArrayList;

public class AdapterShoppingLists extends BaseAdapter implements Filterable {

    private Activity activity;
    private Singleton singleton = Singleton.getInstance();

    private static LayoutInflater inflater = null;
    private ArrayList<ShoppingList> originalItems;
    private ArrayList<ShoppingList> filteredItems;

    public AdapterShoppingLists(Activity activity, ArrayList<ShoppingList> items) {
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
            view = inflater.inflate(R.layout.item_shopping_list, null);
        }

        final ShoppingList shoppingList = filteredItems.get(position);

        TextView shoppingListName = (TextView) view.findViewById(R.id.shopping_list_name);
        shoppingListName.setText(shoppingList.getName());

        final TextView shoppingListDate = (TextView) view.findViewById(R.id.shopping_list_date);
        shoppingListDate.setText(shoppingList.getDate());

        view.setPadding(50, 50, 50, 50);
        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0){
                    results.values = originalItems;
                    results.count = originalItems.size();
                }
                else{
                    String filterString = constraint.toString().toLowerCase();

                    ArrayList<ShoppingList> filterResultsData = new ArrayList<>();
                    for (ShoppingList data : originalItems){
                        if (data.getName().toLowerCase().contains(filterString)){
                            filterResultsData.add(data);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems = (ArrayList<ShoppingList>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
