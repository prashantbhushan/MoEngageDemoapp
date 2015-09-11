package com.moengage.demo.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.moe.pushlibrary.MoEHelper;
import com.moe.pushlibrary.PayloadBuilder;

public class CartFragment extends Fragment {

  RecyclerView productList;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cart, container, false);
    productList = (RecyclerView) view.findViewById(R.id.cartList);
    productList.setHasFixedSize(true);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    productList.setLayoutManager(layoutManager);

    view.findViewById(R.id.btnCheckout).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        double totalValue = 0;
        int itemCount = 0;
        for( Product prod : MoEngageDemoApp.getInstance().cartList){
          totalValue +=prod.price;
          itemCount++;
        }
        EventTracker.orderSuccessful(getActivity(), new PayloadBuilder().putAttrInt("itemCount", itemCount)
            .putAttrDouble("cartValue", totalValue).build());
        MoEngageDemoApp.getInstance().checkoutAll();
        productList.getAdapter().notifyDataSetChanged();
        getActivity().onBackPressed();
      }
    });


    if(MoEngageDemoApp.getInstance().cartList.size() == 0){
      productList.setVisibility(View.INVISIBLE);
      view.findViewById(R.id.emptyCart).setVisibility(View.VISIBLE);
      view.findViewById(R.id.btnCheckout).setVisibility(View.GONE);
    }else{
      CartAdapter adapter = new CartAdapter(MoEngageDemoApp.getInstance().cartList, getActivity());
      productList.setAdapter(adapter);
      view.findViewById(R.id.emptyCart).setVisibility(View.INVISIBLE);
    }
    return view;
  }
}
