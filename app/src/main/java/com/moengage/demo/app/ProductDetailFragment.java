package com.moengage.demo.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class ProductDetailFragment extends Fragment {

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View productView = inflater.inflate(R.layout.layout_product, container, false);
    TextView mTextView = (TextView) productView.findViewById(R.id.textName);
    TextView mProdDesc =(TextView) productView.findViewById(R.id.textDescription);
    TextView mProdPrice =(TextView) productView.findViewById(R.id.textPrice);
    TextView mProdBrand = (TextView) productView.findViewById(R.id.textBrand);
    ImageView bannerImage = (ImageView)productView.findViewById(R.id.categoryImage);
    Button btnBuyNow = (Button) productView.findViewById(R.id.buyNow);
    Button btnAddToCart = (Button) productView.findViewById(R.id.addToCart);
    final ImageButton btnWishListAdd = (ImageButton) productView.findViewById(R.id.addToWishList);
    final ImageButton btnWishListAdded = (ImageButton) productView.findViewById(R.id.addedToWishList);

    Bundle args = getArguments();
    Product product = args.getParcelable("PRODUCT");
    if( null == product ){
      return null;
    }

    EventTracker.trackProductViewed(product, getActivity());

    mTextView.setText(product.title);
    if(!TextUtils.isEmpty(product.description) && !product.description.equals("null")){
      mProdDesc.setText(product.description);
    }
    mProdBrand.setText(product.brand);
    mProdPrice.setText(product.currency + " " + product.price);

    btnBuyNow.setTag(product);

    btnWishListAdd.setTag(product);
    btnWishListAdded.setTag(product);
    
    final ProductListingFragment.OnClickListener listener = (ProductListingFragment.OnClickListener)getActivity();

    btnAddToCart.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Product product = (Product) v.getTag();
        EventTracker.trackAddToCartEvent(product, v.getContext());
        MoEngageDemoApp.getInstance().addToCart(product);
        listener.onProductClick(product, 1);
      }
    });

    btnBuyNow.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        v.setEnabled(false);
        Product product = (Product) v.getTag();
        EventTracker.trackAddToCartEvent(product, v.getContext());
        MoEngageDemoApp.getInstance().addToCart(product);
        listener.onProductClick(product, 3);
      }
    });

    if( null != product.imageUrl){
      Glide.with(getActivity()).load(product.imageUrl)
          //.centerCrop()
          .crossFade()
          .into(bannerImage);
    }

    if(MoEngageDemoApp.getInstance().wishList.contains(product)){
      btnWishListAdded.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          btnWishListAdd.setVisibility(View.VISIBLE);
          btnWishListAdded.setVisibility(View.GONE);
          MoEngageDemoApp.getInstance().removeFromWishList((Product) v.getTag());
          EventTracker.trackWishlistEvent((Product) v.getTag(), v.getContext(), false);
        }
      });
    }else{
      btnWishListAdd.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          btnWishListAdded.setVisibility(View.VISIBLE);
          btnWishListAdd.setVisibility(View.GONE);
          MoEngageDemoApp.getInstance().addToWishList((Product) v.getTag());
          EventTracker.trackWishlistEvent((Product) v.getTag(), v.getContext(), true);

        }
      });
    }
    return productView;
  }


}
