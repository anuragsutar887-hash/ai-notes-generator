package com.ainotes.data.repository;

import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
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
public final class FirebaseSyncRepository_Factory implements Factory<FirebaseSyncRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseStorage> storageProvider;

  public FirebaseSyncRepository_Factory(Provider<Context> contextProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseStorage> storageProvider) {
    this.contextProvider = contextProvider;
    this.firestoreProvider = firestoreProvider;
    this.storageProvider = storageProvider;
  }

  @Override
  public FirebaseSyncRepository get() {
    return newInstance(contextProvider.get(), firestoreProvider.get(), storageProvider.get());
  }

  public static FirebaseSyncRepository_Factory create(Provider<Context> contextProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseStorage> storageProvider) {
    return new FirebaseSyncRepository_Factory(contextProvider, firestoreProvider, storageProvider);
  }

  public static FirebaseSyncRepository newInstance(Context context, FirebaseFirestore firestore,
      FirebaseStorage storage) {
    return new FirebaseSyncRepository(context, firestore, storage);
  }
}
