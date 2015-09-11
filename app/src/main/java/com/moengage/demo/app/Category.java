package com.moengage.demo.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable{

  public String name;
  public int id;
  public String imageUrl;
  public String api;

  public Category(String name, int id, String imageUrl){
    this.name = name;
    this.id = id;
    this.imageUrl = imageUrl;
  }

  /*
   * (non-Javadoc)
   *
   * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
   */
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeInt(id);
    dest.writeString(imageUrl);
    dest.writeString(api);
  }

  public void readFromParcel(Parcel in) {
    name = in.readString();
    id = in.readInt();
    imageUrl = in.readString();
    api = in.readString();
  }

  public Category(Parcel source){
    readFromParcel(source);
  }

  public Category() {	}

  public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

    /*
     * (non-Javadoc)
     *
     * @see
     * android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
     */
    @Override
    public Category createFromParcel(Parcel source) {
      return new Category(source);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable.Creator#newArray(int)
     */
    @Override
    public Category[] newArray(int size) {
      return new Category[size];
    }

  };


  /*
  * (non-Javadoc)
  *
  * @see android.os.Parcelable#describeContents()
  */
  @Override
  public int describeContents() {
    return 0;
  }
}
