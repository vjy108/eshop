package com.eshop.project;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eshop.project.ChecksumActivity;
import com.eshop.project.CommaSeperate;
import com.eshop.project.Individual_product_view_Activity;
import com.eshop.project.Products;
import com.eshop.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.List;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder>  {

    private List<Products> productsList;
    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private AVLoadingIndicatorView loader;

    private String user_id;

    public ProductsAdapter(List<Products> productsList, Context mContext, AVLoadingIndicatorView loader) {
        this.productsList = productsList;
        this.mContext = mContext;
        this.loader=loader;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.products_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Products products = productsList.get(productsList.size()-position-1);


        loader.hide();


        Log.d("size",""+productsList.size());

//        if (productsList.size()==0){
//            tv_noitem.setVisibility(View.VISIBLE);
//        }

        Picasso.get().load(products.getProduct_image()).fit().into(viewHolder.productIV);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        user_id = currentUser.getUid();

        // for comma separate
        String newNumber = CommaSeperate.getFormatedNumber(products.getProduct_price());
        viewHolder.tvProductPrice.setText("₹" + newNumber);

        viewHolder.tvProductName.setText(products.getProduct_name());
        viewHolder.tvSellerName.setText("by " + products.getCompany_name());




        viewHolder.productcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(mContext, Individual_product_view_Activity.class);
                productIntent.putExtra("product_key", products.getProduct_key());
                productIntent.putExtra("product_category", products.getProduct_category());
                productIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(productIntent);
            }
        });

        // Todo :: yet not resolved
        // facing problem of crash at this button in SDK >=28


        viewHolder.buy_now_btn_pl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(mContext, ChecksumActivity.class);
                //productIntent.putExtra("product_key", );
                productIntent.putExtra("product_name", products.getProduct_name());
                productIntent.putExtra("product_price", products.getProduct_price());
                productIntent.putExtra("product_image", products.getProduct_image());
                productIntent.putExtra("product_description", products.getProduct_description());
                productIntent.putExtra("company_name", products.getCompany_name());
                productIntent.putExtra("from_cart", "no");
                productIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // This was the problem of crash
                mContext.startActivity(productIntent);
            }
        });

        viewHolder.button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cart_key = mDatabase.child("cart").child(user_id).push().getKey();
                HashMap<String, Object> dataMap = new HashMap<>();
                dataMap.put("product_name", products.getProduct_name());
                dataMap.put("product_price", products.getProduct_price());
                dataMap.put("product_image", products.getProduct_image());
                dataMap.put("company_name", products.getCompany_name());
                dataMap.put("cart_key", cart_key);
                dataMap.put("product_description", products.getProduct_description());
                mDatabase.child("cart").child(user_id).child(cart_key).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(70);
                        Toast.makeText(mContext, "Product added successfully to cart", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productIV;
        private TextView tvProductName, tvProductPrice, tvSellerName;
        private LinearLayout productcv;
        private ProgressBar progressBar3;
        private Button buy_now_btn_pl, button15;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productIV = itemView.findViewById(R.id.productIV);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvSellerName = itemView.findViewById(R.id.tvSellerName);
            productcv = itemView.findViewById(R.id.productcv1);
            buy_now_btn_pl = itemView.findViewById(R.id.btn_buynow_productlis);
            button15 = itemView.findViewById(R.id.button15);
        }
    }

}
