package com.distory.app.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.distory.app.R
import com.distory.app.databinding.ActivityStoryMapsBinding
import com.distory.app.domain.story.entity.StoryEntity
import com.distory.app.ui.BaseActivity
import com.distory.app.utils.AppUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class StoryMapsActivity : BaseActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private lateinit var binding: ActivityStoryMapsBinding
    private val viewModel: StoryMapsViewModel by viewModels()

    private lateinit var loadingSnackBar: Snackbar
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        observe()
    }

    private fun setupView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.frMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        loadingSnackBar =
            Snackbar.make(binding.clStoryMaps, R.string.loading, Snackbar.LENGTH_INDEFINITE)
    }

    override fun onResume() {
        super.onResume()
        fetchStories()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()
        getMyLocation()
    }

    private fun setupAction() {

    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap?.isMyLocationEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        mMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(it.latitude, it.longitude),
                                10f
                            )
                        )
                    }
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            mMap?.let {
                it.uiSettings.isZoomControlsEnabled = true
                it.uiSettings.isZoomGesturesEnabled = true
                it.uiSettings.isCompassEnabled = true

                val success =
                    it.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
                if (!success) {
                    Log.e("StoryMaps", "Style parsing failed.")
                }
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("StoryMaps", "Can't find style. Error: ", exception)
        }
    }

    private fun observe() {
        observeState()
        observeStories()
    }

    private fun observeState() {
        viewModel.state.observe(this@StoryMapsActivity) {
            handleState(it)
        }
    }

    private fun observeStories() {
        viewModel.stories.observe(this@StoryMapsActivity) {
            handleStories(it)
        }
    }

    private fun fetchStories() {
        viewModel.fetchStoriesWithLocation()
    }

    private fun handleState(state: StoryMapsActivityState) {
        when (state) {
            is StoryMapsActivityState.ShowToast -> Toast.makeText(
                applicationContext,
                state.message,
                Toast.LENGTH_LONG
            ).show()
            is StoryMapsActivityState.IsLoading -> handleLoading(state.isLoading)
            is StoryMapsActivityState.Init -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading)
            loadingSnackBar.show()
        else
            loadingSnackBar.dismiss()
    }

    private fun handleStories(stories: List<StoryEntity>) {
        if (stories.isEmpty())
            Snackbar.make(binding.clStoryMaps, R.string.loading, Snackbar.LENGTH_SHORT).show()
        else {
            for (story in stories) {
                mMap?.apply {
                    with(LatLng(story.latitude, story.longitude)) {
                        addMarker(
                            MarkerOptions()
                                .position(this)
                                .title(story.name)
                                .snippet(story.description)
                                .icon(
                                    AppUtil.vectorToBitmap(
                                        applicationContext,
                                        R.drawable.ic_round_beenhere_24,
                                        ContextCompat.getColor(
                                            applicationContext,
                                            R.color.red
                                        )
                                    )
                                )
                        )
                    }
                }
            }
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