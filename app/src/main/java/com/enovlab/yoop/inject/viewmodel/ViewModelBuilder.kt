package com.enovlab.yoop.inject.viewmodel

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
internal abstract class ViewModelBuilder {

    @Binds
    internal abstract fun bindViewModelFactory(factory: YoopViewModelFactory): ViewModelProvider.Factory
}
