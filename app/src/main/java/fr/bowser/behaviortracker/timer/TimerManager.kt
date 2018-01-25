package fr.bowser.behaviortracker.timer

import android.os.Handler
import java.util.*

class TimerManager {

    private val listeners = ArrayList<UpdateTimerCallback>()

    private val handler = Handler()

    private val timerRunnable = TimerRunnable()

    fun registerUpdateTimerCallback(callback: UpdateTimerCallback): Boolean{
        if(!listeners.contains(callback)){
            // start the runnable if we will put the first listener
            if(listeners.isEmpty()){
                handler.post(timerRunnable)
            }
            return listeners.add(callback)
        }
        return false
    }

    fun unregisterUpdateTimerCallback(callback: UpdateTimerCallback){
        listeners.remove(callback)

        // stop runnable if there is no listener anymore
        if(listeners.isEmpty()){
            handler.removeCallbacks(timerRunnable)
        }
    }

    companion object {
        private val DELAY = 1000L
    }

    interface UpdateTimerCallback{

        fun timeUpdated()

    }

    inner class TimerRunnable: Runnable{
        override fun run() {
            for (listener in listeners) {
                listener.timeUpdated()
            }
            handler.postDelayed(this, DELAY)
        }
    }

}