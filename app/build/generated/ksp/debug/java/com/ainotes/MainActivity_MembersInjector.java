package com.ainotes;

import com.ainotes.data.local.ThemePreferences;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<ThemePreferences> themePreferencesProvider;

  public MainActivity_MembersInjector(Provider<ThemePreferences> themePreferencesProvider) {
    this.themePreferencesProvider = themePreferencesProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<ThemePreferences> themePreferencesProvider) {
    return new MainActivity_MembersInjector(themePreferencesProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectThemePreferences(instance, themePreferencesProvider.get());
  }

  @InjectedFieldSignature("com.ainotes.MainActivity.themePreferences")
  public static void injectThemePreferences(MainActivity instance,
      ThemePreferences themePreferences) {
    instance.themePreferences = themePreferences;
  }
}
