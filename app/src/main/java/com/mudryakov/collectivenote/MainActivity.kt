package com.mudryakov.collectivenote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
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
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY
import com.mudryakov.collectivenote.utilits.SIGN_CODE_REQUEST
import com.mudryakov.collectivenote.utilits.appPreference
import com.mudryakov.collectivenote.utilits.showToast
import java.lang.Exception
import java.lang.reflect.Field


class MainActivity : AppCompatActivity() {
    var _Binding: ActivityMainBinding? = null
    val mBinding get() = _Binding!!
    lateinit var navConroller: NavController
    var actionBar: ActionBar? = null
    lateinit var mDrawer:DrawerLayout
    lateinit var navView: NavigationView

    var back = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _Binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.mainToolbar)
        APP_ACTIVITY = this
        navConroller = Navigation.findNavController(APP_ACTIVITY, R.id.container)
        actionBar = supportActionBar
        mDrawer = mBinding.myDrawer
        navView = mBinding.myNavView

    }

    override fun onStart() {
        super.onStart()
        appPreference.getPreference(APP_ACTIVITY)
        setDrawerEdge()
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
                .apply { setInt(viewDragHelper,width) }
        } catch (e: Exception) {}

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_CODE_REQUEST) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInresult(task)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
      if (back) onBackPressed()
        else mDrawer.openDrawer(navView)
        return true
    }
}