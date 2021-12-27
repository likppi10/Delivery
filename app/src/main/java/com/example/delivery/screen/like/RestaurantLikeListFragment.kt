package com.example.delivery.screen.like

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.delivery.databinding.FragmentRestaurantLikeListBinding
import com.example.delivery.model.restaurant.RestaurantModel
import com.example.delivery.screen.base.BaseFragment
import com.example.delivery.screen.home.restaurant.detail.RestaurantDetailActivity
import com.example.delivery.widget.adapter.ModelRecyclerAdapter
import com.example.delivery.widget.adapter.listener.restaurant.RestaurantLikeListListener
import org.koin.android.viewmodel.ext.android.viewModel

class RestaurantLikeListFragment: BaseFragment<RestaurantLikeListViewModel, FragmentRestaurantLikeListBinding>() {

    override fun getViewBinding(): FragmentRestaurantLikeListBinding = FragmentRestaurantLikeListBinding.inflate(layoutInflater)

    override val viewModel by viewModel<RestaurantLikeListViewModel>()

    private var isFirstShown = false

    /* 2. 찜 탭
       찜 가게 나열 : 클릭하면 해당 "가게 상세"로 이동
   *  가게 리스트 아이템을 선택하면, 해당 "가게 상세"로 이동한다.
   *  하트를 클릭하면 좋아요가 사라지고 찜이 취소된다.
   */
    private val adapter by lazy {
        ModelRecyclerAdapter<RestaurantModel, RestaurantLikeListViewModel>(listOf(), viewModel, adapterListener = object : RestaurantLikeListListener {

            override fun onDislikeItem(model: RestaurantModel) {
                viewModel.dislikeRestaurant(model.toEntity())
            }

            override fun onClickItem(model: RestaurantModel) {
                startActivity(
                    RestaurantDetailActivity.newIntent(requireContext(), model.toEntity())
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (isFirstShown.not()) {
            isFirstShown = true
        } else {
            viewModel.fetchData()
        }
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter
    }

    override fun observeData() {
        viewModel.restaurantListLiveData.observe(viewLifecycleOwner) {
            checkListEmpty(it)
        }
    }

    private fun checkListEmpty(restaurantList: List<RestaurantModel>) {
        val isEmpty = restaurantList.isEmpty()
        binding.recyclerView.isGone = isEmpty
        binding.emptyResultTextView.isVisible = isEmpty
        if (isEmpty.not()) {
            adapter.submitList(restaurantList)
        }
    }

    companion object {

        fun newInstance() = RestaurantLikeListFragment()

        const val TAG = "likeFragment"

    }

}
