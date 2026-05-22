package com.ainotes.di;

import com.ainotes.data.local.AppDatabase;
import com.ainotes.data.local.NoteSessionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideNoteSessionDaoFactory implements Factory<NoteSessionDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideNoteSessionDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public NoteSessionDao get() {
    return provideNoteSessionDao(databaseProvider.get());
  }

  public static AppModule_ProvideNoteSessionDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideNoteSessionDaoFactory(databaseProvider);
  }

  public static NoteSessionDao provideNoteSessionDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideNoteSessionDao(database));
  }
}
