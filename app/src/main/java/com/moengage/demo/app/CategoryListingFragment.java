package com.moengage.demo.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class CategoryListingFragment extends Fragment {

  RecyclerView categoryList;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_category, container, false);

    categoryList = (RecyclerView) view.findViewById(R.id.categoryRecyclerView);
    categoryList.setHasFixedSize(true);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    categoryList.setLayoutManager(layoutManager);

    ArrayList<Category> categoryArrayList =  getArguments().getParcelableArrayList(
        LandingScreen.EXTRA_CATEGORY_LIST);
    CategoryAdapter adapter = new CategoryAdapter(categoryArrayList, getActivity());
    categoryList.setAdapter(adapter);

    return view;
  }

  public interface OnClickListener {

    void onCategoryClick(Category category);
  }

  @Override public void onStart() {
    super.onStart();
  }

  @Override public void onResume() {
    super.onResume();
  }
}
