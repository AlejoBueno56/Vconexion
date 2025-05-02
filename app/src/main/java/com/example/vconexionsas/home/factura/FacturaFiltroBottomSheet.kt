package com.example.vconexionsas.home.factura

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.vconexionsas.databinding.DialogFiltroFacturaBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FacturaFiltroBottomSheet(
    private val onBuscar: (mes: String, anio: String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogFiltroFacturaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFiltroFacturaBinding.inflate(inflater, container, false)

        val meses = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val anios = listOf("2024", "2025") // puedes automatizar si deseas

        binding.spinnerMes.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, meses)
        binding.spinnerAnio.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, anios)

        binding.btnBuscarFactura.setOnClickListener {
            val mes = binding.spinnerMes.selectedItem.toString()
            val anio = binding.spinnerAnio.selectedItem.toString()
            onBuscar(mes, anio)
            dismiss()
        }

        return binding.root
    }
}
