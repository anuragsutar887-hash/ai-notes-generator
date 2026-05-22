package com.ainotes.ui.screens.profile;

import com.ainotes.data.local.ThemePreferences;
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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<ThemePreferences> themePreferencesProvider;

  public ProfileViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<ThemePreferences> themePreferencesProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.themePreferencesProvider = themePreferencesProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(authRepositoryProvider.get(), profileRepositoryProvider.get(), themePreferencesProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<ThemePreferences> themePreferencesProvider) {
    return new ProfileViewModel_Factory(authRepositoryProvider, profileRepositoryProvider, themePreferencesProvider);
  }

  public static ProfileViewModel newInstance(AuthRepository authRepository,
      ProfileRepository profileRepository, ThemePreferences themePreferences) {
    return new ProfileViewModel(authRepository, profileRepository, themePreferences);
  }
}
