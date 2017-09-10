package com.hannesdorfmann.mosby3.mvp;

import java.util.ArrayList;
import java.util.List;


public class TestView implements MvpView {

  public List<Integer> showInvocations = new ArrayList<Integer>();

  public void show(int i){
    this.showInvocations.add(i);
  }
}
