package com.moengage.demo.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable{

  public String productId;
  public String title;
  public String description;
  public String currency;
  public double price;
  public String brand;
  public String imageUrl;
  public boolean inStock;
  public int categoryId;
  public int quantity;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public boolean isInStock() {
    return inStock;
  }

  public void setInStock(boolean inStock) {
    this.inStock = inStock;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public static Creator<Product> getCREATOR() {
    return CREATOR;
  }

  /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#describeContents()
     */
  @Override
  public int describeContents() {
    return 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
   */
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(productId);
    dest.writeString(title);
    dest.writeString(description);
    dest.writeString(currency);
    dest.writeDouble(price);
    dest.writeString(brand);
    dest.writeString(imageUrl);
    dest.writeInt(inStock ? 1 : 0);
    dest.writeInt(categoryId);
    dest.writeInt(quantity);
  }

  public void readFromParcel(Parcel in) {
    productId = in.readString();
    title = in.readString();
    description = in.readString();
    currency = in.readString();
    price = in.readDouble();
    brand = in.readString();
    imageUrl = in.readString();
    inStock = in.readInt() == 1 ;
    categoryId = in.readInt();
    quantity = in.readInt();
  }

  public Product(Parcel source){
    readFromParcel(source);
  }

  public Product() {
    quantity = 1;
  }

  public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {

    /*
     * (non-Javadoc)
     *
     * @see
     * android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
     */
    @Override
    public Product createFromParcel(Parcel source) {
      return new Product(source);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable.Creator#newArray(int)
     */
    @Override
    public Product[] newArray(int size) {
      return new Product[size];
    }

  };

}
