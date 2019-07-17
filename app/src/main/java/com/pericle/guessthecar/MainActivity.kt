package com.pericle.guessthecar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.pericle.guessthecar.database.Car
import com.pericle.guessthecar.database.CarDao
import com.pericle.guessthecar.database.CarDatabase
import com.pericle.guessthecar.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var database: CarDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        // prevent nav gesture if not on start destination
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, bundle: Bundle? ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
        NavigationUI.setupWithNavController(binding.navView, navController)

        val cars = listOf(
            Car(listOf("dodge_viper.jpg"), "Dodge", "Viper", "USA"),
            Car(listOf("mini_cooper.jpg"), "Mini", "Cooper", "USA"),
            Car(listOf("nissan_gtr.jpg"), "Nissan", "GTR", "Japan"),
            Car(listOf("saleen_s7.jpg"), "Saleen", "S7", "USA"),
            Car(listOf("toyota_supra.jpg"), "Toyota", "Supra", "Japan")
        )
        val application = requireNotNull(this).application
        database = CarDatabase.getInstance(application).carDao
        uiScope.launch {
//            insertAll(cars)
        }
    }

    private suspend fun insertAll(cars: List<Car>) {
        withContext(Dispatchers.IO) {
            for (car in cars) {
                database.insert(car)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}
