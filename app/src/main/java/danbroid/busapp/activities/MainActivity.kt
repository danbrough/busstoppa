package danbroid.busapp.activities


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import danbroid.busapp.*
import danbroid.busapp.databinding.ActivityMainBinding
import danbroid.busapp.models.AppModel
import danbroid.busapp.ui.AboutDialogHelper
import danbroid.busapp.ui.BusStopSearchView
import danbroid.touchprompt.TouchPrompt
import danbroid.touchprompt.touchPrompt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory


class MainActivity : AppCompatActivity(), MainActivityInterface {

  protected val navHostFragment: NavHostFragment
    get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

  private val navController: NavController
    get() = navHostFragment.navController

  internal lateinit var searchView: BusStopSearchView

  override val appModel by viewModels<AppModel>()

  private lateinit var binding: ActivityMainBinding

  override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

  override fun onCreate(savedInstanceState: Bundle?) {
    log.debug("onCreate() state: $savedInstanceState")
    setTheme(R.style.AppTheme_NoActionBar)
    // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    super.onCreate(savedInstanceState)

    val veryStrict = false
    if (BuildConfig.DEBUG && veryStrict) {
      StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
          .detectCustomSlowCalls()
          .detectDiskWrites()
          .detectNetwork()
          .penaltyLog()
          .build()
      )
      StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
          .detectLeakedSqlLiteObjects()
          .detectLeakedClosableObjects()
          .penaltyLog()
          .penaltyDeath()
          .build()
      )
    }

    binding = ActivityMainBinding.inflate(layoutInflater).also {
      setContentView(it.root)
    }


    setSupportActionBar(binding.toolbar)

    val navController = navHostFragment.navController

    navController.graph = navController.createBusAppNavGraph(this)


    setupActionBarWithNavController(navController)


    var appBarBackground: Drawable? = null
    var appBarElevation: Float = -1.0f
    var layoutParams: CoordinatorLayout.LayoutParams? = null
    var appBarBehavior: CoordinatorLayout.Behavior<*>? = null

    navController.addOnDestinationChangedListener { controller, destination, arguments ->
      log.info("onDestinationChanged() $destination arg: $arguments")
      if (appBarBackground == null) {
        appBarBackground = binding.appbar.background
        appBarElevation = ViewCompat.getElevation(binding.appbar)
        layoutParams =
          findViewById<View>(R.id.nav_host_fragment).layoutParams as CoordinatorLayout.LayoutParams
        appBarBehavior = layoutParams!!.behavior
      }
      when (destination.id) {
        NavGraph.dest.map -> {
          //binding.appbar.alpha = 0.5f
          binding.appbar.setBackgroundColor(
            ResourcesCompat.getColor(
              resources,
              R.color.colorPrimaryTransparent,
              null
            )
          )
          layoutParams?.behavior = null
          ViewCompat.setElevation(binding.appbar, 0f)
        }
        else -> {
          //binding.appbar.alpha = 1f
          binding.appbar.setBackgroundColor(
            ResourcesCompat.getColor(
              resources,
              R.color.colorPrimary,
              null
            )
          )
          ViewCompat.setElevation(binding.appbar, appBarElevation)
          layoutParams?.behavior = appBarBehavior

        }
      }
      /*    if (appBarBackground == null){
            appBarBackground = binding.appbar.background
            appBarElevation = ViewCompat.getElevation(binding.appbar)
            layoutParams = findViewById<View>(R.id.nav_host_fragment).layoutParams as CoordinatorLayout.LayoutParams
            appBarBehavior = layoutParams!!.behavior
          }
          when (destination.id) {
            NavGraph.dest.map -> {
              binding.toolbar.setTitleTextColor(Color.BLUE)
              layoutParams?.behavior = null
              ViewCompat.setElevation(binding.appbar, 0f)
              binding.appbar.background = null
            }
            else -> {
              layoutParams?.behavior = appBarBehavior
              ViewCompat.setElevation(binding.appbar, appBarElevation)
              binding.appbar.background = appBarBackground
            }

          }*/
    }

//    var toolbarAnim: ViewPropertyAnimator? = null

    closeSearchView()
    appModel.errorMessage.value = null


    // supportActionBar!!.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 1)

/*    appModel.searchBusy.observe(this, { busy ->

      *//*findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)?.let { swipeLayout ->
        swipeLayout.isRefreshing = busy
      }*//*

      if (busy) {
        binding.progressBar.visibility = View.VISIBLE
        appModel.errorMessage.value = null
      } else {
        binding.progressBar.visibility = View.GONE
      }
    })*/

    appModel.errorMessage.observe(this, Observer(::showError))

    appModel.results.observe(this, Observer
    {
      searchView.results = it
    })

    searchView = BusStopSearchView(this)

    searchView.queryHint = getString(R.string.msg_search_button)

