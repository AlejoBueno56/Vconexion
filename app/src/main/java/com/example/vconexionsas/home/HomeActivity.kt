package com.example.vconexionsas.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.vconexionsas.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Obtener el NavHostFragment y el NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Obtener BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Configurar navegación automática con el BottomNavigationView
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        // Sincronizar visualmente el item seleccionado incluso al usar "back"
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
        }
    }
}


