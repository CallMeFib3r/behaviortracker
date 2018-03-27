package fr.bowser.behaviortracker.home

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import fr.bowser.behaviortracker.R
import fr.bowser.behaviortracker.config.BehaviorTrackerApp
import fr.bowser.behaviortracker.createtimer.CreateTimerDialog
import fr.bowser.behaviortracker.shortcut.TimerShortcutManager
import fr.bowser.behaviortracker.timerlist.TimerFragment
import javax.inject.Inject

class HomeActivity : AppCompatActivity(), HomeContract.View {

    @Inject
    lateinit var presenter: HomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupGraph()

        initializeToolbar()

        displayTimerFragment()

        manageIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        manageIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onPause() {
        presenter.stop()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_reset_all -> {
                presenter.onClickResetAll()
                return true
            }
        }
        return false
    }

    override fun displayResetAllDialog() {
        val message = resources.getString(R.string.home_dialog_confirm_reset_all_timers)
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
                .setPositiveButton(android.R.string.yes, { dialog, which ->
                    presenter.onClickResetAllTimers()
                })
                .setNegativeButton(android.R.string.no, { dialog, which ->
                    // do nothing
                })
                .show()
    }

    private fun setupGraph() {
        val build = DaggerHomeComponent.builder()
                .behaviorTrackerAppComponent(BehaviorTrackerApp.getAppComponent(this))
                .homeModule(HomeModule(this))
                .build()
        build.inject(this)
    }

    private fun initializeToolbar() {
        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(myToolbar)
    }

    private fun displayTimerFragment() {
        supportFragmentManager.inTransaction {
            replace(R.id.fragment_container, TimerFragment(), TimerFragment.TAG)
        }
    }

    private fun manageIntent(intent: Intent?) {
        when {
            intent?.action == CREATE_TIMER_FROM_SHORTCUT -> CreateTimerDialog.showDialog(this, true)
            intent?.action == TimerShortcutManager.START_TIMER_FROM_SHORTCUT -> presenter.startTimer(getTimerIdFromIntent(intent))
        }
    }

    private fun getTimerIdFromIntent(intent: Intent?): Long {
        val timerId = intent?.getLongExtra(TimerShortcutManager.SHORTCUT_KEY_TIMER_ID,
                -1)
        if (timerId == -1L || timerId == null) {
            throw IllegalArgumentException("No data with key SHORTCUT_KEY_TIMER_ID has been " +
                    "attached to the intent.")
        }
        return timerId
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() ->
    FragmentTransaction) {
        beginTransaction().func().commit()
    }

    companion object {
        const val CREATE_TIMER_FROM_SHORTCUT = "android.intent.action.CREATE_TIMER"
    }

}
