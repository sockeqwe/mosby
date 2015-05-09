package com.hannesdorfmann.mosby.sample.mail.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Scope;

/**
 * @author Hannes Dorfmann
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationWide {
}
