package fr.bowser.behaviortracker.timeritem

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import fr.bowser.behaviortracker.R
import fr.bowser.behaviortracker.config.BehaviorTrackerApp
import fr.bowser.behaviortracker.showmode.ShowModeActivity
import fr.bowser.behaviortracker.timer.Timer
import fr.bowser.behaviortracker.utils.ColorUtils
import fr.bowser.behaviortracker.utils.TimeConverter
import javax.inject.Inject


class TimerRowView(context: Context) :
        CardView(context),
        TimerItemContract.View {

    private val chrono: TextView
    private val tvName: TextView
    private val menu: ImageView
    private val reduceChrono: TextView
    private val increaseChrono: TextView
    private val color: View
    private val btnPlayPause: ImageView

    @Inject
    lateinit var presenter: TimerItemPresenter

    init {
        setupGraph()

        inflate(context, R.layout.item_timer, this)

        // card radius
        radius = resources.getDimension(R.dimen.default_space_half)

        setOnClickListener { manageClickPlayPauseButton() }

        btnPlayPause = findViewById(R.id.timer_play_pause)

        color = findViewById(R.id.timer_color)
        color.setOnClickListener { presenter.onClickCard() }
        chrono = findViewById(R.id.timer_chrono)
        tvName = findViewById(R.id.timer_name)
        menu = findViewById(R.id.timer_menu)
        menu.setOnClickListener { displayMenu() }
        reduceChrono = findViewById(R.id.timer_reduce_time)
        reduceChrono.setOnClickListener { presenter.onClickDecreaseTime() }
        increaseChrono = findViewById(R.id.timer_increase_time)
        increaseChrono.setOnClickListener { presenter.onClickIncreaseTime() }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter.start()
    }

    override fun onDetachedFromWindow() {
        presenter.stop()
        super.onDetachedFromWindow()
    }

    override fun updateTimeModification(timeModification: Int) {
        reduceChrono.text = timeModification.toString()
        increaseChrono.text = timeModification.toString()
    }

    fun setTimer(timer: Timer) {
        presenter.setTimer(timer)

        chrono.text = TimeConverter.convertSecondsToHumanTime(timer.currentTime)
        tvName.text = timer.name
        color.setBackgroundColor(ColorUtils.getColor(context!!, timer.color))

        updateBtnPlayPause(timer.isActivate)
    }

    override fun timerUpdated(newTime: Long) {
        chrono.text = TimeConverter.convertSecondsToHumanTime(newTime)
    }

    override fun nameUpdated(newName: String) {
        tvName.text = newName
    }

    private fun displayMenu() {
        val popup = PopupMenu(context, menu)

        popup.menuInflater.inflate(R.menu.menu_item_timer, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_timer_reset -> {
                    presenter.onClickResetTimer()
                }
                R.id.item_timer_delete -> {
                    displayRemoveConfirmationDialog()
                }
                R.id.item_timer_rename -> {
                    presenter.onClickRenameTimer()
                }
            }
            true
        }

        popup.show()
    }

    private fun manageClickPlayPauseButton() {
        presenter.timerStateChange()
    }

    private fun updateBtnPlayPause(isActivate: Boolean) {
        if (isActivate) {
            btnPlayPause.setImageResource(R.drawable.ic_pause)
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play)
        }
    }

    override fun displayRenameDialog(oldName: String) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setMessage(resources.getString(R.string.timer_row_rename))

        val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_rename_timer, null)
        val input = rootView.findViewById<EditText>(R.id.edittext)

        input.setText(oldName)
        input.setSelection(input.text.length)

        alertDialog.setView(rootView)

        alertDialog.setPositiveButton(android.R.string.yes, { dialog, which ->
            val newName = input.text.toString()
            presenter.onTimerNameUpdated(newName)
        })

        alertDialog.setNegativeButton(android.R.string.no, { dialog, which -> dialog.cancel() })

        alertDialog.show()
    }

    override fun statusUpdated(activate: Boolean) {
        updateBtnPlayPause(activate)
    }

    override fun timerRenamed(name: String) {
        tvName.text = name
    }

    override fun startShowMode(id: Long) {
        ShowModeActivity.startActivity(context, id)
    }

    private fun displayRemoveConfirmationDialog() {
        val message = resources.getString(R.string.item_timer_remove_message)
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
                .setPositiveButton(android.R.string.yes, { dialog, which ->
                    presenter.onClickDeleteTimer()
                })
                .setNegativeButton(android.R.string.no, { dialog, which ->
                    // do nothing
                })
                .show()
    }

    private fun setupGraph() {
        val component = DaggerTimerItemComponent.builder()
                .behaviorTrackerAppComponent(BehaviorTrackerApp.getAppComponent(context))
                .timerItemModule(TimerItemModule(this))
                .build()
        component.inject(this)
    }

}