package fr.bowser.behaviortracker.home

interface HomeContract {

    interface View {

        fun displayResetAllDialog()

        fun displaySettingsView()

        fun displayPomodoroView()

        fun displayTimerView()

    }

    interface Presenter {

        fun initialize()

        fun start()

        fun stop()

        fun onClickResetAll()

        fun onClickResetAllTimers()

        fun onClickSettings()

        fun onClickTimerView()

        fun onClickPomodoroView()

    }

}