package com.ainotes.data.repository;

import com.ainotes.data.local.NoteSessionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SessionRepository_Factory implements Factory<SessionRepository> {
  private final Provider<NoteSessionDao> daoProvider;

  public SessionRepository_Factory(Provider<NoteSessionDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public SessionRepository get() {
    return newInstance(daoProvider.get());
  }

  public static SessionRepository_Factory create(Provider<NoteSessionDao> daoProvider) {
    return new SessionRepository_Factory(daoProvider);
  }

  public static SessionRepository newInstance(NoteSessionDao dao) {
    return new SessionRepository(dao);
  }
}
