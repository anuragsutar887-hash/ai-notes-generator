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
public final class OcrHelper_Factory implements Factory<OcrHelper> {
  private final Provider<Context> contextProvider;

  public OcrHelper_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public OcrHelper get() {
    return newInstance(contextProvider.get());
  }

  public static OcrHelper_Factory create(Provider<Context> contextProvider) {
    return new OcrHelper_Factory(contextProvider);
  }

  public static OcrHelper newInstance(Context context) {
    return new OcrHelper(context);
  }
}
