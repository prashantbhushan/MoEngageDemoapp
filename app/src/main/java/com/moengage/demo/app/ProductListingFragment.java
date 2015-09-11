package com.moengage.demo.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


public class ProductListingFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Product>>{

  RecyclerView productList;
  TextView header;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_category, container, false);
    productList = (RecyclerView) view.findViewById(R.id.categoryRecyclerView);
    productList.setHasFixedSize(true);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    productList.setLayoutManager(layoutManager);

    getActivity().getSupportLoaderManager().initLoader(CAT_LOADER_ID, null, this);
    ((ProgressCallbacks)getActivity()).showProgressBar();

    header = (TextView)view.findViewById(R.id.catName);
    header.setVisibility(View.VISIBLE);
    header.setText("Category > " + MoEngageDemoApp.getInstance().categoryInView.name);

    EventTracker.trackLastCategoryView(MoEngageDemoApp.getInstance().categoryInView, getActivity());

    return view;
  }

  private static final int CAT_LOADER_ID = 532;

  @Override public void onStart() {
    super.onStart();
    getActivity().getSupportLoaderManager().getLoader(CAT_LOADER_ID).startLoading();
    ((ProgressCallbacks)getActivity()).showProgressBar();

  }

  @Override public Loader<ArrayList<Product>> onCreateLoader(int id, Bundle args) {
    return new CategoryLoader(getActivity().getApplicationContext());
  }

  @Override public void onLoadFinished(Loader<ArrayList<Product>> loader, ArrayList<Product> data) {
    ProductAdapter adapter = new ProductAdapter(data, getActivity());
    productList.setAdapter(adapter);
    ProgressCallbacks callback = ((ProgressCallbacks) getActivity());
    if( null == callback)return;
    if( null == data) {
      callback.showErrorMessage();
    }else {
      callback.hideProgressBar();
    }
  }

  @Override public void onLoaderReset(Loader<ArrayList<Product>> loader) {

  }

  public interface OnClickListener {

    void onProductClick(Product product, int button);

    void onProductRemoved(Product product);

    void viewProductDetail(Product product);
  }

  public void reset(){
    getActivity().getSupportLoaderManager().getLoader(CAT_LOADER_ID).forceLoad();
    header.setText("Category > " + MoEngageDemoApp.getInstance().categoryInView.name);
  }
}
