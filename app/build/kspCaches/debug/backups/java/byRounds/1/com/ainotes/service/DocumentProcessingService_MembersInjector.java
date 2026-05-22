package com.ainotes.service;

import com.ainotes.data.repository.GeminiRepository;
import com.ainotes.data.repository.SessionRepository;
import com.ainotes.util.FileHelper;
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
public final class DocumentProcessingService_MembersInjector implements MembersInjector<DocumentProcessingService> {
  private final Provider<GeminiRepository> geminiRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<FileHelper> fileHelperProvider;

  public DocumentProcessingService_MembersInjector(
      Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<FileHelper> fileHelperProvider) {
    this.geminiRepositoryProvider = geminiRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.fileHelperProvider = fileHelperProvider;
  }

  public static MembersInjector<DocumentProcessingService> create(
      Provider<GeminiRepository> geminiRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<FileHelper> fileHelperProvider) {
    return new DocumentProcessingService_MembersInjector(geminiRepositoryProvider, sessionRepositoryProvider, fileHelperProvider);
  }

  @Override
  public void injectMembers(DocumentProcessingService instance) {
    injectGeminiRepository(instance, geminiRepositoryProvider.get());
    injectSessionRepository(instance, sessionRepositoryProvider.get());
    injectFileHelper(instance, fileHelperProvider.get());
  }

  @InjectedFieldSignature("com.ainotes.service.DocumentProcessingService.geminiRepository")
  public static void injectGeminiRepository(DocumentProcessingService instance,
      GeminiRepository geminiRepository) {
    instance.geminiRepository = geminiRepository;
  }

  @InjectedFieldSignature("com.ainotes.service.DocumentProcessingService.sessionRepository")
  public static void injectSessionRepository(DocumentProcessingService instance,
      SessionRepository sessionRepository) {
    instance.sessionRepository = sessionRepository;
  }

  @InjectedFieldSignature("com.ainotes.service.DocumentProcessingService.fileHelper")
  public static void injectFileHelper(DocumentProcessingService instance, FileHelper fileHelper) {
    instance.fileHelper = fileHelper;
  }
}
