package com.hannesdorfmann.mosby.sample.dagger2.repos;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.sample.dagger2.model.Repo;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public interface ReposView extends MvpLceView<List<Repo>> {
}
