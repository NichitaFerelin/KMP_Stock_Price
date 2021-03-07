package com.ferelin.stockprice.ui.chart

import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.ferelin.stockprice.R
import com.ferelin.stockprice.custom.chart.ChartView
import com.ferelin.stockprice.custom.utils.Marker

class ChartFragment : Fragment(R.layout.fragment_chart) {

    val markers = mutableListOf<Marker>().apply {
        this.add(Marker(value = 200F))
        this.add(Marker(value = 201F))
        this.add(Marker(value = 199F))
        this.add(Marker(value = 205F))
        this.add(Marker(value = 215F))
        this.add(Marker(value = 187F))
        this.add(Marker(value = 200F))
        this.add(Marker(value = 201F))
        this.add(Marker(value = 199F))
        this.add(Marker(value = 207F))
        this.add(Marker(value = 200F))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val a = view.findViewById<CardView>(R.id.item_suggestion)
        view.findViewById<ChartView>(R.id.charView).apply {
            setMarkers(markers)
            setOnTouchListener {
                a.x = it.position.x
                a.y = it.position.y
            }
        }



    }

    companion object {

        const val TEXT_KEY = "text"

        fun newInstance(arguments: Bundle): ChartFragment {
            return ChartFragment().apply { setArguments(arguments) }
        }
    }
}