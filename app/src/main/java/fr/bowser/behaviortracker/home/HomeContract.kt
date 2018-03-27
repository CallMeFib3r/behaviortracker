package fr.bowser.behaviortracker.home

interface HomeContract {

    interface View{

        fun displayResetAllDialog()

    }

    interface Presenter{

        fun start()

        fun stop()

        fun onClickResetAll()

        fun onClickResetAllTimers()

        fun startTimer(timerId: Long)

    }

}