package com.bemo.store;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProduct extends AppCompatActivity {

    String id, nameValue, categoryValue, priceValue, inStockValue;

    private DatabaseReference mDatabase;

    EditText name, category, price;
    CheckBox inStockCheckBox;
    TextView inStockTextView;

    private String availableString;
    private String soldOutString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        availableString = getResources().getText(R.string.available).toString();
        soldOutString = getResources().getText(R.string.sold_out).toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        id = getIntent().getExtras().getString("id");
        nameValue = getIntent().getExtras().getString("name");
        categoryValue = getIntent().getExtras().getString("category");
        priceValue = getIntent().getExtras().getString("price");
        inStockValue = getIntent().getExtras().getString("inStock");

        name = (EditText)findViewById(R.id.product_name_text_edit);
        category = (EditText)findViewById(R.id.product_category_text_edit);
        price = (EditText)findViewById(R.id.product_price_text_edit);
        inStockCheckBox = (CheckBox)findViewById(R.id.product_in_stock_check_box_edit);
        inStockTextView = (TextView)findViewById(R.id.product_in_stock_text_view_edit);

        name.setText(nameValue);
        category.setText(categoryValue);
        price.setText(priceValue);
        inStockTextView.setText(inStockValue);

        if (inStockValue.equals(availableString)) {
            inStockCheckBox.setChecked(true);
        } else {
            inStockCheckBox.setChecked(false);
        }

        inStockCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inStockCheckBox.isChecked()) {
                    inStockTextView.setText(availableString);
                } else {
                    inStockTextView.setText(soldOutString);
                }
            }
        });
    }

    public void updateProduct() {
        String productName, productCategory, productPrice, productAvailability;

        productName = name.getText().toString();
        productCategory = category.getText().toString();
        productPrice = price.getText().toString();

        if (inStockCheckBox.isChecked()) {
            productAvailability = availableString;
        } else {
            productAvailability = soldOutString;
        }

        ///////////////////////////

        if (productName.isEmpty() || productName.equals(" ")) {
            name.setError("Please fill this input!");
            return;
        }
        if (productCategory.isEmpty() || productCategory.equals(" ")) {
            category.setError("Please fill this input!");
            return;
        }
        if (productPrice.isEmpty() || productPrice.equals(" ")) {
            price.setError("Please fill this input!");
            return;
        }

        ///////////////////////////

        Product product = new Product(productName, productCategory, productPrice, productAvailability);
        product.setId(id);

        mDatabase.child("products").child(id).setValue(product);

        Toast.makeText(this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                updateProduct();
                break;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setTitle(nameValue)
                        .setMessage("Do you want to delete this product?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child("products").child(id).removeValue();
                                onBackPressed();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
