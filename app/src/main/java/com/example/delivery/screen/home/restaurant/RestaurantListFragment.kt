package com.example.delivery.screen.home.restaurant

import android.os.Bundle
import com.example.delivery.data.entity.locaion.LocationLatLngEntity
import com.example.delivery.databinding.FragmentListBinding
import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.screen.base.BaseFragment
import com.example.delivery.screen.home.restaurant.detail.RestaurantDetailActivity
import com.example.delivery.widget.adapter.ModelRecyclerAdapter
import com.example.delivery.widget.adapter.listener.restaurant.RestaurantListListener
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RestaurantListFragment : BaseFragment<RestaurantListViewModel, FragmentListBinding>() {

    override fun getViewBinding(): FragmentListBinding = FragmentListBinding.inflate(layoutInflater)

    private val restaurantCategory by lazy { arguments?.getSerializable(RESTAURANT_CATEGORY_KEY) as RestaurantCategory }
    private val locationLatLngEntity by lazy<LocationLatLngEntity> { arguments?.getParcelable(LOCATION_KEY)!! }

    override val viewModel by viewModel<RestaurantListViewModel> { parametersOf(restaurantCategory, locationLatLngEntity) }

    private val adapter by lazy {
        ModelRecyclerAdapter<RestaurantModel, RestaurantListViewModel>(listOf(), viewModel, adapterListener = object : RestaurantListListener {
            /* 1-2. 가게 나열 : 클릭하면 "가게 상세" 이동 및 가게 필터링
            *  매장 리스트중에서 한 아이템이 클릭되었을 경우 "가게 상세"로 이동한다.
            */
            override fun onClickItem(model: RestaurantModel) {
                startActivity(
                    RestaurantDetailActivity.newIntent(requireContext(), model.toEntity())
                )
            }
        })
    }

    override fun initViews() = with(binding) {
        recyclerVIew.adapter = adapter
    }

    override fun observeData() {
        viewModel.restaurantListLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    companion object {
        const val RESTAURANT_KEY = "Restaurant"
        const val RESTAURANT_CATEGORY_KEY = "restaurantCategory"
        const val LOCATION_KEY = "location"

        fun newInstance(restaurantCategory: RestaurantCategory, locationLatLng: LocationLatLngEntity): RestaurantListFragment {
            val bundle = Bundle().apply {
                putSerializable(RESTAURANT_CATEGORY_KEY, restaurantCategory)
                putParcelable(LOCATION_KEY, locationLatLng)
            }

            return RestaurantListFragment().apply {
                arguments = bundle
            }
        }

    }
}
