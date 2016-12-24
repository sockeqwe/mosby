/*
 * Copyright 2016 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model;

/**
 * This is a pojo model class representing a Product
 *
 * @author Hannes Dorfmann
 */
public final class Product implements FeedItem {
  private int id;
  private String image;
  private String name;
  private String category;
  private String description;
  private double price;

  Product() {
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public String getDescription() {
    return description;
  }

  public String getImage() {
    return image;
  }

  public double getPrice() {
    return price;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Product product = (Product) o;

    if (id != product.id) return false;
    if (Double.compare(product.price, price) != 0) return false;
    if (image != null ? !image.equals(product.image) : product.image != null) return false;
    if (name != null ? !name.equals(product.name) : product.name != null) return false;
    if (category != null ? !category.equals(product.category) : product.category != null) {
      return false;
    }
    return description != null ? description.equals(product.description)
        : product.description == null;
  }

  @Override public int hashCode() {
    int result;
    long temp;
    result = id;
    result = 31 * result + (image != null ? image.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (category != null ? category.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    temp = Double.doubleToLongBits(price);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override public String toString() {
    return "Product{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", category='" + category + '\'' +
        '}';
  }
}
