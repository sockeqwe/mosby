/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby3.sample.mvp.customviewstate;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby3.sample.mvp.model.custom.A;
import com.hannesdorfmann.mosby3.sample.mvp.model.custom.B;
import java.util.Random;

/**
 * @author Hannes Dorfmann
 */
public class MyCustomPresenter extends MvpBasePresenter<MyCustomView> {

  Random random = new Random();
  public void doA() {

    A a = new A("My name is A "+random.nextInt(10));

    if (isViewAttached()) {
      getView().showA(a);
    }
  }

  public void doB() {

    B b = new B("I am B "+random.nextInt(10));

    if (isViewAttached()) {
      getView().showB(b);
    }
  }
}
