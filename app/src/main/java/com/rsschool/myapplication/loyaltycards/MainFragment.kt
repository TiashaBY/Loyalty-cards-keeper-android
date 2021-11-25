package com.rsschool.myapplication.loyaltycards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rsschool.myapplication.loyaltycards.databinding.MainFragmentBinding
import com.rsschool.myapplication.loyaltycards.viewmodel.AuthentificationState
import com.rsschool.myapplication.loyaltycards.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainFragment: Fragment() {

    private var _binding : MainFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val userAuthViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            userAuthViewModel.authState.collect { state ->
                Log.d("dfghfhgfhgfhhg", "stater!!!=" + state)
                when (state) {
                    AuthentificationState.AUTH -> {
                        Log.d("dfghfhgfhgfhhg", "navigate to dashboard")
                        findNavController().navigate(R.id.cardsDashboardFragment)
                    }
                    else -> {
                        Log.d("dfghfhgfhgfhhg", "navigate to sign in")
                        findNavController().navigate(R.id.signInFragment)
                    }
                }
            }
        }
    }
}
