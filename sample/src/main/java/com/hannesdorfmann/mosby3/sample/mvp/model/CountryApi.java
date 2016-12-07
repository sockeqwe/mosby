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

package com.hannesdorfmann.mosby3.sample.mvp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class CountryApi {

  public static List<Country> getCountries() {
    ArrayList<Country> countries = new ArrayList<Country>(20);

    countries.add(new Country("Italy"));
    countries.add(new Country("Germany"));
    countries.add(new Country("Belgium"));
    countries.add(new Country("Austria"));
    countries.add(new Country("Brazil"));
    countries.add(new Country("Chile"));
    countries.add(new Country("China"));
    countries.add(new Country("Denmark"));
    countries.add(new Country("Finnland"));
    countries.add(new Country("France"));
    countries.add(new Country("Ghana"));
    countries.add(new Country("Japan"));
    countries.add(new Country("Mexico"));
    countries.add(new Country("Netherlands"));
    countries.add(new Country("Norway"));
    countries.add(new Country("Spain"));
    countries.add(new Country("Switzerland"));
    countries.add(new Country("United Kingdom"));
    countries.add(new Country("United States"));
    countries.add(new Country("Australia"));

    return countries;
  }
}
