package com.example.vconexionsas.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vconexionsas.R
import com.example.vconexionsas.databinding.PlanItemBinding
import com.example.vconexionsas.model.PlanData

class PlanAdapter(private val plans: List<PlanData>) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {

    private var onContactClickListener: ((PlanData) -> Unit)? = null

    fun setOnContactClickListener(listener: (PlanData) -> Unit) {
        onContactClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = PlanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = plans[position]
        holder.bind(plan)
    }

    override fun getItemCount(): Int = plans.size

    inner class PlanViewHolder(private val binding: PlanItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: PlanData) {
            binding.planTitle.text = plan.title
            binding.planSpeed.text = plan.speed
            binding.planPrice.text = plan.price

            // Limpiar características anteriores antes de agregar nuevas
            binding.planFeaturesContainer.removeAllViews()

            // Agregar dinámicamente cada característica
            for (feature in plan.features) {
                val textView = TextView(binding.root.context).apply {
                    text = "✔ $feature"
                    setTextColor(ContextCompat.getColor(context, R.color.green)) // Usa tu color o reemplaza con un color directo
                    textSize = 14f
                    setPadding(0, 4, 0, 4)
                }
                binding.planFeaturesContainer.addView(textView)
            }

            // Botón contacto
            binding.contactButton.setOnClickListener {
                onContactClickListener?.invoke(plan)
            }
        }
    }
}

