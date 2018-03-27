package fr.bowser.behaviortracker.config

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fr.bowser.behaviortracker.database.DatabaseManager
import fr.bowser.behaviortracker.database.DatabaseManagerModule
import fr.bowser.behaviortracker.notification.TimeNotificationManagerModule
import fr.bowser.behaviortracker.notification.TimerNotificationManager
import fr.bowser.behaviortracker.shortcut.TimerShortcutManager
import fr.bowser.behaviortracker.shortcut.TimerShortcutManagerModule
import fr.bowser.behaviortracker.timer.TimeManager
import fr.bowser.behaviortracker.timer.TimeManagerModule
import fr.bowser.behaviortracker.timer.TimerListManager
import fr.bowser.behaviortracker.timer.TimerListManagerModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(DatabaseManagerModule::class,
        TimeManagerModule::class,
        TimeNotificationManagerModule::class,
        TimerShortcutManagerModule::class,
        TimerListManagerModule::class))
interface BehaviorTrackerAppComponent {

    fun provideDatabaseManager(): DatabaseManager

    fun provideTimeManager(): TimeManager

    fun provideTimerListManager(): TimerListManager

    fun provideTimerNotificationManager(): TimerNotificationManager

    fun provideTimerShortcutManager(): TimerShortcutManager

    @Component.Builder
    interface Builder {

        fun build(): BehaviorTrackerAppComponent
        @BindsInstance
        fun context(context: Context): Builder
    }

}