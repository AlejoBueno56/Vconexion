package com.example.vconexionsas.home.conocenos

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.vconexionsas.R
import kotlin.math.abs

class ConocenosFragment : Fragment() {

    private var currentItemIndex = 0
    private val scrollDelay = 5000L // Tiempo entre scrolls automáticos (5 segundos)
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var scrollRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_conocenos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón de regreso
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Botón físico atrás
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        val scrollView = view.findViewById<HorizontalScrollView>(R.id.conocenosCarousel)
        val linearLayout = view.findViewById<LinearLayout>(R.id.conocenosLinearLayout)

        // Auto-scroll
        scrollRunnable = object : Runnable {
            override fun run() {
                val childCount = linearLayout.childCount
                if (childCount == 0) return

                currentItemIndex = (currentItemIndex + 1) % childCount
                val targetView = linearLayout.getChildAt(currentItemIndex)
                scrollView.smoothScrollTo(
                    targetView.left - (scrollView.width - targetView.width) / 2,
                    0
                )

                handler.postDelayed(this, scrollDelay)
            }
        }

        handler.postDelayed(scrollRunnable, scrollDelay)

        // Centrado automático al soltar el dedo
        scrollView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.removeCallbacks(scrollRunnable)
                }

                MotionEvent.ACTION_UP -> {
                    handler.postDelayed(scrollRunnable, scrollDelay)
                    snapToNearestCard(scrollView, linearLayout)
                }
            }
            false
        }
    }

    private fun snapToNearestCard(scrollView: HorizontalScrollView, container: LinearLayout) {
        var closestView: View? = null
        var minDistance = Int.MAX_VALUE

        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            val childCenter = child.left + child.width / 2
            val scrollCenter = scrollView.scrollX + scrollView.width / 2
            val distance = abs(childCenter - scrollCenter)

            if (distance < minDistance) {
                minDistance = distance
                closestView = child
            }
        }

        closestView?.let {
            scrollView.post {
                scrollView.smoothScrollTo(
                    it.left - (scrollView.width - it.width) / 2,
                    0
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(scrollRunnable)
    }
}

