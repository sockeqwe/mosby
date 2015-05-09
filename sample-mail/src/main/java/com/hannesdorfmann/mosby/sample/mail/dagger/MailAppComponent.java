package com.hannesdorfmann.mosby.sample.mail.dagger;

import dagger.Component;

/**
 * @author Hannes Dorfmann
 */


@Component(
    modules = MailModule.class
)
public interface MailAppComponent {
}
