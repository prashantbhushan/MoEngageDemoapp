package com.moengage.demo.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OrderSuccess extends Fragment {

  RecyclerView productList;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cart, container, false);
    productList = (RecyclerView) view.findViewById(R.id.cartList);
    productList.setHasFixedSize(true);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    productList.setLayoutManager(layoutManager);

    if(MoEngageDemoApp.getInstance().orderHistory.size() == 0){
      productList.setVisibility(View.INVISIBLE);
      view.findViewById(R.id.emptyCart).setVisibility(View.VISIBLE);
    }else{
      ProductAdapter adapter = new ProductAdapter(MoEngageDemoApp.getInstance().orderHistory, getActivity());
      adapter.isListing(true);
      productList.setAdapter(adapter);
      view.findViewById(R.id.emptyCart).setVisibility(View.INVISIBLE);
    }
    view.findViewById(R.id.btnCheckout).setVisibility(View.GONE);
    return view;
  }

}
