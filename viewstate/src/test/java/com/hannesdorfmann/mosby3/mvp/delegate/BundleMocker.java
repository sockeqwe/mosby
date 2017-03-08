/*
 * Copyright 2017 Hannes Dorfmann.
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

package com.hannesdorfmann.mosby3.mvp.delegate;

import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * A simple factor that creates Bundle Mocks
 * @author Hannes Dorfmann
 */
public class BundleMocker {

  private static Map<Bundle, Map<String, String>> internalBundleMap = new HashMap<>();

  public static Bundle create() {
    final Bundle bundle = Mockito.mock(Bundle.class);

    Mockito.doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        String key = (String) invocation.getArguments()[0];
        String value = (String) invocation.getArguments()[1];
        Map<String, String> internalMap = internalBundleMap.get(bundle);
        if (internalMap == null) {
          internalMap = new HashMap<String, String>();
        }

        internalMap.put(key, value);
        internalBundleMap.put(bundle, internalMap);
        return null;
      }
    }).when(bundle).putString(Mockito.anyString(), Mockito.anyString());

    Mockito.doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        String key = (String) invocation.getArguments()[0];
        Map<String, String> internalMap = internalBundleMap.get(bundle);
        if (internalMap == null) {
          return null;
        }

        return internalMap.get(key);
      }
    }).when(bundle).getString(Mockito.anyString());

    return bundle;
  }
}
