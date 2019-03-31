package com.bemo.store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductsAdapter extends ArrayAdapter<Product> {

    private TextView name, category, price, inStock;
    private CircleImageView image;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    public ProductsAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("products");
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Product product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        name = (TextView)convertView.findViewById(R.id.product_name_text_view);
        category = (TextView)convertView.findViewById(R.id.product_category_text_view);
        price = (TextView)convertView.findViewById(R.id.product_price_text_view);
        inStock = (TextView)convertView.findViewById(R.id.product_in_stock_text_view);
        image = (CircleImageView)convertView.findViewById(R.id.product_image);

        name.setText(product.getName());
        category.setText(product.getCategory());
        price.setText(product.getPrice() + " EPG");
        inStock.setText(product.getIn_stock());
        if (product.getIn_stock().equals(getContext().getResources().getText(R.string.available).toString())) {
            inStock.setTextColor(getContext().getResources().getColor(R.color.colorGreen1));
        } else {
            inStock.setTextColor(getContext().getResources().getColor(R.color.colorRed1));
        }

        mStorage.child("images/products/" + product.getId())
                .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(getContext())
                            .load(task.getResult())
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.product_default_image)
                            .error(R.drawable.product_default_image)
                            .into(image);
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("requested url = ", mStorage.getDownloadUrl().toString());
                }
            }
        });


        View listItem = (View)convertView.findViewById(R.id.item_view);
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit = new Intent(getContext().getApplicationContext(), NewProduct.class);

                edit.putExtra("id", product.getId());
                edit.putExtra("name", product.getName());
                edit.putExtra("category", product.getCategory());
                edit.putExtra("price", product.getPrice());
                edit.putExtra("inStock", product.getIn_stock());

                getContext().startActivity(edit);
            }
        });


        listItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(product.getName())
                        .setMessage("Do you want to delete this product?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child(product.getId()).removeValue();
                                mStorage.child("images/products/" + product.getId()).delete();
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
