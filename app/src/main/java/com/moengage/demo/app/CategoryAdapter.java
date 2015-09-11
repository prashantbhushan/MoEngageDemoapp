package com.moengage.demo.app;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.moe.pushlibrary.MoEHelper;
import com.moe.pushlibrary.PayloadBuilder;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

  // Provide a reference to the views for each data item
  // Complex data items may need more than one view per item, and
  // you provide access to all the views for a data item in a view holder
  public static class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView mTextView;
    public Category category;
    public ImageView bannerImage;

    public ViewHolder(View linear) {
      super(linear);
      mTextView = (TextView) linear.findViewById(R.id.textName);
      this.bannerImage = (ImageView) linear.findViewById(R.id.categoryImage);
    }
  }

  ArrayList<Category> dataset;

  Context context;

  CategoryListingFragment.OnClickListener listener;

  // Provide a suitable constructor (depends on the kind of dataset)
  public CategoryAdapter(ArrayList<Category> dataset, Activity activity) {
    this.dataset = dataset;
    this.context = activity;
    this.listener = (CategoryListingFragment.OnClickListener) activity;
  }

  // Create new views (invoked by the layout manager)
  @Override public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // create a new view
    View v =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category, parent, false);
    // set the view's size, margins, paddings and layout parameters
    ViewHolder vh = new ViewHolder(v);
    v.setTag(vh);
    v.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ViewHolder vh = (ViewHolder) v.getTag();
        MoEHelper.getInstance(v.getContext())
            .trackEvent("categoryViewed",
                new PayloadBuilder().putAttrString("categoryAPI", vh.category.api)
                    .putAttrInt("categoryId", vh.category.id)
                    .putAttrString("categoryName", vh.category.name)
                    .build());
        listener.onCategoryClick(vh.category);
      }
    });
    return vh;
  }

  // Replace the contents of a view (invoked by the layout manager)
  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    holder.category = dataset.get(position);
    holder.mTextView.setText(holder.category.name);

    Glide.with(context).load(holder.category.imageUrl)
        //.fitCenter()
        .centerCrop().crossFade().into(holder.bannerImage);
  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override public int getItemCount() {
    if( null == dataset)return 0;
    return dataset.size();
  }
}
