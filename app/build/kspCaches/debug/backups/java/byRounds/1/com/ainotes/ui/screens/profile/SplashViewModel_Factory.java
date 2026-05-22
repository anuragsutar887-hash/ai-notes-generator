package com.ainotes.ui.screens.profile;

import com.ainotes.data.local.UserPreferences;
import com.ainotes.data.repository.AuthRepository;
import com.ainotes.data.repository.ProfileRepository;
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
public final class SplashViewModel_Factory implements Factory<SplashViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public SplashViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  @Override
  public SplashViewModel get() {
    return newInstance(authRepositoryProvider.get(), profileRepositoryProvider.get(), userPreferencesProvider.get());
  }

  public static SplashViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    return new SplashViewModel_Factory(authRepositoryProvider, profileRepositoryProvider, userPreferencesProvider);
  }

  public static SplashViewModel newInstance(AuthRepository authRepository,
      ProfileRepository profileRepository, UserPreferences userPreferences) {
    return new SplashViewModel(authRepository, profileRepository, userPreferences);
  }
}
