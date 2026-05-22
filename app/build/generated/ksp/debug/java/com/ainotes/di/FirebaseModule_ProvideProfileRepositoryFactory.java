package com.ainotes.di;

import com.ainotes.data.repository.ProfileRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class FirebaseModule_ProvideProfileRepositoryFactory implements Factory<ProfileRepository> {
  private final Provider<FirebaseFirestore> firebaseFirestoreProvider;

  public FirebaseModule_ProvideProfileRepositoryFactory(
      Provider<FirebaseFirestore> firebaseFirestoreProvider) {
    this.firebaseFirestoreProvider = firebaseFirestoreProvider;
  }

  @Override
  public ProfileRepository get() {
    return provideProfileRepository(firebaseFirestoreProvider.get());
  }

  public static FirebaseModule_ProvideProfileRepositoryFactory create(
      Provider<FirebaseFirestore> firebaseFirestoreProvider) {
    return new FirebaseModule_ProvideProfileRepositoryFactory(firebaseFirestoreProvider);
  }

  public static ProfileRepository provideProfileRepository(FirebaseFirestore firebaseFirestore) {
    return Preconditions.checkNotNullFromProvides(FirebaseModule.INSTANCE.provideProfileRepository(firebaseFirestore));
  }
}
