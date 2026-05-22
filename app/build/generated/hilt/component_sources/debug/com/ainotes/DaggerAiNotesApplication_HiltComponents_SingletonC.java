package com.ainotes;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.ainotes.data.local.AppDatabase;
import com.ainotes.data.local.NoteSessionDao;
import com.ainotes.data.local.ThemePreferences;
import com.ainotes.data.local.UserPreferences;
import com.ainotes.data.repository.AuthRepository;
import com.ainotes.data.repository.FirebaseSyncRepository;
import com.ainotes.data.repository.GeminiRepository;
import com.ainotes.data.repository.ProfileRepository;
import com.ainotes.data.repository.SessionRepository;
import com.ainotes.di.AppModule_ProvideContextFactory;
import com.ainotes.di.AppModule_ProvideDatabaseFactory;
import com.ainotes.di.AppModule_ProvideNoteSessionDaoFactory;
import com.ainotes.di.FirebaseModule_ProvideAuthRepositoryFactory;
import com.ainotes.di.FirebaseModule_ProvideFirebaseAuthFactory;
import com.ainotes.di.FirebaseModule_ProvideFirebaseFirestoreFactory;
import com.ainotes.di.FirebaseModule_ProvideFirebaseStorageFactory;
import com.ainotes.di.FirebaseModule_ProvideProfileRepositoryFactory;
import com.ainotes.service.DocumentProcessingService;
import com.ainotes.service.DocumentProcessingService_MembersInjector;
import com.ainotes.ui.screens.history.HistoryViewModel;
import com.ainotes.ui.screens.history.HistoryViewModel_HiltModules;
import com.ainotes.ui.screens.home.HomeViewModel;
import com.ainotes.ui.screens.home.HomeViewModel_HiltModules;
import com.ainotes.ui.screens.login.LoginViewModel;
import com.ainotes.ui.screens.login.LoginViewModel_HiltModules;
import com.ainotes.ui.screens.profile.ProfileViewModel;
import com.ainotes.ui.screens.profile.ProfileViewModel_HiltModules;
import com.ainotes.ui.screens.profile.SplashViewModel;
import com.ainotes.ui.screens.profile.SplashViewModel_HiltModules;
import com.ainotes.ui.screens.results.ResultsViewModel;
import com.ainotes.ui.screens.results.ResultsViewModel_HiltModules;
import com.ainotes.util.FileHelper;
import com.ainotes.util.OcrHelper;
import com.ainotes.util.PdfChunker;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerAiNotesApplication_HiltComponents_SingletonC {
  private DaggerAiNotesApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public AiNotesApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements AiNotesApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public AiNotesApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements AiNotesApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public AiNotesApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements AiNotesApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public AiNotesApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements AiNotesApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AiNotesApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements AiNotesApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AiNotesApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements AiNotesApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public AiNotesApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements AiNotesApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public AiNotesApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends AiNotesApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends AiNotesApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends AiNotesApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends AiNotesApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(6).put(LazyClassKeyProvider.com_ainotes_ui_screens_history_HistoryViewModel, HistoryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_ainotes_ui_screens_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_ainotes_ui_screens_login_LoginViewModel, LoginViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_ainotes_ui_screens_profile_ProfileViewModel, ProfileViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_ainotes_ui_screens_results_ResultsViewModel, ResultsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_ainotes_ui_screens_profile_SplashViewModel, SplashViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectThemePreferences(instance, singletonCImpl.themePreferencesProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_ainotes_ui_screens_profile_SplashViewModel = "com.ainotes.ui.screens.profile.SplashViewModel";

      static String com_ainotes_ui_screens_results_ResultsViewModel = "com.ainotes.ui.screens.results.ResultsViewModel";

      static String com_ainotes_ui_screens_history_HistoryViewModel = "com.ainotes.ui.screens.history.HistoryViewModel";

      static String com_ainotes_ui_screens_home_HomeViewModel = "com.ainotes.ui.screens.home.HomeViewModel";

      static String com_ainotes_ui_screens_login_LoginViewModel = "com.ainotes.ui.screens.login.LoginViewModel";

      static String com_ainotes_ui_screens_profile_ProfileViewModel = "com.ainotes.ui.screens.profile.ProfileViewModel";

      @KeepFieldType
      SplashViewModel com_ainotes_ui_screens_profile_SplashViewModel2;

      @KeepFieldType
      ResultsViewModel com_ainotes_ui_screens_results_ResultsViewModel2;

      @KeepFieldType
      HistoryViewModel com_ainotes_ui_screens_history_HistoryViewModel2;

      @KeepFieldType
      HomeViewModel com_ainotes_ui_screens_home_HomeViewModel2;

      @KeepFieldType
      LoginViewModel com_ainotes_ui_screens_login_LoginViewModel2;

      @KeepFieldType
      ProfileViewModel com_ainotes_ui_screens_profile_ProfileViewModel2;
    }
  }

  private static final class ViewModelCImpl extends AiNotesApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<HistoryViewModel> historyViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LoginViewModel> loginViewModelProvider;

    private Provider<ProfileViewModel> profileViewModelProvider;

    private Provider<ResultsViewModel> resultsViewModelProvider;

    private Provider<SplashViewModel> splashViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.loginViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.resultsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.splashViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(6).put(LazyClassKeyProvider.com_ainotes_ui_screens_history_HistoryViewModel, ((Provider) historyViewModelProvider)).put(LazyClassKeyProvider.com_ainotes_ui_screens_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_ainotes_ui_screens_login_LoginViewModel, ((Provider) loginViewModelProvider)).put(LazyClassKeyProvider.com_ainotes_ui_screens_profile_ProfileViewModel, ((Provider) profileViewModelProvider)).put(LazyClassKeyProvider.com_ainotes_ui_screens_results_ResultsViewModel, ((Provider) resultsViewModelProvider)).put(LazyClassKeyProvider.com_ainotes_ui_screens_profile_SplashViewModel, ((Provider) splashViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_ainotes_ui_screens_profile_SplashViewModel = "com.ainotes.ui.screens.profile.SplashViewModel";

      static String com_ainotes_ui_screens_profile_ProfileViewModel = "com.ainotes.ui.screens.profile.ProfileViewModel";

      static String com_ainotes_ui_screens_home_HomeViewModel = "com.ainotes.ui.screens.home.HomeViewModel";

      static String com_ainotes_ui_screens_history_HistoryViewModel = "com.ainotes.ui.screens.history.HistoryViewModel";

      static String com_ainotes_ui_screens_login_LoginViewModel = "com.ainotes.ui.screens.login.LoginViewModel";

      static String com_ainotes_ui_screens_results_ResultsViewModel = "com.ainotes.ui.screens.results.ResultsViewModel";

      @KeepFieldType
      SplashViewModel com_ainotes_ui_screens_profile_SplashViewModel2;

      @KeepFieldType
      ProfileViewModel com_ainotes_ui_screens_profile_ProfileViewModel2;

      @KeepFieldType
      HomeViewModel com_ainotes_ui_screens_home_HomeViewModel2;

      @KeepFieldType
      HistoryViewModel com_ainotes_ui_screens_history_HistoryViewModel2;

      @KeepFieldType
      LoginViewModel com_ainotes_ui_screens_login_LoginViewModel2;

      @KeepFieldType
      ResultsViewModel com_ainotes_ui_screens_results_ResultsViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.ainotes.ui.screens.history.HistoryViewModel 
          return (T) new HistoryViewModel(singletonCImpl.sessionRepositoryProvider.get());

          case 1: // com.ainotes.ui.screens.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.geminiRepositoryProvider.get(), singletonCImpl.sessionRepositoryProvider.get(), singletonCImpl.fileHelperProvider.get(), singletonCImpl.firebaseSyncRepositoryProvider.get(), singletonCImpl.provideProfileRepositoryProvider.get());

          case 2: // com.ainotes.ui.screens.login.LoginViewModel 
          return (T) new LoginViewModel(singletonCImpl.provideAuthRepositoryProvider.get());

          case 3: // com.ainotes.ui.screens.profile.ProfileViewModel 
          return (T) new ProfileViewModel(singletonCImpl.provideAuthRepositoryProvider.get(), singletonCImpl.provideProfileRepositoryProvider.get(), singletonCImpl.themePreferencesProvider.get());

          case 4: // com.ainotes.ui.screens.results.ResultsViewModel 
          return (T) new ResultsViewModel(singletonCImpl.sessionRepositoryProvider.get());

          case 5: // com.ainotes.ui.screens.profile.SplashViewModel 
          return (T) new SplashViewModel(singletonCImpl.provideAuthRepositoryProvider.get(), singletonCImpl.provideProfileRepositoryProvider.get(), singletonCImpl.userPreferencesProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends AiNotesApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends AiNotesApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectDocumentProcessingService(
        DocumentProcessingService documentProcessingService) {
      injectDocumentProcessingService2(documentProcessingService);
    }

    @CanIgnoreReturnValue
    private DocumentProcessingService injectDocumentProcessingService2(
        DocumentProcessingService instance) {
      DocumentProcessingService_MembersInjector.injectGeminiRepository(instance, singletonCImpl.geminiRepositoryProvider.get());
      DocumentProcessingService_MembersInjector.injectSessionRepository(instance, singletonCImpl.sessionRepositoryProvider.get());
      DocumentProcessingService_MembersInjector.injectFileHelper(instance, singletonCImpl.fileHelperProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends AiNotesApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<Context> provideContextProvider;

    private Provider<ThemePreferences> themePreferencesProvider;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<NoteSessionDao> provideNoteSessionDaoProvider;

    private Provider<SessionRepository> sessionRepositoryProvider;

    private Provider<PdfChunker> pdfChunkerProvider;

    private Provider<OcrHelper> ocrHelperProvider;

    private Provider<FileHelper> fileHelperProvider;

    private Provider<GeminiRepository> geminiRepositoryProvider;

    private Provider<FirebaseFirestore> provideFirebaseFirestoreProvider;

    private Provider<FirebaseStorage> provideFirebaseStorageProvider;

    private Provider<FirebaseSyncRepository> firebaseSyncRepositoryProvider;

    private Provider<ProfileRepository> provideProfileRepositoryProvider;

    private Provider<FirebaseAuth> provideFirebaseAuthProvider;

    private Provider<AuthRepository> provideAuthRepositoryProvider;

    private Provider<UserPreferences> userPreferencesProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideContextProvider = DoubleCheck.provider(new SwitchingProvider<Context>(singletonCImpl, 1));
      this.themePreferencesProvider = DoubleCheck.provider(new SwitchingProvider<ThemePreferences>(singletonCImpl, 0));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 4));
      this.provideNoteSessionDaoProvider = DoubleCheck.provider(new SwitchingProvider<NoteSessionDao>(singletonCImpl, 3));
      this.sessionRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SessionRepository>(singletonCImpl, 2));
      this.pdfChunkerProvider = DoubleCheck.provider(new SwitchingProvider<PdfChunker>(singletonCImpl, 6));
      this.ocrHelperProvider = DoubleCheck.provider(new SwitchingProvider<OcrHelper>(singletonCImpl, 7));
      this.fileHelperProvider = DoubleCheck.provider(new SwitchingProvider<FileHelper>(singletonCImpl, 8));
      this.geminiRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<GeminiRepository>(singletonCImpl, 5));
      this.provideFirebaseFirestoreProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseFirestore>(singletonCImpl, 10));
      this.provideFirebaseStorageProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseStorage>(singletonCImpl, 11));
      this.firebaseSyncRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseSyncRepository>(singletonCImpl, 9));
      this.provideProfileRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ProfileRepository>(singletonCImpl, 12));
      this.provideFirebaseAuthProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuth>(singletonCImpl, 14));
      this.provideAuthRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepository>(singletonCImpl, 13));
      this.userPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<UserPreferences>(singletonCImpl, 15));
    }

    @Override
    public void injectAiNotesApplication(AiNotesApplication aiNotesApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.ainotes.data.local.ThemePreferences 
          return (T) new ThemePreferences(singletonCImpl.provideContextProvider.get());

          case 1: // android.content.Context 
          return (T) AppModule_ProvideContextFactory.provideContext(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.ainotes.data.repository.SessionRepository 
          return (T) new SessionRepository(singletonCImpl.provideNoteSessionDaoProvider.get());

          case 3: // com.ainotes.data.local.NoteSessionDao 
          return (T) AppModule_ProvideNoteSessionDaoFactory.provideNoteSessionDao(singletonCImpl.provideDatabaseProvider.get());

          case 4: // com.ainotes.data.local.AppDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.ainotes.data.repository.GeminiRepository 
          return (T) new GeminiRepository(singletonCImpl.pdfChunkerProvider.get(), singletonCImpl.ocrHelperProvider.get(), singletonCImpl.fileHelperProvider.get());

          case 6: // com.ainotes.util.PdfChunker 
          return (T) new PdfChunker(singletonCImpl.provideContextProvider.get());

          case 7: // com.ainotes.util.OcrHelper 
          return (T) new OcrHelper(singletonCImpl.provideContextProvider.get());

          case 8: // com.ainotes.util.FileHelper 
          return (T) new FileHelper(singletonCImpl.provideContextProvider.get());

          case 9: // com.ainotes.data.repository.FirebaseSyncRepository 
          return (T) new FirebaseSyncRepository(singletonCImpl.provideContextProvider.get(), singletonCImpl.provideFirebaseFirestoreProvider.get(), singletonCImpl.provideFirebaseStorageProvider.get());

          case 10: // com.google.firebase.firestore.FirebaseFirestore 
          return (T) FirebaseModule_ProvideFirebaseFirestoreFactory.provideFirebaseFirestore();

          case 11: // com.google.firebase.storage.FirebaseStorage 
          return (T) FirebaseModule_ProvideFirebaseStorageFactory.provideFirebaseStorage();

          case 12: // com.ainotes.data.repository.ProfileRepository 
          return (T) FirebaseModule_ProvideProfileRepositoryFactory.provideProfileRepository(singletonCImpl.provideFirebaseFirestoreProvider.get());

          case 13: // com.ainotes.data.repository.AuthRepository 
          return (T) FirebaseModule_ProvideAuthRepositoryFactory.provideAuthRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideFirebaseAuthProvider.get(), singletonCImpl.sessionRepositoryProvider.get());

          case 14: // com.google.firebase.auth.FirebaseAuth 
          return (T) FirebaseModule_ProvideFirebaseAuthFactory.provideFirebaseAuth();

          case 15: // com.ainotes.data.local.UserPreferences 
          return (T) new UserPreferences(singletonCImpl.provideContextProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
