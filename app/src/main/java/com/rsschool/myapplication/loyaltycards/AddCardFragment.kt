package com.rsschool.myapplication.loyaltycards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rsschool.myapplication.loyaltycards.databinding.AddCardFragmentBinding
import com.rsschool.myapplication.loyaltycards.databinding.SignInFragmentBinding

class AddCardFragment : Fragment() {

    private var _binding : AddCardFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddCardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.cameraPreviewFragment)
        }
        arguments?.let {
            val number: String? = arguments?.getString("CODE")
            val type = arguments?.getInt("TYPE")
            binding.cardNumber.setText(number)
        }

    }
}