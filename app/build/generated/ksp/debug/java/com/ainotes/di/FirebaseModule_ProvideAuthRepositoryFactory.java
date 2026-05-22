package com.ainotes.di;

import android.content.Context;
import com.ainotes.data.repository.AuthRepository;
import com.ainotes.data.repository.SessionRepository;
import com.google.firebase.auth.FirebaseAuth;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class FirebaseModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<FirebaseAuth> firebaseAuthProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public FirebaseModule_ProvideAuthRepositoryFactory(Provider<Context> contextProvider,
      Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.firebaseAuthProvider = firebaseAuthProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public AuthRepository get() {
    return provideAuthRepository(contextProvider.get(), firebaseAuthProvider.get(), sessionRepositoryProvider.get());
  }

  public static FirebaseModule_ProvideAuthRepositoryFactory create(
      Provider<Context> contextProvider, Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new FirebaseModule_ProvideAuthRepositoryFactory(contextProvider, firebaseAuthProvider, sessionRepositoryProvider);
  }

  public static AuthRepository provideAuthRepository(Context context, FirebaseAuth firebaseAuth,
      SessionRepository sessionRepository) {
    return Preconditions.checkNotNullFromProvides(FirebaseModule.INSTANCE.provideAuthRepository(context, firebaseAuth, sessionRepository));
  }
}
