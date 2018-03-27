package fr.bowser.behaviortracker.shortcut

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import fr.bowser.behaviortracker.R
import fr.bowser.behaviortracker.home.HomeActivity
import fr.bowser.behaviortracker.timer.TimeManager
import fr.bowser.behaviortracker.timer.Timer
import fr.bowser.behaviortracker.timer.TimerListManager
import java.util.*
import kotlin.collections.ArrayList


@TargetApi(Build.VERSION_CODES.O_MR1)
class TimerShortcutManager(private val context: Context,
                           timeManager: TimeManager,
                           timerListManager: TimerListManager) :
        TimeManager.TimerCallback,
        TimerListManager.TimerCallback {


    private val shortcutManager: ShortcutManager
    private val sharedPreferences: SharedPreferences
    private val values: MutableMap<String, Int>

    init {
        timeManager.registerUpdateTimerCallback(this)
        timerListManager.registerTimerCallback(this)

        shortcutManager = context.getSystemService(ShortcutManager::class.java)
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE)

        values = sharedPreferences.all as MutableMap<String, Int>
    }

    override fun onTimerStateChanged(timer: Timer) {
        if (timer.isActivate) {
            val key = timer.id.toString()
            val oldNumberOfActivation = sharedPreferences.getInt(key, 0)
            val newNumberOfActivation = oldNumberOfActivation + 1

            values[key] = newNumberOfActivation

            val edit = sharedPreferences.edit()
            edit.putInt(key, newNumberOfActivation)
            edit.apply()

            val dynamicShortcuts = shortcutManager.dynamicShortcuts

            // do nothing if there is already a shortcut for this timer
            for (dynamicShortcut in dynamicShortcuts) {
                if (dynamicShortcut.id == timer.id.toString()) {
                    return
                }
            }

            if (dynamicShortcuts.size < 2) {
                addShortcut(timer)
            } else {
                val nbUseShortcut1 = values[dynamicShortcuts[0].id]!!
                val nbUseShortcut2 = values[dynamicShortcuts[1].id]!!

                val maxShortcut: String
                val minShortcut: String

                if (nbUseShortcut1 > nbUseShortcut2) {
                    maxShortcut = dynamicShortcuts[0].id
                    minShortcut = dynamicShortcuts[1].id
                } else {
                    maxShortcut = dynamicShortcuts[1].id
                    minShortcut = dynamicShortcuts[0].id
                }

                if (values[maxShortcut]!! < values[key]!! || values[minShortcut]!! <= values[key]!!) {
                    removeShortcut(minShortcut)
                    addShortcut(timer)
                }
            }
        }
    }

    override fun onTimerRemoved(timer: Timer) {
        val edit = sharedPreferences.edit()
        edit.remove(timer.id.toString())
        edit.apply()

        removeShortcut(timer.id.toString())
    }

    override fun onTimerAdded(timer: Timer) {
        val edit = sharedPreferences.edit()
        edit.putInt(timer.id.toString(), 0)
        edit.apply()
    }

    override fun onTimerRenamed(timer: Timer) {
        val intent = Intent(context, HomeActivity::class.java)
        intent.action = START_TIMER_FROM_SHORTCUT
        intent.putExtra(SHORTCUT_KEY_TIMER_ID, timer.id)
        val shortcut = ShortcutInfo.Builder(context, timer.id.toString())
                .setShortLabel(timer.name)
                .setIcon(Icon.createWithResource(context, R.drawable.ic_check))//TODO change icon
                .setIntent(intent)
                .build()
        val shortcutList: ArrayList<ShortcutInfo> = ArrayList()
        shortcutList.add(shortcut)
        shortcutManager.updateShortcuts(shortcutList)
    }

    override fun onTimerTimeChanged(timer: Timer) {
        //nothing to do
    }

    private fun addShortcut(timer: Timer) {
        val intent = Intent(context, HomeActivity::class.java)
        intent.action = START_TIMER_FROM_SHORTCUT
        intent.putExtra(SHORTCUT_KEY_TIMER_ID, timer.id)
        val shortcut = ShortcutInfo.Builder(context, timer.id.toString())
                .setShortLabel(timer.name)
                .setIcon(Icon.createWithResource(context, R.drawable.ic_check))//TODO change icon
                .setIntent(intent)
                .build()

        shortcutManager.addDynamicShortcuts(Arrays.asList(shortcut))
    }

    private fun removeShortcut(timerId: String) {
        val idList = ArrayList<String>()
        idList.add(timerId)
        shortcutManager.removeDynamicShortcuts(idList)
    }

    companion object {
        private const val SHARED_PREFERENCE_KEY = "timershortcutmanager.key.shared_preference"

        const val START_TIMER_FROM_SHORTCUT = "android.intent.action.START_TIMER"

        const val SHORTCUT_KEY_TIMER_ID = "timershortcutmanager.key.SHORTCUT_KEY_TIMER_ID"
    }

}