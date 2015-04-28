package com.hannesdorfmann.mosby.sample.mail.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.hannesdorfmann.mosby.sample.mail.model.contact.Person;
import com.hannesdorfmann.mosby.sample.mail.model.contact.ProfileScreen;
import com.hannesdorfmann.mosby.sample.mail.profile.mails.ProfileMailsFragmentBuilder;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class ProfileScreensAdapter extends FragmentPagerAdapter {

  private List<ProfileScreen> screens;
  private Person person;

  public ProfileScreensAdapter(FragmentManager fm, Person person) {
    super(fm);
    this.person = person;
  }

  public List<ProfileScreen> getScreens() {
    return screens;
  }

  public void setScreens(List<ProfileScreen> screens) {
    this.screens = screens;
  }

  @Override public Fragment getItem(int position) {

    ProfileScreen screen = screens.get(position);
    if (screen.getType() == ProfileScreen.TYPE_MAILS) {
      return new ProfileMailsFragmentBuilder(person).build();
    }

    return null;
  }

  @Override public int getCount() {

    // return screens == null ? 0 : screens.size();
    return 1;
  }

  @Override public CharSequence getPageTitle(int position) {
    return screens.get(position).getName();
  }
}
