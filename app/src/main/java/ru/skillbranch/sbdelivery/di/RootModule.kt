package ru.skillbranch.sbdelivery.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import ru.skillbranch.sbdelivery.screens.root.logic.Command
import ru.skillbranch.sbdelivery.screens.root.logic.Eff


@InstallIn(ViewModelComponent::class)
@Module
object RootModule {

    @ViewModelScoped
    @Provides
    fun provideCommandChanel() : Channel<Command> = Channel()

    @ViewModelScoped
    @Provides
    fun provideNotificationChanel() : Channel<Eff.Notification> = Channel()

    @Provides
    fun provideJob(): Job = SupervisorJob()
}
