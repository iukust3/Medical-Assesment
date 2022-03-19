package com.example.medicalassesment.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicalassesment.ViewModels.InspectionViewModel
import com.example.medicalassesment.ViewModels.LoginViewModel
import com.example.medicalassesment.ViewModels.SurveyViewModel
import com.example.medicalassesment.ViewModels.UploadViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SurveyViewModel::class)
    abstract fun bindSurvayViewModel(viewModel: SurveyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindShowsViewModel(showsViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InspectionViewModel::class)
    abstract fun bindInspectionViewModel(showsViewModel: InspectionViewModel): ViewModel


    /* @Binds
     @IntoMap
     @ViewModelKey(FavoriteShowsViewModel::class)
     abstract fun bindFavoriteShowsViewModel(favoriteShowsViewModel: FavoriteShowsViewModel): ViewModel
 */
}
