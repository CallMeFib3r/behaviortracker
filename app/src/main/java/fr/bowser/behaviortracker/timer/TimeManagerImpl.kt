package fr.bowser.behaviortracker.timer

import android.os.Handler
import fr.bowser.behaviortracker.setting.SettingManager
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newFixedThreadPoolContext

class TimeManagerImpl(private val timerDAO: TimerDAO,
                      private val settingManager: SettingManager,
                      private val handler: Handler?) : TimeManager {

    internal val background = newFixedThreadPoolContext(2, "time_manager_bg")

    private val listeners = ArrayList<TimeManager.TimerCallback>()

    private val timerRunnable = TimerRunnable()

    private val timerList = ArrayList<Timer>()

    override fun startTimer(timer: Timer) {
        if (timer.isActivate) {
            return
        }

        if(settingManager.isOneActiveTimerAtATime()){
            stopAllRunningTimers()
        }

        timer.isActivate = true
        for (callback in listeners) {
            callback.onTimerStateChanged(timer)
        }

        if (!timerList.contains(timer)) {
            if (timerList.isEmpty()) { // start runnable
                handler?.postDelayed(timerRunnable, DELAY)
            }
            timerList.add(timer)
        }
    }

    override fun stopTimer(timer: Timer) {
        if (!timer.isActivate) {
            return
        }

        timer.isActivate = false
        for (callback in listeners) {
            callback.onTimerStateChanged(timer)
        }

        timerList.remove(timer)

        if (timerList.isEmpty()) {
            handler?.removeCallbacks(timerRunnable)
        }
    }

    override fun updateTime(timer: Timer, newTime: Long) {
        var currentNewTime = newTime
        if (currentNewTime < 0) {
            currentNewTime = 0
        }

        timer.currentTime = currentNewTime

        launch(background) {
            timerDAO.updateTimerTime(timer.id, timer.currentTime)
        }

        for (callback in listeners) {
            callback.onTimerTimeChanged(timer)
        }
    }

    override fun registerUpdateTimerCallback(callback: TimeManager.TimerCallback): Boolean {
        if (!listeners.contains(callback)) {
            return listeners.add(callback)
        }
        return false
    }

    override fun unregisterUpdateTimerCallback(callback: TimeManager.TimerCallback) {
        listeners.remove(callback)
    }

    private fun stopAllRunningTimers(){
        for (timerToStop in timerList) {
            timerToStop.isActivate = false
            for (callback in listeners) {
                callback.onTimerStateChanged(timerToStop)
            }
        }
        timerList.clear()
        handler?.removeCallbacks(timerRunnable)
    }

    inner class TimerRunnable : Runnable {
        override fun run() {
            timerList.forEach {
                updateTime(it, it.currentTime + 1)
            }

            handler?.postDelayed(this, DELAY)
        }
    }

    companion object {
        private const val DELAY = 1000L
    }

}