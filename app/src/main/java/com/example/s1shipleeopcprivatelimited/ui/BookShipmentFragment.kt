package com.example.s1shipleeopcprivatelimited.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.s1shipleeopcprivatelimited.R
import com.example.s1shipleeopcprivatelimited.databinding.FragmentBookShipmentBinding
import com.example.s1shipleeopcprivatelimited.model.Address
import com.example.s1shipleeopcprivatelimited.utils.ValidationUtils
import com.example.s1shipleeopcprivatelimited.viewmodel.ShipmentViewModel

class BookShipmentFragment : Fragment() {
    private var _binding: FragmentBookShipmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShipmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookShipmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.couriers.observe(viewLifecycleOwner) { couriers ->
            val courierNames = couriers.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                courierNames
            )
            binding.spinnerCourier.adapter = adapter
        }

        viewModel.shippingRates.observe(viewLifecycleOwner) { rates ->
            if (rates.isNotEmpty()) {
                binding.ratesLayout.visibility = View.VISIBLE
                binding.recyclerViewRates.adapter = ShippingRateAdapter(rates) { rate ->
                    // Find and select the courier that corresponds to this rate
                    viewModel.couriers.value?.find { it.id == rate.courierId }?.let { courier ->
                        viewModel.selectCourier(courier)
                        // Update UI to show selected rate
                        binding.tvTotalPrice.text = "₹${rate.totalAmount}"
                        binding.btnProceedToPayment.isEnabled = true
                    }
                }
            } else {
                binding.ratesLayout.visibility = View.GONE
                Toast.makeText(context, "No shipping rates available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.spinnerCourier.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.couriers.value?.get(position)?.let { courier ->
                    viewModel.selectCourier(courier)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        binding.btnCalculateRates.setOnClickListener {
            if (validateAddressFields()) {
                saveAddressesToViewModel()
                viewModel.calculateRates(
                    binding.etPickupPincode.text.toString(),
                    binding.etDeliveryPincode.text.toString()
                )
            }
        }

        binding.btnProceedToPayment.setOnClickListener {
            if (validateAllFields()) {
                findNavController().navigate(R.id.action_bookShipmentFragment_to_paymentFragment)
            }
        }
    }

    private fun validateAddressFields(): Boolean {
        // Basic validation for required fields
        var isValid = true

        with(binding) {
            if (etPickupName.text.isNullOrBlank()) {
                etPickupName.error = "Required"
                isValid = false
            }

            if (etPickupPhone.text.isNullOrBlank()) {
                etPickupPhone.error = "Required"
                isValid = false
            } else if (!ValidationUtils.isValidPhoneNumber(etPickupPhone.text.toString())) {
                etPickupPhone.error = "Invalid phone number"
                isValid = false
            }

            if (etPickupAddress1.text.isNullOrBlank()) {
                etPickupAddress1.error = "Required"
                isValid = false
            }

            if (etPickupCity.text.isNullOrBlank()) {
                etPickupCity.error = "Required"
                isValid = false
            }

            if (etPickupPincode.text.isNullOrBlank()) {
                etPickupPincode.error = "Required"
                isValid = false
            } else if (!ValidationUtils.isValidPincode(etPickupPincode.text.toString())) {
                etPickupPincode.error = "Invalid pincode"
                isValid = false
            }

            // Same validation for delivery address fields
            if (etDeliveryName.text.isNullOrBlank()) {
                etDeliveryName.error = "Required"
                isValid = false
            }

            // ... (similar validation for other delivery fields)
        }

        return isValid
    }

    private fun validateAllFields(): Boolean {
        if (!validateAddressFields()) return false

        // Additional validation
        if (viewModel.selectedCourier.value == null) {
            Toast.makeText(context, "Please select a courier", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etWeight.text.isNullOrBlank()) {
            binding.etWeight.error = "Required"
            return false
        }

        try {
            val weight = binding.etWeight.text.toString().toDouble()
            if (weight <= 0 || weight > 50) {
                binding.etWeight.error = "Weight must be between 0.1 and 50 kg"
                return false
            }
            viewModel.packageWeight.value = weight
        } catch (e: NumberFormatException) {
            binding.etWeight.error = "Invalid weight"
            return false
        }

        return true
    }

    private fun saveAddressesToViewModel() {
        val pickupAddress = Address(
            fullName = binding.etPickupName.text.toString(),
            phoneNumber = binding.etPickupPhone.text.toString(),
            addressLine1 = binding.etPickupAddress1.text.toString(),
            addressLine2 = binding.etPickupAddress2.text.toString(),
            city = binding.etPickupCity.text.toString(),
            state = binding.etPickupState.text.toString(),
            pincode = binding.etPickupPincode.text.toString(),
            country = "India" // Default for now
        )

        val deliveryAddress = Address(
            fullName = binding.etDeliveryName.text.toString(),
            phoneNumber = binding.etDeliveryPhone.text.toString(),
            addressLine1 = binding.etDeliveryAddress1.text.toString(),
            addressLine2 = binding.etDeliveryAddress2.text.toString(),
            city = binding.etDeliveryCity.text.toString(),
            state = binding.etDeliveryState.text.toString(),
            pincode = binding.etDeliveryPincode.text.toString(),
            country = "India" // Default for now
        )

        viewModel.pickupAddress.value = pickupAddress
        viewModel.deliveryAddress.value = deliveryAddress
        viewModel.isFragile.value = binding.checkboxFragile.isChecked
        viewModel.notes.value = binding.etNotes.text.toString()

        // Parse dimensions (LxWxH)
        val dimensions = "${binding.etLength.text ?: "30"}x${binding.etWidth.text ?: "30"}x${binding.etHeight.text ?: "30"}"
        viewModel.packageDimensions.value = dimensions
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
