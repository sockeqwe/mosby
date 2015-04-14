package com.hannesdorfmann.mosby.sample.mail.label;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.sample.mail.model.mail.Label;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public interface LabelView extends MvpLceView<List<Label>> {

  public void showLabel();

}