/*  searchView.onActionViewExpandedListener = {
    if (!it) binding.fragment.requestFocus()
  }*/

    searchView.onStopSelectListener = {
      addToHistoryAndShowDepartures(it.code)
    }

    searchView.setOnQueryTextListener(
      object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(query: String): Boolean = false

        override fun onQueryTextChange(query: String): Boolean {
          appModel.performSearch(query)
          return true
        }
      })

    processIntent(intent)

    //if (BuildConfig.DEBUG) TouchPrompt.clearPrefs(this)

    AboutDialogHelper(this).showAbout {
      touchPrompt(HelpCodes.SEARCH_BUTTON) {
        setPrimaryText(R.string.lbl_search_button)
        setSecondaryText(R.string.msg_search_button)
        initialDelay = 1000
        targetID = R.id.action_search
        onDismissed = {
          touchPrompt(HelpCodes.MAP_BUTTON) {
            initialDelay = 500
            setPrimaryText(R.string.lbl_map_button)
            setSecondaryText(R.string.msg_map_button)
            targetID = R.id.action_map
          }
        }
      }
    }
  }

/*  fun showDepartures(stopCode: String) =
    lifecycleScope.launch {
      log.info("showDepartures() $stopCode")
      metlinkOld.stopInfo(stopCode).also {
        showDepartures(it)
      }
    }.invokeOnCompletion {

      if (it != null) {
        if (it !is CancellationException)
          appModel.errorMessage.postValue("Network error: ${it.message}")
      }
    }


  fun showDepartures(stop: BusStop) {
    appModel.currentStop.value = stop
    //setContent(DeparturesFragment(), addToBackStack = addToBackStack)
  }*/


  override fun showBrowser(url: String) {
    startActivity(
      Intent(Intent.ACTION_VIEW)
        .setData(Uri.parse(url))
    )

//    setContent(BrowserFragment.newInstance(url))
  }

  override fun showMap(stopCode: String?) =
    navController.showMap(stopCode)


  override fun setTitle(name: CharSequence) {
    binding.toolbar.title = name
  }


  //private var errorDialog: AlertDialog? = null
  private var alertDialog: androidx.appcompat.app.AlertDialog? = null

  private fun showError(msg: String?) {
    log.info("showError(): $msg")
    alertDialog?.cancel().also {
      alertDialog = null
    }

    msg ?: return

    findViewById<View>(R.id.progress_bar).visibility = View.GONE
    alertDialog = MaterialAlertDialogBuilder(this).apply {
      setTitle(R.string.lbl_alert)
      setMessage(msg)
      setIcon(R.drawable.ic_warning_small)
      setPositiveButton(android.R.string.ok, null)
    }.show()
  }

  fun processIntent(intent: Intent) {
    log.info("processIntent(): $intent")
    intent.extras?.getString(INTENT_EXTRA_STOP_CODE)?.also { code ->
      addToHistoryAndShowDepartures(code)
    }
  }

  override fun addToHistoryAndShowDepartures(code: String) {
    log.info("addToHistoryAndShowDepartures() $code")
    lifecycleScope.launch(Dispatchers.IO) {
      appModel.addStop(code).also {
        log.info("showing departures for $code")
        withContext(Dispatchers.Main) {
          navController.showDepartures(code)
        }
      }
    }.invokeOnCompletion {
      if (it != null)
        log.error(it.message, it)
    }
  }

  override val context: Context = this

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    processIntent(intent)
  }

  private var lastTimeBackPressed = 0L

  override fun onBackPressed() {
    log.debug("onBackPressed back count: ${supportFragmentManager.backStackEntryCount}")

    if (searchView.closeSearchView()) return

    if (navController.navigateUp()) return
/*
  currentContent?.let {
    it is SupportsBackButton && it.onBackPressed() && return
  }
*/


/*
    if (supportFragmentManager.backStackEntryCount > 0) {
      supportFragmentManager.popBackStack()
      return
    }*/

    val now = System.currentTimeMillis()
    if (now - lastTimeBackPressed < 3000) {
      finish()
      return
    }
    lastTimeBackPressed = now
    Toast.makeText(this, R.string.msg_press_back_again_to_quit, Toast.LENGTH_SHORT).show()

  }

  private var menu: Menu? = null

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    this.menu = menu

    menuInflater.inflate(R.menu.main_activity, menu)
    super.onCreateOptionsMenu(menu)

    if (BuildConfig.DEBUG) {

      menu.add("Show test stop").setOnMenuItemClickListener {
        addToHistoryAndShowDepartures("WELL")
        true
      }

      menu.add("Test ERror").setOnMenuItemClickListener {
        appModel.errorMessage.value = "Something bad happened!"
        true
      }

      menu.add("Menu Prompt").setOnMenuItemClickListener {
        touchPrompt {
          primaryText = "Menu Prompt"
          setTargetPosition(100f, 200f)
        }
        true
      }

    }

    return true
  }


  override fun onOptionsItemSelected(item: MenuItem): Boolean =
    when (item.itemId) {
      android.R.id.home -> {
        onBackPressed()
        true
      }
      R.id.action_about -> {
        AboutDialogHelper(this).showAbout(true, true)
        true
      }
      R.id.action_reset_help -> {
        TouchPrompt.clearPrefs(this)
        val nextIntent = intent
        finish()
        startActivity(nextIntent)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }


  fun closeSearchView() = binding.toolbar.collapseActionView()


}

fun Activity?.displayToolbar(visible: Boolean): Unit =
  (this as AppCompatActivity).run {
    if (visible) {
      findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
    } else {
      findViewById<View>(R.id.toolbar).visibility = View.GONE
    }
  }


val Fragment.mainActivity: MainActivityInterface
  get() = requireActivity() as MainActivityInterface

val Fragment.appModel: AppModel
  get() = mainActivity.appModel

private val log =
  LoggerFactory.getLogger(MainActivity::class.java)


