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
public final class PdfChunker_Factory implements Factory<PdfChunker> {
  private final Provider<Context> contextProvider;

  public PdfChunker_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PdfChunker get() {
    return newInstance(contextProvider.get());
  }

  public static PdfChunker_Factory create(Provider<Context> contextProvider) {
    return new PdfChunker_Factory(contextProvider);
  }

  public static PdfChunker newInstance(Context context) {
    return new PdfChunker(context);
  }
}
