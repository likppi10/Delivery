package com.example.delivery.screen.mylocation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.delivery.R
import com.example.delivery.data.entity.locaion.LocationLatLngEntity
import com.example.delivery.data.entity.locaion.MapSearchInfoEntity
import com.example.delivery.databinding.ActivityMyLocationBinding
import com.example.delivery.screen.base.BaseActivity
import com.example.delivery.screen.home.HomeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MyLocationActivity : BaseActivity<MyLocationViewModel, ActivityMyLocationBinding>(), OnMapReadyCallback {

    override val viewModel by viewModel<MyLocationViewModel> {
        parametersOf(
            intent.getParcelableExtra<MapSearchInfoEntity>(HomeViewModel.MY_LOCATION_KEY)
        )
    }

    override fun getViewBinding() = ActivityMyLocationBinding.inflate(layoutInflater)

    companion object {
        const val CAMERA_ZOOM_LEVEL = 17f

        fun newIntent(context: Context, mapSearchInfoEntity: MapSearchInfoEntity) =
            Intent(context, MyLocationActivity::class.java).apply {
                putExtra(HomeViewModel.MY_LOCATION_KEY, mapSearchInfoEntity)
            }

    }

    private lateinit var map: GoogleMap

    private var isMapInitialized: Boolean = false
    private var isChangingLocation: Boolean = false

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        confirmButton.setOnClickListener {
            viewModel.confirmSelectLocation()
        }
        setupGoogleMap()
    }

    private fun setupGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map ?: return
        viewModel.fetchData()
    }

    override fun observeData() {
        viewModel.myLocationStateLiveData.observe(this) {
            when (it) {
                is MyLocationState.Loading -> {
                    handleLoadingState()
                }
                is MyLocationState.Success -> {
                    //맵이 초기화가 되어야지만
                    if (::map.isInitialized) {
                        handleSuccessState(it)
                    }
                }
                is MyLocationState.Confirm -> {
                    /* 1-1. 위치 정보 : 위치 정보 감지 및 주변 가게
                    * 마커가 가르키는 곳을 현재 위치로 변경
                    */
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra(HomeViewModel.MY_LOCATION_KEY, it.mapSearchInfoEntity)
                    })
                    finish()
                }
                else -> Unit
            }
        }
    }

    private fun handleLoadingState() = with(binding) {
        locationLoading.isVisible = true
        locationTitleTextView.text = getString(R.string.loading)
    }

    /* 1-1. 위치 정보 : 위치 정보 감지 및 주변 가게
     * 맵이 초기화가 됬으면,
     * 현재위치를 비추는 멥을 구현하고,
     * 옮긴 위치가 있다면,
    */
    private fun handleSuccessState(state: MyLocationState.Success) = with(binding) {
        val mapSearchInfo = state.mapSearchInfoEntity
        locationLoading.isGone = true
        locationTitleTextView.text = mapSearchInfo.fullAddress
        //맵이 적절하게 초기화됬는지 판단
        if (isMapInitialized.not()) {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        mapSearchInfo.locationLatLng.latitude,
                        mapSearchInfo.locationLatLng.longitude
                    ), CAMERA_ZOOM_LEVEL
                )
            )

            map.setOnCameraIdleListener {
                if (isChangingLocation.not()) {
                    isChangingLocation = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        val cameraLatLng = map.cameraPosition.target
                        viewModel.changeLocationInfo(
                            LocationLatLngEntity(
                                cameraLatLng.latitude,
                                cameraLatLng.longitude
                            )
                        )
                        isChangingLocation = false
                    }, 1000)
                }
            }
            isMapInitialized = true
        }
    }


}
