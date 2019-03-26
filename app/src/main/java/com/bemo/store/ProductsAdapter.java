package com.bemo.store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProductsAdapter extends ArrayAdapter<Product> {

    private TextView name, category, price, inStock;

    private DatabaseReference mDatabase;

    public ProductsAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("products");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Product product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        name = (TextView) convertView.findViewById(R.id.product_name_text_view);
        category = (TextView) convertView.findViewById(R.id.product_category_text_view);
        price = (TextView) convertView.findViewById(R.id.product_price_text_view);
        inStock = (TextView) convertView.findViewById(R.id.product_in_stock_text_view);

        name.setText(product.getName());
        category.setText(product.getCategory());
        price.setText(product.getPrice());
        inStock.setText(product.getIn_stock());
        if (product.getIn_stock().equals(getContext().getResources().getText(R.string.available).toString())) {
            inStock.setTextColor(getContext().getResources().getColor(R.color.colorGreen1));
        } else {
            inStock.setTextColor(getContext().getResources().getColor(R.color.colorRed1));
        }


        View edit = (View)convertView.findViewById(R.id.item_view);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit = new Intent(getContext(), EditProduct.class);

                edit.putExtra("id", product.getId());
                edit.putExtra("name", product.getName());
                edit.putExtra("category", product.getCategory());
                edit.putExtra("price", product.getPrice());
                edit.putExtra("inStock", product.getIn_stock());

                getContext().startActivity(edit);
            }
        });


        edit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(product.getName())
                        .setMessage("Do you want to delete this product?")
                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child(product.getId()).removeValue();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return false;
            }
        });

        return convertView;
    }
}
