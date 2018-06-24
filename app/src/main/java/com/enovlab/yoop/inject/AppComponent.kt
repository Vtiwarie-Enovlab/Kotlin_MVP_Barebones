package com.enovlab.yoop.inject

import com.enovlab.yoop.Application
import com.enovlab.yoop.inject.api.ApiModule
import com.enovlab.yoop.inject.viewmodel.ViewModelBuilder
import com.enovlab.yoop.ui.main.MainBuilder
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(
    AndroidSupportInjectionModule::class,
    AppModule::class,
    DatabaseModule::class,
    NetworkModule::class,
    ApiModule::class,
    ViewModelBuilder::class,
    MainBuilder::class
))
interface AppComponent : AndroidInjector<Application> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<Application>()
}
