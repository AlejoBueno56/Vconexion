package com.example.vconexionsas.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vconexionsas.home.inicio.InicioFragment
import com.example.vconexionsas.home.perfil.PerfilFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InicioFragment()
            1 -> PerfilFragment()
            else -> InicioFragment()
        }
    }
}
