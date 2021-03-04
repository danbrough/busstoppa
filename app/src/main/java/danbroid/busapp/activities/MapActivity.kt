package danbroid.busapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import danbroid.busapp.R
import danbroid.busapp.createBusAppNavGraph
import danbroid.busapp.databinding.ActivityMainBinding
import androidx.navigation.ui.setupActionBarWithNavController
import danbroid.busapp.NavGraph

class MapActivity : AppCompatActivity(){
  protected val navHostFragment: NavHostFragment
    get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

  private val navController: NavController
    get() = navHostFragment.navController

  private lateinit var binding:ActivityMainBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.map_activity_layout)

    binding = ActivityMainBinding.inflate(layoutInflater).also {
      setContentView(it.root)
    }


    setSupportActionBar(binding.toolbar)

    val navController = navHostFragment.navController

    navController.graph = navController.createBusAppNavGraph(this,NavGraph.dest.map_fragment)


    setupActionBarWithNavController(navController)
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MapActivity::class.java)
