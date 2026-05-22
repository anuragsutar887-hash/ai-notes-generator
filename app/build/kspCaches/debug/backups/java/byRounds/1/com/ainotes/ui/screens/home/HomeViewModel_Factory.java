package com.ainotes.ui.screens.home;

import com.ainotes.data.repository.FirebaseSyncRepository;
import com.ainotes.data.repository.GeminiRepository;
import com.ainotes.data.repository.ProfileRepository;
import com.ainotes.data.repository.SessionRepository;
import com.ainotes.util.FileHelper;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GeminiRepository> geminiRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<FileHelper> fileHelperProvider;

  private final Provider<FirebaseSyncRepository> firebaseSyncRepositoryProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  public HomeViewModel_Factory(Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<FileHelper> fileHelperProvider,
      Provider<FirebaseSyncRepository> firebaseSyncRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider) {
    this.geminiRepositoryProvider = geminiRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.fileHelperProvider = fileHelperProvider;
    this.firebaseSyncRepositoryProvider = firebaseSyncRepositoryProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(geminiRepositoryProvider.get(), sessionRepositoryProvider.get(), fileHelperProvider.get(), firebaseSyncRepositoryProvider.get(), profileRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<FileHelper> fileHelperProvider,
      Provider<FirebaseSyncRepository> firebaseSyncRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider) {
    return new HomeViewModel_Factory(geminiRepositoryProvider, sessionRepositoryProvider, fileHelperProvider, firebaseSyncRepositoryProvider, profileRepositoryProvider);
  }

  public static HomeViewModel newInstance(GeminiRepository geminiRepository,
      SessionRepository sessionRepository, FileHelper fileHelper,
      FirebaseSyncRepository firebaseSyncRepository, ProfileRepository profileRepository) {
    return new HomeViewModel(geminiRepository, sessionRepository, fileHelper, firebaseSyncRepository, profileRepository);
  }
}
