package com.ainotes.data.repository;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<FirebaseAuth> firebaseAuthProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public AuthRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.firebaseAuthProvider = firebaseAuthProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(contextProvider.get(), firebaseAuthProvider.get(), sessionRepositoryProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new AuthRepositoryImpl_Factory(contextProvider, firebaseAuthProvider, sessionRepositoryProvider);
  }

  public static AuthRepositoryImpl newInstance(Context context, FirebaseAuth firebaseAuth,
      SessionRepository sessionRepository) {
    return new AuthRepositoryImpl(context, firebaseAuth, sessionRepository);
  }
}
