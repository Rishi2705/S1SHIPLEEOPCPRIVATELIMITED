package com.example.s1shipleeopcprivatelimited.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.s1shipleeopcprivatelimited.databinding.ItemShippingRateBinding
import com.example.s1shipleeopcprivatelimited.model.ShippingRate

class ShippingRateAdapter(
    private val rates: List<ShippingRate>,
    private val onRateSelected: (ShippingRate) -> Unit
) : RecyclerView.Adapter<ShippingRateAdapter.RateViewHolder>() {

    inner class RateViewHolder(private val binding: ItemShippingRateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rate: ShippingRate) {
            binding.tvCourierName.text = rate.courierId // Ideally replaced with actual name
            binding.tvPrice.text = "₹${rate.totalAmount}"
            binding.tvDeliveryTime.text = rate.estimatedDeliveryTime

            binding.root.setOnClickListener {
                onRateSelected(rate)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        val binding = ItemShippingRateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bind(rates[position])
    }

    override fun getItemCount() = rates.size
}