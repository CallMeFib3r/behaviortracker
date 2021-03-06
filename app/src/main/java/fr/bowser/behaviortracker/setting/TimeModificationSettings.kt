package fr.bowser.behaviortracker.setting

import android.content.Context
import android.content.res.Resources
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import fr.bowser.behaviortracker.R

class TimeModificationSettings(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    private var defaultValue: Int = 0

    private lateinit var slider: SeekBar

    private lateinit var timerModificationText: TextView

    private var res: Resources = context.resources

    init {
        defaultValue = res.getInteger(R.integer.settings_default_value_time_modification)

        dialogLayoutResource = R.layout.dialog_preference_automix
    }

    override fun onCreateDialogView(): View {
        val view = super.onCreateDialogView()

        timerModificationText = view.findViewById(R.id.tv_time_modification)

        slider = view.findViewById(R.id.slider_time_modification)
        slider.setOnSeekBarChangeListener(CustomSeekBarChangeListener())
        slider.max = MAX_DURATION - MIN_VALUE_TIME

        return view
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        val timerModification = getPersistedInt(defaultValue) - MIN_VALUE_TIME

        slider.progress = timerModification
        timerModificationText.text = convertTime(timerModification).toString()
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (positiveResult) {
            val value = slider.progress
            persistInt(convertTime(value))
        }
    }

    private fun convertTime(progress: Int): Int {
        return MIN_VALUE_TIME + progress
    }

    private inner class CustomSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            timerModificationText.text = convertTime(progress).toString()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            // nothing to do
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            // nothing to do
        }
    }

    companion object {

        private const val MAX_DURATION = 60

        private const val MIN_VALUE_TIME = 5
    }
}