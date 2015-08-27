/*
 *  Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby.sample.dagger1;

import dagger.ObjectGraph;

/**
 * Injector will be passed around in the application to get access to the {@link
 * dagger.ObjectGraph}.
 *
 * <p>
 * Since Injector can be Activity or Fragment, never ever store the injector since <b>it can cause
 * memory leaks</b>. If there is really no other solution then pass around the object graph {@link #getObjectGraph()}
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public interface Injector {

  /**
   * Get the object graph
   */
  public ObjectGraph getObjectGraph();
}
