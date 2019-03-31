package com.bemo.store;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class NewProduct extends AppCompatActivity {

    String id, nameValue, categoryValue, priceValue, inStockValue;
    Boolean editMode = false;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    EditText name, category, price;
    CheckBox inStockCheckBox;
    TextView inStockTextView;
    ImageView productImageView;

    private String availableString;
    private String soldOutString;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private String productKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        super.setTitle("New Product");

        availableString = getResources().getText(R.string.available).toString();
        soldOutString = getResources().getText(R.string.sold_out).toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        Intent i = getIntent();

        if (i.hasExtra("id")) {
            editMode = true;
        }

        productImageView = (ImageView)findViewById(R.id.product_image_selector);
        name = (EditText)findViewById(R.id.product_name_edit_text);
        category = (EditText)findViewById(R.id.product_category_edit_text);
        price = (EditText)findViewById(R.id.product_price_edit_text);
        inStockCheckBox = (CheckBox)findViewById(R.id.product_in_stock_check_box);
        inStockTextView = (TextView)findViewById(R.id.product_in_stock_text_view);

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

        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        if (editMode) {
            super.setTitle("Edit Product");

            id = getIntent().getExtras().getString("id");
            nameValue = getIntent().getExtras().getString("name");
            categoryValue = getIntent().getExtras().getString("category");
            priceValue = getIntent().getExtras().getString("price");
            inStockValue = getIntent().getExtras().getString("inStock");

            name.setText(nameValue);
            category.setText(categoryValue);
            price.setText(priceValue);
            inStockTextView.setText(inStockValue);

            if (inStockValue.equals(availableString)) {
                inStockCheckBox.setChecked(true);
            } else {
                inStockCheckBox.setChecked(false);
            }

            mStorage.child("images/products/" + id)
                    .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Glide.with(NewProduct.this)
                                .load(task.getResult())
                                .apply(RequestOptions.circleCropTransform())
                                .placeholder(R.drawable.product_default_image)
                                .error(R.drawable.product_default_image)
                                .into(productImageView);
                    } else {
                        Toast.makeText(NewProduct.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("requested url = ", mStorage.getDownloadUrl().toString());
                    }
                }
            });
        }
    }

    public void addProduct() {
        if (filePath == null && !editMode) {
            Toast.makeText(NewProduct.this, "Please choose an image!", Toast.LENGTH_SHORT).show();
            return;
        }

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
            category.setError("Please fill this input!");
            return;
        }
        if (productPrice.isEmpty() || productPrice.equals(" ")) {
            price.setError("Please fill this input!");
            return;
        }

        ///////////////////////////

        Product product = new Product(productName, productCategory, productPrice, productAvailability);

        if (editMode) {
            product.setId(id);

            mDatabase.child("products").child(id).setValue(product);
            uploadImage();

            Toast.makeText(this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
            return;
        }

        productKey = mDatabase.child("products").push().getKey();
        product.setId(productKey);

        mDatabase.child("products").child(productKey).setValue(product);
        uploadImage();

        Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show();
        name.setText("");
        name.requestFocus();
        category.setText("");
        price.setText("");
        productImageView.setImageDrawable(getResources().getDrawable(R.drawable.product_default_image));
    }

    public void chooseImage() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE_REQUEST);

    }

    public void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            StorageReference ref = mStorage.child("images/products/" + productKey);
            if (editMode) {
                ref = mStorage.child("images/products/" + id);
            }
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(NewProduct.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                            filePath = null;
                            if (editMode) { onBackPressed(); }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(NewProduct.this, "Failed to upload image!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(final UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int)progress + "%");
                        }
                    });
        } else { if (editMode) { onBackPressed(); } }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                productImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        if (!editMode) {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                addProduct();
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
