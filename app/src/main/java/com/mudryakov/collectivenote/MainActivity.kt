package com.mudryakov.collectivenote

import android.content.Intent
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
import com.mudryakov.collectivenote.database.firebase.handleSignInresult
import com.mudryakov.collectivenote.databinding.ActivityMainBinding
import com.mudryakov.collectivenote.utilits.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var _Binding: ActivityMainBinding? = null
    val mBinding get() = _Binding!!
    lateinit var navConroller: NavController
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
        navConroller = Navigation.findNavController(APP_ACTIVITY, R.id.container)
        actionBar = supportActionBar
        mDrawer = mBinding.myDrawer
        mNavView = mBinding.myNavView
        mNavView.itemIconTintList = null
    }

    override fun onStart() {
        super.onStart()

        appPreference.getPreference(APP_ACTIVITY)
        setDrawerEdge()
        mNavView.setNavigationItemSelectedListener(this)

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
            handleSignInresult(task)
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
            R.id.drawer_settings -> fastNavigate(R.id.action_mainFragment_to_settingsFragment)
            R.id.drawer_help -> fastNavigate(R.id.action_mainFragment_to_helpFragment)
        }


        return true
    }

    override fun onBackPressed() {
        if (back)
            super.onBackPressed()
        else {
            if (doubleClick) super.onBackPressed()
            else {
                doubleClick = true
                showToast("Нажмите ещё раз для выхода")
                CoroutineScope(IO).launch {
                    delay(1500)
                    doubleClick = false
                }
            }
        }
    }

}