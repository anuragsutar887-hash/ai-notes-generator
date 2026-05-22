package com.ainotes.data.local;

import android.content.Context;
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
public final class ThemePreferences_Factory implements Factory<ThemePreferences> {
  private final Provider<Context> contextProvider;

  public ThemePreferences_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ThemePreferences get() {
    return newInstance(contextProvider.get());
  }

  public static ThemePreferences_Factory create(Provider<Context> contextProvider) {
    return new ThemePreferences_Factory(contextProvider);
  }

  public static ThemePreferences newInstance(Context context) {
    return new ThemePreferences(context);
  }
}
