package com.mudryakov.collectivenote

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.navigation.NavigationView
import com.mudryakov.collectivenote.database.firebase.handleSignInResult
import com.mudryakov.collectivenote.databinding.ActivityMainBinding
import com.mudryakov.collectivenote.utility.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var _Binding: ActivityMainBinding? = null
    val mBinding get() = _Binding!!
    lateinit var navController: NavController
    var actionBar: ActionBar? = null
    lateinit var mDrawer: DrawerLayout
    lateinit var mNavView: NavigationView
    var doubleClick = false
    var back = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            _Binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.mainToolbar)
        APP_ACTIVITY = this
        INTERNET = false
        navController = Navigation.findNavController(APP_ACTIVITY, R.id.container)
        actionBar = supportActionBar
        mDrawer = mBinding.myDrawer
        mNavView = mBinding.myNavView
        mNavView.itemIconTintList = null
          }


    override fun onStart() {
        super.onStart()
        hideKeyboard(APP_ACTIVITY)
        AppPreference.getPreference(APP_ACTIVITY)
        setDrawerEdge()
        mNavView.setNavigationItemSelectedListener(this)
        APP_ACTIVITY.mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
       }


    private fun setDrawerEdge() {
        val width = APP_ACTIVITY.resources.getDimension(R.dimen.drawer_edge).toInt()
        try {
            val viewDragHelper = mDrawer::class.java
                .getDeclaredField("mLeftDragger")
                .apply { isAccessible = true }
                .run { get(mDrawer) as ViewDragHelper }
            viewDragHelper.let { it::class.java.getDeclaredField("mEdgeSize") }
                .apply { isAccessible = true }
                .apply { setInt(viewDragHelper, width) }
        } catch (e: Exception) {
        }

    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_CODE_REQUEST) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        if (back) onBackPressed()
        else mDrawer.openDrawer(mNavView)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mDrawer.closeDrawer(mNavView)
        when (item.itemId) {
            R.id.drawer_history -> fastNavigate(R.id.action_mainFragment_to_historyFragment)
            R.id.drawer_info -> fastNavigate(R.id.action_mainFragment_to_roomInfoFragment)
            R.id.drawer_settings -> {
                if (INTERNET) fastNavigate(R.id.action_mainFragment_to_settingsFragment)
                else showNoInternetToast()
            }
            R.id.drawer_help -> fastNavigate(R.id.action_mainFragment_to_helpFragment)
        }
        return true
    }

    override fun onBackPressed() {
        mDrawer.closeDrawer(mNavView)
        hideKeyboard(APP_ACTIVITY)


        if (back)
            super.onBackPressed()
        else {
            if (doubleClick) super.onBackPressed()
            else {
                doubleClick = true
                showToast(R.string.ask_for_double_click_toast)
                CoroutineScope(IO).launch {
                    delay(1500)
                    doubleClick = false
                }
            }
        }

    }

    override fun onStop() {
       try {
            fastNavigate(R.id.action_settingsFragment_to_mainFragment)
        } catch (e: Exception) {
        }
        super.onStop()
    }


}
