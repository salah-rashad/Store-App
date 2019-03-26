package com.bemo.store;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        id = getIntent().getExtras().getString("id");
        nameValue = getIntent().getExtras().getString("name");
        categoryValue = getIntent().getExtras().getString("category");
        priceValue = getIntent().getExtras().getString("price");
        inStockValue = getIntent().getExtras().getString("inStock");

        name = (EditText)findViewById(R.id.product_name_edit_text);
        category = (EditText)findViewById(R.id.product_category_edit_text);
        price = (EditText)findViewById(R.id.product_price_edit_text);
        inStockCheckBox = (CheckBox)findViewById(R.id.product_in_stock_check_box);
        inStockTextView = (TextView)findViewById(R.id.product_in_stock_text_view);

        name.setText(nameValue);
        category.setText(categoryValue);
        price.setText(priceValue);
        inStockTextView.setText(inStockValue);

        if (inStockValue.equals(getResources().getText(R.string.available).toString())) {
            inStockCheckBox.setEnabled(true);
        } else {
            inStockCheckBox.setEnabled(false);
        }

        inStockCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inStockCheckBox.isChecked()) {
                    inStockTextView.setText(getResources().getText(R.string.available).toString());
                } else {
                    inStockTextView.setText(getResources().getText(R.string.sold_out).toString());
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
            productAvailability = getResources().getText(R.string.available).toString();
        } else {
            productAvailability = getResources().getText(R.string.sold_out).toString();
        }

        ///////////////////////////

        if (productName.isEmpty() || productName.equals(" ")) {
            name.setError("Please fill this input!");
            return;
        }
        if (productCategory.isEmpty() || productCategory.equals(" ")) {
            name.setError("Please fill this input!");
            return;
        }
        if (productPrice.isEmpty() || productPrice.equals(" ")) {
            name.setError("Please fill this input!");
            return;
        }

        ///////////////////////////

        Product product = new Product(productName, productCategory, productPrice, productAvailability);
        product.setId(id);

        mDatabase.child("products").child(id).setValue(product);

        Toast.makeText(this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}
