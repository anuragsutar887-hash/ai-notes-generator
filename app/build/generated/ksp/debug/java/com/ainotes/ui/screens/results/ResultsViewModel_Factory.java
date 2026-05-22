package com.ainotes.ui.screens.results;

import com.ainotes.data.repository.SessionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ResultsViewModel_Factory implements Factory<ResultsViewModel> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  public ResultsViewModel_Factory(Provider<SessionRepository> sessionRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public ResultsViewModel get() {
    return newInstance(sessionRepositoryProvider.get());
  }

  public static ResultsViewModel_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new ResultsViewModel_Factory(sessionRepositoryProvider);
  }

  public static ResultsViewModel newInstance(SessionRepository sessionRepository) {
    return new ResultsViewModel(sessionRepository);
  }
}
