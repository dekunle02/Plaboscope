package com.adeleke.samad.plaboscope

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.adeleke.samad.plaboscope.database.PlaboscopeDatabase
import com.adeleke.samad.plaboscope.database.QuestionsDao
import com.adeleke.samad.plaboscope.database.StatsDao
import com.adeleke.samad.plaboscope.models.QuestionHelper
import com.adeleke.samad.plaboscope.services.AlarmHelper
import com.adeleke.samad.plaboscope.settings.SettingsActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the drawer and the navigation
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_specialty, R.id.nav_dashboard, R.id.nav_bookmark, R.id.nav_note), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out this Plab 1 prep app!: http://play.google.com/store/apps/details?id=com.adeleke.samad.plaboscope")
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
                return true
            }
            R.id.action_feedback -> {
                contactMe()
                return true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun contactMe() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "dekunle02@gmail.com"))
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val androidVersion = Build.VERSION.SDK_INT
        emailIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Feedback from Plaboscope App\nManufacturer: $manufacturer \nmodel: $model \nOs: $androidVersion"
        )
        startActivity(emailIntent)
    }

/*
*
    private fun endLottieAnim() {
        val fragmentView = findViewById<View>(R.id.nav_host_fragment)
        fragmentView.visibility = View.VISIBLE
        animFrame.visibility = View.GONE
        loadingAnim.visibility = View.GONE
        loadingAnim.cancelAnimation() // Cancels the animation
    }

    private fun startLottieAnim() {
        val fragmentView = findViewById<View>(R.id.nav_host_fragment)
        fragmentView.visibility = View.INVISIBLE
        animFrame.visibility = View.VISIBLE
        loadingAnim.visibility = View.VISIBLE
        loadingAnim.speed = 0.5F // How fast does the animation play
        loadingAnim.progress = 0.5F // Starts the animation from 50% of the beginning
        loadingAnim.addAnimatorUpdateListener {
            // Called everytime the frame of the animation changes
        }
        loadingAnim.repeatMode = LottieDrawable.RESTART // Restarts the animation (you can choose to reverse it as well)
    }
* */

}
