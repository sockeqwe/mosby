package com.hannesdorfmann.mosby.sample.mail.profile.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.mosby.MosbyFragment;
import com.hannesdorfmann.mosby.sample.mail.R;
import com.hannesdorfmann.mosby.sample.mail.model.contact.Person;
import java.text.SimpleDateFormat;

/**
 * @author Hannes Dorfmann
 */
public class AboutFragment extends MosbyFragment {

  @Arg Person person;

  @InjectView(R.id.email) TextView email;
  @InjectView(R.id.birthday) TextView birthday;
  @InjectView(R.id.bio) TextView bio;

  @Override protected int getLayoutRes() {
    return R.layout.fragment_about;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");

    if (!TextUtils.isEmpty(person.getEmail())) {
      email.setText(person.getEmail());
    }

    if (person.getBirthday() != null) {
      birthday.setText(sdf.format(person.getBirthday()));
    }

    if (person.getBioRes() != 0) {
      bio.setText(person.getBioRes());
    }
  }
}
