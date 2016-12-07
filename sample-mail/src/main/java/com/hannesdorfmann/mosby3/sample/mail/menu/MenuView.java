/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby3.sample.mail.menu;

import com.hannesdorfmann.mosby3.sample.mail.base.view.AuthView;
import com.hannesdorfmann.mosby3.sample.mail.model.account.Account;
import com.hannesdorfmann.mosby3.sample.mail.model.mail.Label;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public interface MenuView extends AuthView<List<Label>> {

  public void setAccount(Account account);

  public void decrementUnreadCount(String label);
}
