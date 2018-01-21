package fr.bowser.behaviortracker.timer

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.widget.ImageView
import android.widget.TextView
import fr.bowser.behaviortracker.R
import fr.bowser.behaviortracker.model.Timer
import fr.bowser.behaviortracker.utils.TimeConverter

class TimerRowView(context: Context) : ConstraintLayout(context) {

    private val chrono: TextView
    private val name: TextView
    private val menu: ImageView
    private val reduceChrono: TextView
    private val increateChrono: TextView

    var listener: ActionListener? = null

    init {
        inflate(context, R.layout.item_timer, this)

        val padding = resources.getDimensionPixelOffset(R.dimen.default_space)
        setPadding(padding, padding, padding, padding)

        setOnClickListener { listener?.onTimerStateChange() }

        chrono = findViewById(R.id.timer_chrono)
        name = findViewById(R.id.timer_name)
        menu = findViewById(R.id.timer_menu)
        reduceChrono = findViewById(R.id.timer_reduce_time)
        reduceChrono.setOnClickListener { listener?.onClickDecreaseTime() }
        increateChrono = findViewById(R.id.timer_increase_time)
        increateChrono.setOnClickListener { listener?.onClickIncreaseTime() }
    }

    fun setTimer(timer: Timer) {
        chrono.text = TimeConverter.convertSecondsToHumanTime(timer.time)
        name.text = timer.name
        setBackgroundColor(timer.color)
    }

    interface ActionListener {
        fun onTimerStateChange()
        fun onClickIncreaseTime()
        fun onClickDecreaseTime()
    }

}