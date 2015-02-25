package com.hannesdorfmann.mosby.sample.mvp.lce;

/**
 * @author Hannes Dorfmann
 */
public class Country {

  private String name;

  public Country(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }


}
