package com.ainotes.util;

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
public final class FileHelper_Factory implements Factory<FileHelper> {
  private final Provider<Context> contextProvider;

  public FileHelper_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public FileHelper get() {
    return newInstance(contextProvider.get());
  }

  public static FileHelper_Factory create(Provider<Context> contextProvider) {
    return new FileHelper_Factory(contextProvider);
  }

  public static FileHelper newInstance(Context context) {
    return new FileHelper(context);
  }
}
