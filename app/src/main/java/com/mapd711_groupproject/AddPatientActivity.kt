package com.mapd711_groupproject

import android.os.Bundle

class AddPatientActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)
        setupDrawer(R.id.nav_home)
    }
}
