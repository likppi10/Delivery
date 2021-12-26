package com.example.delivery.screen.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.delivery.R
import com.example.delivery.data.entity.locaion.LocationLatLngEntity
import com.example.delivery.data.entity.locaion.MapSearchInfoEntity
import com.example.delivery.databinding.FragmentHomeBinding
import com.example.delivery.screen.MainActivity
import com.example.delivery.screen.MainTabMenu
import com.example.delivery.screen.base.BaseFragment
import com.example.delivery.screen.home.HomeViewModel.Companion.MY_LOCATION_KEY
import com.example.delivery.screen.home.restaurant.RestaurantCategory
import com.example.delivery.screen.home.restaurant.RestaurantListFragment
import com.example.delivery.screen.home.restaurant.RestautantFilterOrder
import com.example.delivery.screen.mylocation.MyLocationActivity
import com.example.delivery.screen.order.OrderMenuListActivity
import com.example.delivery.widget.adapter.RestaurantListFragmentPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    companion object {

        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        const val TAG = "MainFragment"

        fun newInstance() = HomeFragment()
    }

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override val viewModel by viewModel<HomeViewModel>()

    private lateinit var locationManager: LocationManager

    private lateinit var myLocationListener: MyLocationListener

    private lateinit var viewPagerAdapter: RestaurantListFragmentPagerAdapter

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    // 이게 꽤 흥미로운데, changeLocationLauncher가 쓰이는 곳에서 intent한 액티비티에서 전달 받은 값을 가지고 loadReverseGeoInformation를 한번 더함
    private val changeLocationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getParcelableExtra<MapSearchInfoEntity>(MY_LOCATION_KEY)?.let { myLocationInfo ->
                viewModel.loadReverseGeoInformation(myLocationInfo.locationLatLng)
            }
        }
    }

    /* 1-1. 위치 정보 : 위치 정보 감지 및 주변 가게
     * 위치 접근 권한 요청 런처
    */
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // 받은 권한
            val responsePermissions = permissions.entries.filter {
                it.key == Manifest.permission.ACCESS_FINE_LOCATION
                    || it.key == Manifest.permission.ACCESS_COARSE_LOCATION
            }
            // 필요한 권한 개수 = 받은 권한 개수 인지 확인
            if (responsePermissions.filter { it.value == true }.size == locationPermissions.size) {
                setMyLocationListener()
            } else {
                with(binding.locationTitleTextView) {
                    text = getString(R.string.please_request_location_permission)
                    setOnClickListener {
                        /* 1-1. 위치 정보 : 위치 정보 감지 및 재설정
                        * 권한 허용 후 위치정보를 가져온다.
                        */
                        getMyLocation()
                    }
                }
                Toast.makeText(requireContext(), getString(R.string.can_not_assigned_permission), Toast.LENGTH_SHORT).show()
            }
        }

    override fun initViews() = with(binding) {

        /* 1-1. 위치 정보 : 위치 정보 감지 및 재설정
        *  주소가 표시되어있는 텍스트 클릭하면 위치 재설정 가능
        */
        locationTitleTextView.setOnClickListener {
            viewModel.getMapSearchInfo()?.let { mapInfo ->
                changeLocationLauncher.launch(
                    MyLocationActivity.newIntent(
                        requireContext(), mapInfo
                    )
                )
            }
        }
        orderChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipDefault -> {
                    chipInitialize.isGone = true
                    changeRestaurantFilterOrder(RestautantFilterOrder.DEFAULT)
                }
                R.id.chipInitialize -> {
                    chipDefault.isChecked = true
                }
                R.id.chipDeliveryTip -> {
                    chipInitialize.isVisible = true
                    changeRestaurantFilterOrder(RestautantFilterOrder.LOW_DELIVERY_TIP)
                }
                R.id.chipFastDelivery -> {
                    chipInitialize.isVisible = true
                    changeRestaurantFilterOrder(RestautantFilterOrder.FAST_DELIVERY)
                }
                R.id.chipTopRate -> {
                    chipInitialize.isVisible = true
                    changeRestaurantFilterOrder(RestautantFilterOrder.TOP_RATE)
                }
            }
        }
    }

    private fun alertLoginNeed(afterAction: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("로그인이 필요합니다.")
            .setMessage("주문하려면 로그인이 필요합니다. My탭으로 이동하시겠습니까?")
            .setPositiveButton("이동") { dialog, _ ->
                afterAction()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun changeRestaurantFilterOrder(order: RestautantFilterOrder) {
        viewPagerAdapter.fragmentList.forEach {
            it.viewModel.setRestaurantFilterOrder(order)
        }
    }

    private fun getMyLocation() {
        // location manager 설정
        if (::locationManager.isInitialized.not()) {
            locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        //GPS 켜져있는지 확인
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        /* 1-1. 위치 정보 : 위치 정보 감지 및 재설정
        * 위치 접근 권한 요청
        */
        if (isGpsEnable) {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationListener() {
        val minTime: Long = 1500
        val minDistance = 100f
        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }
        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }
    }

    override fun observeData() {
        //HomeState에서 위치정보의 현재 state에 따라서 분기를 줘서 처리
        viewModel.homeStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is HomeState.Uninitialized -> {
                    /* 1-1. 위치 정보 : 위치 정보 감지 및 재설정
                    * 아직 초기화 되지 않았을 때
                    */
                    getMyLocation()
                }
                is HomeState.Loading -> {
                    binding.locationLoading.isVisible = true
                    binding.locationTitleTextView.text = getString(R.string.loading)
                }
                is HomeState.Success -> {
                    binding.locationLoading.isGone = true
                    binding.locationTitleTextView.text = it.mapSearchInfoEntity.fullAddress
                    initViewPager(it.mapSearchInfoEntity.locationLatLng)
                    if (it.isLocationSame.not()) {
                        Toast.makeText(requireContext(), "위치가 맞는지 확인해주세요!", Toast.LENGTH_SHORT).show()
                    }
                }
                is HomeState.Error -> {
                    Toast.makeText(requireContext(), it.messageId, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
        // 로그인면 내 화면 탭으로 이동, 비어있으면 장바구니 표시 X
        viewModel.foodMenuBasketLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.basketButtonContainer.isVisible = true
                binding.basketCountTextView.text = getString(R.string.basket_count, it.size)
                binding.basketButton.setOnClickListener {
                    if (firebaseAuth.currentUser == null) {
                        alertLoginNeed {
                            (requireActivity() as MainActivity).goToTab(MainTabMenu.MY)
                        }
                    } else {
                        startActivity(
                            OrderMenuListActivity.newIntent(requireActivity())
                        )
                    }
                }
            } else {
                binding.basketButtonContainer.isGone = true
                binding.basketButton.setOnClickListener(null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkMyBasket()
    }

    private fun initViewPager(locationLatLng: LocationLatLngEntity) = with(binding) {
        //위치 정보가 잘 확보된 상태여야 탭레이아웃 보여주겠다.
        orderChipGroup.isVisible = true
        if (::viewPagerAdapter.isInitialized.not()) {
            val restaurantCategories = RestaurantCategory.values()
            val restaurantListFragmentList = restaurantCategories.map {
                RestaurantListFragment.newInstance(it, locationLatLng)
            }
            viewPagerAdapter = RestaurantListFragmentPagerAdapter(
                this@HomeFragment,
                restaurantListFragmentList,
                locationLatLng
            )
            viewPager.adapter = viewPagerAdapter
            // 뷰페이저 재사용
            viewPager.offscreenPageLimit = restaurantCategories.size
            //텝레이아웃에 탭들을 뿌려주도록함
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.setText(RestaurantCategory.values()[position].categoryNameId)
            }.attach()
        }
        //현재 위치정보랑 뷰페이저에 있는 위치정보가 맞지 않다면 재설정
        if (locationLatLng != viewPagerAdapter.locationLatLng) {
            viewPagerAdapter.locationLatLng = locationLatLng
            viewPagerAdapter.fragmentList.forEach {
                it.viewModel.setLocationLatLng(locationLatLng)
            }
        }
    }

    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener)
        }
    }

    /* 1-1. 위치 정보 : 위치 정보 감지 및 재설정
    * HomeViewModel로 위치정보 전송
    */
    inner class MyLocationListener : LocationListener {

        override fun onLocationChanged(location: Location) {
            viewModel.loadReverseGeoInformation(
                // 별거 아닌거도 전부 entity를 만들어 사용
                LocationLatLngEntity(
                    location.latitude,
                    location.longitude
                )
            )
            removeLocationListener()
        }

    }

}
