package com.moengage.demo.app;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.moe.pushlibrary.MoEHelper;
import com.moe.pushlibrary.PayloadBuilder;
import java.util.Date;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

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
    public Button btnRemove;

    public ViewHolder(View linear) {
      super(linear);
      mTextView = (TextView) linear.findViewById(R.id.textName);
      mProdDesc =(TextView) linear.findViewById(R.id.textDescription);
      mProdPrice =(TextView) linear.findViewById(R.id.textPrice);
      mProdBrand = (TextView) linear.findViewById(R.id.textBrand);
      this.bannerImage = (ImageView)linear.findViewById(R.id.categoryImage);
      btnBuyNow = (Button) linear.findViewById(R.id.buyNow);
      btnRemove = (Button) linear.findViewById(R.id.remove);
    }
  }

  List<Product> dataset;

  Context context;

  public ProductListingFragment.OnClickListener listener;
  // Provide a suitable constructor (depends on the kind of dataset)
  public CartAdapter(List<Product> dataset, Activity activity) {
    this.dataset = dataset;
    this.context = activity;
    this.listener = (ProductListingFragment.OnClickListener)activity;
  }

  // Create new views (invoked by the layout manager)
  @Override public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // create a new view
    View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart, parent, false);
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

    holder.btnRemove.setTag(holder.product);

    holder.btnBuyNow.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        v.setEnabled(false);
        Product product = (Product) v.getTag();
        MoEngageDemoApp.getInstance().orderSuccessful(product);
        MoEngageDemoApp.getInstance().removeFromCart(product);

        EventTracker.orderSuccessful(v.getContext(), new PayloadBuilder().putAttrInt("itemCount", 1)
                .putAttrDouble("cartValue", product.price)
                .build());
        listener.onProductClick(product, 4);
      }
    });

    holder.btnRemove.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        v.setEnabled(false);
        Product product = (Product) v.getTag();
        trackRemoveFromCartEvent(product, v.getContext());
        MoEngageDemoApp.getInstance().removeFromCart(product);
      }
    });

    if( null != holder.product.imageUrl){
      Glide.with(context).load(holder.product.imageUrl)
          //.centerCrop()
          .crossFade()
          .into(holder.bannerImage);
    }
  }

  private void trackRemoveFromCartEvent(Product product, Context context){
    PayloadBuilder builder = new PayloadBuilder();
    builder.putAttrDate("date", new Date())
        .putAttrDouble("price", product.price)
        .putAttrString("id", product.productId)
        .putAttrInt("categoryId", product.categoryId)
        .putAttrString("categoryName", MoEngageDemoApp.getCategoryNameFromId(product.categoryId))
        .putAttrString("productName", product.title)
        .putAttrString("currency", product.currency)
        .putAttrInt("quantity", 1);
    MoEHelper.getInstance(context).trackEvent("removedFromCart", builder.build());
    listener.onProductRemoved(product);
  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override public int getItemCount() {
    if( null == dataset)return 0;
    return dataset.size();
  }
}
