package com.bemo.store;

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

public class NewProduct extends AppCompatActivity {

    private DatabaseReference mDatabase;

    EditText name, price, category;
    CheckBox inStockCheckBox;
    TextView inStockTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        name = (EditText)findViewById(R.id.product_name_edit_text);
        category = (EditText) findViewById(R.id.product_category_edit_text);
        price = (EditText)findViewById(R.id.product_price_edit_text);
        inStockCheckBox = (CheckBox)findViewById(R.id.product_in_stock_check_box);
        inStockTextView = (TextView)findViewById(R.id.product_in_stock_text_view);

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addProduct() {
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
        if (productPrice.isEmpty() || productPrice.equals(" ")) {
            name.setError("Please fill this input!");
            return;
        }

        ///////////////////////////

        Product product = new Product(productName, productCategory, productPrice, productAvailability);

        String key = mDatabase.child("products").push().getKey();
        product.setId(key);

        mDatabase.child("products").child(key).setValue(product);

        Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show();
        name.setText("");
        price.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                addProduct();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
