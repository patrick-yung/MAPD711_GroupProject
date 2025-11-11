package com.mapd711_groupproject

import android.content.Intent
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity() {
    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    protected fun setupDrawer(currentMenuId: Int) {
        // 1) find views from the layout
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 2) connect the hamburger icon with the drawer
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 3) mark the current page
        navView.setCheckedItem(currentMenuId)

        // 4) handle clicks in the drawer
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this !is HomeActivity)
                        startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.nav_map -> {
                    if (this !is MapsActivity)
                        startActivity(Intent(this, MapsActivity::class.java))
                }
                R.id.nav_others -> {
                    // Do nothing
                }
            }
            drawerLayout.closeDrawers()
            true
        }

    }
}
