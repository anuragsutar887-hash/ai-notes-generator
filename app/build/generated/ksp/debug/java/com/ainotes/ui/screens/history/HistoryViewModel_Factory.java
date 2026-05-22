package com.ainotes.ui.screens.history;

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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  public HistoryViewModel_Factory(Provider<SessionRepository> sessionRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(sessionRepositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new HistoryViewModel_Factory(sessionRepositoryProvider);
  }

  public static HistoryViewModel newInstance(SessionRepository sessionRepository) {
    return new HistoryViewModel(sessionRepository);
  }
}
