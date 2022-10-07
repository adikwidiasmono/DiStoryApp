package com.distory.app.ui.story.add

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.distory.app.R
import com.distory.app.databinding.ActivityAddStoryBinding
import com.distory.app.domain.story.entity.StatusAndMessage
import com.distory.app.ui.BaseActivity
import com.distory.app.utils.AppUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AddStoryActivity : BaseActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels()

    private var fileUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Double? = null
    private var currentLon: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        observe()
    }

    private fun setupView() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.tietCurrentLoc.setText(getString(R.string.unknown))
        getMyLocation()
    }

    private fun setupAction() {
        binding.ivPickImg.setOnClickListener {
            ImagePicker.with(this)
                // Final image size will be less than 1 MB(Optional)
                .compress(1024)
                // Final image resolution will be less than 1080 x 1080(Optional)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForStoryImageResult.launch(intent)
                }
        }
        binding.mbAdd.setOnClickListener {
            val desc = binding.tietDescription.text.toString()

            if (fileUri == null) {
                Toast.makeText(
                    applicationContext, getString(R.string.err_mandatory_image), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            binding.tilDescription.apply {
                isErrorEnabled = false
                if (desc.isEmpty()) {
                    isErrorEnabled = true
                    error = getString(R.string.err_mandatory_field)
                    return@setOnClickListener
                }
            }

            addNewStory(fileUri!!, desc, currentLat ?: 0.0, currentLon ?: 0.0)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun playAnimation() {

    }

    private fun observe() {
        observeState()
        observeResult()
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { handleState(it) }
            .launchIn(lifecycleScope)
    }

    private fun observeResult() {
        viewModel.result.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { handleResult(it) }
            .launchIn(lifecycleScope)
    }

    private fun addNewStory(uri: Uri, desc: String, lat: Double, lon: Double) {
        viewModel.addNewStory(uri, desc, lat, lon)
    }

    private fun handleState(state: AddStoryActivityState) {
        when (state) {
            is AddStoryActivityState.ShowToast -> Toast.makeText(
                applicationContext,
                state.message,
                Toast.LENGTH_LONG
            ).show()
            is AddStoryActivityState.IsLoading -> handleLoading(state.isLoading)
            is AddStoryActivityState.Init -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mbAdd.visibility = View.INVISIBLE
            binding.pbAdd.visibility = View.VISIBLE
        } else {
            binding.mbAdd.visibility = View.VISIBLE
            binding.pbAdd.visibility = View.GONE
        }
    }

    private fun handleResult(result: StatusAndMessage) {
        // Success add new story
        if (!result.isError)
            finish()
    }

    private val startForStoryImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    fileUri = data?.data!!
                    Glide
                        .with(applicationContext)
                        .load(fileUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_outline_image_24)
                        .into(binding.ivPickImg)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, getString(R.string.task_cancelled), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        currentLat = it.latitude
                        currentLon = it.longitude

                        AppUtil.getAddressName(applicationContext, it.latitude, it.longitude)
                            ?.let { addr ->
                                binding.tietCurrentLoc.setText(addr)
                            }
                    }
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
}