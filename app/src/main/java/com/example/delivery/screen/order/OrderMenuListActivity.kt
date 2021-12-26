package com.example.delivery.screen.order

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.delivery.databinding.ActivityOrderMenuListBinding
import com.example.delivery.model.restaurant.FoodModel
import com.example.delivery.screen.base.BaseActivity
import com.example.delivery.widget.adapter.ModelRecyclerAdapter
import com.example.delivery.widget.adapter.listener.order.OrderMenuListListener
import org.koin.android.viewmodel.ext.android.viewModel

class OrderMenuListActivity : BaseActivity<OrderMenuListViewModel, ActivityOrderMenuListBinding>() {

    override val viewModel by viewModel<OrderMenuListViewModel>()

    override fun getViewBinding(): ActivityOrderMenuListBinding = ActivityOrderMenuListBinding.inflate(layoutInflater)

    companion object {
        fun newIntent(context: Context) = Intent(context, OrderMenuListActivity::class.java)
    }

    /* 1-2-1. 장바구니 :
    메뉴 나열 : 담는 정보는 "가게 상세"의 메뉴 나열과 같음
	주문하기 : 장바구니 클리어, 주문완료
    *  장바구니의 아이템이 클릭됐을 경우 장바구니에서 삭제된다.
    */
    private val adapter by lazy {
        ModelRecyclerAdapter<FoodModel, OrderMenuListViewModel>(listOf(), viewModel, adapterListener = object : OrderMenuListListener {
            override fun onRemoveItem(model: FoodModel) {
                viewModel.removeOrderMenu(model)
            }
        })
    }

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        recyclerVIew.adapter = adapter
        binding.confirmButton.setOnClickListener {
            viewModel.orderMenu()
        }
        binding.orderClearButton.setOnClickListener {
            viewModel.clearOrderMenu()
        }
    }

    override fun observeData() = viewModel.orderMenuState.observe(this) {
        when(it) {
            is OrderMenuState.Loading -> {
                binding.progressBar.isVisible = true
            }
            is OrderMenuState.Success -> {
                handleSuccessState(it)
            }
            /* 1-2-1. 장바구니 :
            메뉴 나열 : 담는 정보는 "가게 상세"의 메뉴 나열과 같음
	        주문하기 : 장바구니 클리어, 주문완료
            *  주문하기를 누른다.
            */
            is OrderMenuState.Order -> {
                handleOrderState()
            }
            is OrderMenuState.Error -> {
                handleErrorState(it)
            }
            else -> Unit
        }
    }

    private fun handleSuccessState(state: OrderMenuState.Success) = with(binding) {
        binding.progressBar.isGone = true
        adapter.submitList(state.restaurantFoodModelList)
        val menuOrderIsEmpty = state.restaurantFoodModelList.isNullOrEmpty()
        binding.confirmButton.isEnabled = menuOrderIsEmpty.not()
        if(menuOrderIsEmpty) {
            Toast.makeText(this@OrderMenuListActivity, "주문 메뉴가 없어 화면을 종료합니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /* 1-2-1. 장바구니 :
    메뉴 나열 : 담는 정보는 "가게 상세"의 메뉴 나열과 같음
	주문하기 : 장바구니 클리어, 주문완료
    *  주문 완료 후 장바구니 페이지를 닫는다.
    */
    private fun handleOrderState() {
        Toast.makeText(this, "성공적으로 주문을 완료하였습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun handleErrorState(state: OrderMenuState.Error) {
        Toast.makeText(this, getString(state.messageId, state.e.message), Toast.LENGTH_SHORT).show()
    }

}
