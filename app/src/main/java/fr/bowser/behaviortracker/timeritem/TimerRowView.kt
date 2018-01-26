package fr.bowser.behaviortracker.timeritem

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.widget.ImageView
import android.widget.TextView
import fr.bowser.behaviortracker.R
import fr.bowser.behaviortracker.config.BehaviorTrackerApp
import fr.bowser.behaviortracker.timer.Timer
import fr.bowser.behaviortracker.utils.TimeConverter
import javax.inject.Inject

class TimerRowView(context: Context) : ConstraintLayout(context), TimerItemContract.View {

    private val chrono: TextView
    private val name: TextView
    private val menu: ImageView
    private val reduceChrono: TextView
    private val increaseChrono: TextView

    var listener: ActionListener? = null

    @Inject
    lateinit var presenter: TimerItemPresenter

    init {
        setupGraph()

        inflate(context, R.layout.item_timer, this)

        val padding = resources.getDimensionPixelOffset(R.dimen.default_space)
        setPadding(padding, padding, padding, padding)

        setOnClickListener { listener?.onTimerStateChange() }

        chrono = findViewById(R.id.timer_chrono)
        name = findViewById(R.id.timer_name)
        menu = findViewById(R.id.timer_menu)
        reduceChrono = findViewById(R.id.timer_reduce_time)
        reduceChrono.setOnClickListener { listener?.onClickDecreaseTime() }
        increaseChrono = findViewById(R.id.timer_increase_time)
        increaseChrono.setOnClickListener { listener?.onClickIncreaseTime() }
    }

    private fun setupGraph(){
        val component = DaggerTimerItemComponent.builder()
                .behaviorTrackerAppComponent(BehaviorTrackerApp.getAppComponent(context))
                .timerItemModule(TimerItemModule(this))
                .build()
        component.inject(this)
    }

    fun setTimer(timer: Timer) {
        chrono.text = TimeConverter.convertSecondsToHumanTime(timer.currentTime)
        name.text = timer.name
        setBackgroundColor(timer.color)
    }

    interface ActionListener {
        fun onTimerStateChange()
        fun onClickIncreaseTime()
        fun onClickDecreaseTime()
    }

}