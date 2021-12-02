package com.rsschool.myapplication.loyaltycards.ui

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rsschool.myapplication.loyaltycards.databinding.CameraPreviewFragmentBinding
import com.rsschool.myapplication.loyaltycards.domain.utils.BarcodeAnalyzer
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CameraActionsRequest
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CameraResultEvent
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.camera_preview_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.concurrent.Executors
import java.io.File
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var _binding: CameraPreviewFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val imageCaptureUseCase = ImageCapture.Builder()
        .build()

    private var cameraExecutor = Executors.newSingleThreadExecutor()

    private val cameraViewModel : CameraViewModel by viewModels()

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CameraPreviewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Request camera permissions

        lifecycleScope.launchWhenCreated {
            cameraViewModel.event.collectLatest {
                when (it) {
                    is CameraActionsRequest.ScanBarcodeAction -> {
                        if (cameraPermissionGranted()) {
                            binding.cameraCaptureButton.visibility = GONE
                            scanBarcode()
                        } else {
                            launcher.launch(CAMERA_PERMISSIONS)

                        }
                    }
                    is CameraActionsRequest.CaptureImageAction -> {
                        if (allPermissionsGranted()) {
                            startCamera()
                            binding.cameraCaptureButton.visibility = VISIBLE
                            binding.cameraCaptureButton.setOnClickListener {
                                //cameraViewModel.onTakePhotoClick()
                             takepic()}
                        } else {
                            launcher.launch(WRITE_EXTERNAL_STORAGE)
                            launcher.launch(CAMERA)
                        }
                    }
                }
            }
        }
    }

    private fun previewUseCase(): Preview {
        // Preview
        return Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }
    }

    private fun imageAnalysisUseCase() = ImageAnalysis.Builder()
        .build()
        .also {
            it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { code ->
                val action = CameraFragmentDirections.actionCameraFragmentToAddCardFragment(code)
                lifecycleScope.launchWhenResumed {
                    findNavController().navigate(action)
                }
            })
        }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, previewUseCase(), imageCaptureUseCase)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun scanBarcode() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Select back camera
            try {
                // Unbind any bound use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to lifecycleOwner
                cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase(), imageAnalysisUseCase())
            } catch (e: Exception) {
                Log.e("PreviewUseCase", "Binding failed! :(", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    fun takepic() {
        val imageCapture = imageCaptureUseCase

        val photoFile = File(requireContext().filesDir, UUID.randomUUID().toString() + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d("CameraFragment", msg)
                    lifecycleScope.launchWhenCreated {
                        cameraViewModel.event.collectLatest {
                            when (it) {
                                is CameraActionsRequest.CaptureImageAction -> {
                                    val action = CameraFragmentDirections
                                        .actionCameraFragmentToAddCardFragment(
                                            CameraResultEvent.ImageSaved(it.type, savedUri)
                                        )
                                    findNavController().navigate(action)

                                }
                            }
                        }
                    }
                }
            })
    }

    private fun cameraPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(), CAMERA_PERMISSIONS
    ) == PackageManager.PERMISSION_GRANTED

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private val CAMERA_PERMISSIONS = CAMERA
        private val REQUIRED_PERMISSIONS = arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA)

    }
}

typealias BarcodeListener = (resultEvent: CameraResultEvent) -> Unit
