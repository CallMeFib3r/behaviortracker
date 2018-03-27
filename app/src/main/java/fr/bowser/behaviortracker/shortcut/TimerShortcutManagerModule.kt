package fr.bowser.behaviortracker.shortcut

import android.content.Context
import dagger.Module
import dagger.Provides
import fr.bowser.behaviortracker.timer.TimeManager
import fr.bowser.behaviortracker.timer.TimerListManager
import javax.inject.Singleton

@Module
internal class TimerShortcutManagerModule {

    @Singleton
    @Provides
    fun provideTimerShortManager(context: Context,
                                 timeManager: TimeManager,
                                 timerListManager: TimerListManager): TimerShortcutManager {
        return TimerShortcutManager(context, timeManager, timerListManager)
    }

}