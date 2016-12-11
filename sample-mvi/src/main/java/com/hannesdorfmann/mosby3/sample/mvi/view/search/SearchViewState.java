package com.hannesdorfmann.mosby3.sample.mvi.view.search;

import com.hannesdorfmann.mosby3.sample.mvi.businesslogic.model.Product;
import java.util.List;

/**
 * This class represents the ViewState Model for searching
 *
 * @author Hannes Dorfmann
 */
public interface SearchViewState {

  /**
   * The search has not been stared yet
   */
  final class SearchNotStartedYet implements SearchViewState {
  }

  final class Loading implements SearchViewState {
  }

  /**
   * Indicates that the search has delivered an empty result set
   */
  final class EmptyResult implements SearchViewState {
    private final String searchQueryText;

    public EmptyResult(String searchQueryText) {
      this.searchQueryText = searchQueryText;
    }

    public String getSearchQueryText() {
      return searchQueryText;
    }

    @Override public String toString() {
      return "EmptyResult{" +
          "searchQueryText='" + searchQueryText + '\'' +
          '}';
    }
  }

  /**
   * A valid search result. Contains a list of items that have matched the searching criteria.
   */
  final class SearchResult implements SearchViewState {
    private final String searchQueryText;
    private final List<Product> result;

    public SearchResult(String searchQueryText, List<Product> result) {
      this.searchQueryText = searchQueryText;
      this.result = result;
    }

    public String getSearchQueryText() {
      return searchQueryText;
    }

    public List<Product> getResult() {
      return result;
    }

    @Override public String toString() {
      return "SearchResult{" +
          "searchQueryText='" + searchQueryText + '\'' +
          ", result=" + result +
          '}';
    }
  }

  /**
   * Says that an error has occurred while searching
   */
  final class Error implements SearchViewState {
    private final String searchQueryText;
    private final Throwable error;

    public Error(String searchQueryText, Throwable error) {
      this.searchQueryText = searchQueryText;
      this.error = error;
    }

    public String getSearchQueryText() {
      return searchQueryText;
    }

    public Throwable getError() {
      return error;
    }

    @Override public String toString() {
      return "Error{" +
          "searchQueryText='" + searchQueryText + '\'' +
          ", error=" + error +
          '}';
    }
  }
}
