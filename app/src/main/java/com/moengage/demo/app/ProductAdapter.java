package com.moengage.demo.app;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.moe.pushlibrary.MoEHelper;
import com.moe.pushlibrary.PayloadBuilder;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

  List<String> wishList = new ArrayList<>();
  List<String> cartList = new ArrayList<>();
  // Provide a reference to the views for each data item
  // Complex data items may need more than one view per item, and
  // you provide access to all the views for a data item in a view holder
  public static class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView mTextView;
    public TextView mProdDesc;
    public TextView mProdPrice;
    public TextView mProdBrand;
    public Product product;
    public ImageView bannerImage;

    public Button btnBuyNow;
    public Button btnAddToCart;
    public ImageButton btnWishListAdd;
    public ImageButton btnWishListAdded;
    public View root;
    public ViewHolder(View linear) {
      super(linear);
      mTextView = (TextView) linear.findViewById(R.id.textName);
      mProdDesc =(TextView) linear.findViewById(R.id.textDescription);
      mProdPrice =(TextView) linear.findViewById(R.id.textPrice);
      mProdBrand = (TextView) linear.findViewById(R.id.textBrand);
      this.bannerImage = (ImageView)linear.findViewById(R.id.categoryImage);
      btnBuyNow = (Button) linear.findViewById(R.id.buyNow);
      btnAddToCart = (Button) linear.findViewById(R.id.addToCart);
      btnWishListAdd = (ImageButton) linear.findViewById(R.id.addToWishList);
      btnWishListAdded = (ImageButton) linear.findViewById(R.id.addedToWishList);
      root = linear;
    }
  }

  private boolean listing;

  public void isListing(boolean disableButtons){
    listing = disableButtons;
  }

  public ProductAdapter() {
    if( MoEngageDemoApp.getInstance().wishList.size() > 0 ){
      for( Product prod : MoEngageDemoApp.getInstance().wishList){
        wishList.add(prod.productId);
      }
    }
    if( MoEngageDemoApp.getInstance().cartList.size() > 0 ){
      for( Product prod : MoEngageDemoApp.getInstance().cartList){
        cartList.add(prod.productId);
      }
    }
  }

  List<Product> dataset;

  Context context;

  public ProductListingFragment.OnClickListener listener;
  // Provide a suitable constructor (depends on the kind of dataset)
  public ProductAdapter(List<Product> dataset, Activity activity) {
    this.dataset = dataset;
    this.context = activity;
    this.listener = (ProductListingFragment.OnClickListener)activity;
  }

  // Create new views (invoked by the layout manager)
  @Override public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // create a new view
    View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product, parent, false);
    ViewHolder vh = new ViewHolder(v);
    v.setTag(vh);
    return vh;
  }

  // Replace the contents of a view (invoked by the layout manager)
  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    holder.product = dataset.get(position);
    holder.mTextView.setText(holder.product.title);
    if(!TextUtils.isEmpty(holder.product.description) && !holder.product.description.equals("null")){
      holder.mProdDesc.setText(holder.product.description);
    }
    holder.mProdBrand.setText(holder.product.brand);
    holder.mProdPrice.setText(holder.product.currency + " " + holder.product.price);

    holder.btnBuyNow.setTag(holder.product);

    holder.btnWishListAdd.setTag(holder.product);
    holder.btnWishListAdded.setTag(holder.product);

    if(wishList.contains(holder.product.productId)){
      holder.btnWishListAdded.setVisibility(View.VISIBLE);
      holder.btnWishListAdd.setVisibility(View.GONE);
    }else{
      holder.btnWishListAdded.setVisibility(View.GONE);
      holder.btnWishListAdd.setVisibility(View.VISIBLE);
    }

    if(cartList.contains(holder.product.productId)){
      holder.btnAddToCart.setText("In Cart");
      holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Product product = (Product) v.getTag();
          listener.onProductClick(product, 1);

          PayloadBuilder builder = new PayloadBuilder();
          builder.putAttrString("productId", product.productId)
              .putAttrBoolean("inCart", true);
          MoEHelper.getInstance(v.getContext()).trackEvent("interestedInCartItem", builder.build());
        }
      });
    }else{
      holder.btnAddToCart.setText(R.string.text_cart);
      holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Product product = (Product) v.getTag();
          EventTracker.trackAddToCartEvent(product, v.getContext());
          MoEngageDemoApp.getInstance().addToCart(product);
          listener.onProductClick(product, 1);
        }
      });
    }
    holder.btnAddToCart.setTag(holder.product);

    holder.btnBuyNow.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        v.setEnabled(false);
        Product product = (Product) v.getTag();
        EventTracker.trackAddToCartEvent(product, v.getContext());
        MoEngageDemoApp.getInstance().addToCart(product);
        listener.onProductClick(product, 3);
      }
    });

    if( null != holder.product.imageUrl){
      Glide.with(context).load(holder.product.imageUrl)
          //.centerCrop()
          .crossFade()
          .into(holder.bannerImage);
    }

    holder.btnWishListAdd.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        holder.btnWishListAdded.setVisibility(View.VISIBLE);
        holder.btnWishListAdd.setVisibility(View.GONE);
        MoEngageDemoApp.getInstance().addToWishList((Product) v.getTag());
        EventTracker.trackWishlistEvent((Product) v.getTag(), v.getContext(), true);
        wishList.add(((Product) v.getTag()).productId);
      }
    });

    holder.btnWishListAdded.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        holder.btnWishListAdd.setVisibility(View.VISIBLE);
        holder.btnWishListAdded.setVisibility(View.GONE);
        MoEngageDemoApp.getInstance().removeFromWishList((Product) v.getTag());
        EventTracker.trackWishlistEvent((Product) v.getTag(), v.getContext(), false);
        wishList.remove(((Product) v.getTag()).productId);
      }
    });

    if( listing ){
      holder.btnAddToCart.setVisibility(View.GONE);
      holder.btnWishListAdded.setVisibility(View.GONE);
      holder.btnWishListAdd.setVisibility(View.GONE);
      holder.btnBuyNow.setVisibility(View.GONE);
    }

    holder.root.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.viewProductDetail(holder.product);
      }
    });
  }





  // Return the size of your dataset (invoked by the layout manager)
  @Override public int getItemCount() {
    if( null == dataset)return 0;
    return dataset.size();
  }
}
