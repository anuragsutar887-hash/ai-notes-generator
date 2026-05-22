package com.ainotes.data.repository;

import com.ainotes.util.FileHelper;
import com.ainotes.util.OcrHelper;
import com.ainotes.util.PdfChunker;
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
public final class GeminiRepository_Factory implements Factory<GeminiRepository> {
  private final Provider<PdfChunker> pdfChunkerProvider;

  private final Provider<OcrHelper> ocrHelperProvider;

  private final Provider<FileHelper> fileHelperProvider;

  public GeminiRepository_Factory(Provider<PdfChunker> pdfChunkerProvider,
      Provider<OcrHelper> ocrHelperProvider, Provider<FileHelper> fileHelperProvider) {
    this.pdfChunkerProvider = pdfChunkerProvider;
    this.ocrHelperProvider = ocrHelperProvider;
    this.fileHelperProvider = fileHelperProvider;
  }

  @Override
  public GeminiRepository get() {
    return newInstance(pdfChunkerProvider.get(), ocrHelperProvider.get(), fileHelperProvider.get());
  }

  public static GeminiRepository_Factory create(Provider<PdfChunker> pdfChunkerProvider,
      Provider<OcrHelper> ocrHelperProvider, Provider<FileHelper> fileHelperProvider) {
    return new GeminiRepository_Factory(pdfChunkerProvider, ocrHelperProvider, fileHelperProvider);
  }

  public static GeminiRepository newInstance(PdfChunker pdfChunker, OcrHelper ocrHelper,
      FileHelper fileHelper) {
    return new GeminiRepository(pdfChunker, ocrHelper, fileHelper);
  }
}
