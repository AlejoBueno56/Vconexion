package com.example.vconexionsas.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vconexionsas.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.NavigationUI

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Obtén el NavController del NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Configura el BottomNavigationView con el NavController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        // Asocia el BottomNavigationView con el NavController
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navInicio -> {
                    navController.navigate(R.id.homeFragment) // Navega al fragmento de Inicio
                    true
                }
                R.id.navPerfil -> {
                    navController.navigate(R.id.profileFragment) // Navega al fragmento de Perfil
                    true
                }
                else -> false
            }
        }
    }
}

