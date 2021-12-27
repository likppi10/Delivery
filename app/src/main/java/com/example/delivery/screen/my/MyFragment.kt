package com.example.delivery.screen.my

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.delivery.R
import com.example.delivery.databinding.FragmentMyBinding
import com.example.delivery.extensions.load
import com.example.delivery.model.order.OrderModel
import com.example.delivery.screen.base.BaseFragment
import com.example.delivery.screen.review.AddRestaurantReviewActivity
import com.example.delivery.widget.adapter.ModelRecyclerAdapter
import com.example.delivery.widget.adapter.listener.order.OrderListListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.android.viewmodel.ext.android.viewModel

class MyFragment: BaseFragment<MyViewModel, FragmentMyBinding>() {

    override val viewModel by viewModel<MyViewModel>()

    override fun getViewBinding(): FragmentMyBinding = FragmentMyBinding.inflate(layoutInflater)

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val gsc by lazy { GoogleSignIn.getClient(requireActivity(), gso) }

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    /* 3. 내정보 탭
       3-1. 구글 로그인 : 로그인 기록 없을 시 하게 함
    *  로그인 런처 생성
    */
    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                task.getResult(ApiException::class.java)?.let { account ->
                    Log.e(TAG, "firebaseAuthWithGoogle: ${account.id}")
                    viewModel.saveToken(account.idToken ?: throw Exception())
                } ?: throw Exception()
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    /* 3. 내정보 탭
     3-3. 주문 내역
    *  주문내역 내열을 위한 리사이클러뷰 어댑터
    *  주문 내역 리스트 아이템을 클릭하면 리뷰 작성페이지로 이동한다.
    */
    private val adapter by lazy {
        ModelRecyclerAdapter<OrderModel, MyViewModel>(listOf(), viewModel, adapterListener = object : OrderListListener {

            override fun writeRestaurantReview(orderId: String, restaurantTitle: String) {
                startActivity(
                    AddRestaurantReviewActivity.newIntent(requireContext(), orderId, restaurantTitle)
                )
            }

        })
    }

    private var isFirstShown = false

    override fun onResume() {
        super.onResume()
        if (isFirstShown.not()) {
            isFirstShown = true
        } else {
            viewModel.fetchData()
        }
    }

    /* 3. 내정보 탭
       3-1. 구글 로그인 : 로그인 기록 없을 시 하게 함
    *  로그인 버튼 클릭 시
    *  로그아웃 버튼 클릭 시
    *  아직 내 정보 화면을 초기화 하기 전의 상태, 리스너를 생성하여 구현한다.
    */
    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter
        loginButton.setOnClickListener {
            signInGoogle()
        }
        logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            viewModel.signOut()
        }
    }

    /* 3. 내정보 탭
       3-1. 구글 로그인 : 로그인 기록 없을 시 하게 함
    *  구글 로그인 런처 실행
    */
    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    override fun observeData() = viewModel.myStateLiveData.observe(this) {
        when (it) {
            is MyState.Uninitialized -> initViews()
            is MyState.Loading -> handleLoadingState()
            is MyState.Login -> handleLoginState(it)
            is MyState.Success -> handleSuccessState(it)
            is MyState.Error -> handleErrorState(it)
        }
    }

    /* 3. 내정보 탭
      3-1. 구글 로그인 : 로그인 기록 없을 시 하게 함
    *  리스너 연결 후 로딩 중 구현
    */
    private fun handleLoadingState() = with(binding) {
        progressBar.isVisible = true
        loginRequiredGroup.isGone = true
    }

    /* 3. 내정보 탭
      3-1. 구글 로그인 : 로그인 기록 없을 시 하게 함
    *  로그인 결과에 따른 분기
    */
    private fun handleSuccessState(state: MyState.Success) = with(binding) {
        progressBar.isGone = true
        when (state) {
            is MyState.Success.Registered -> {
                handleRegisteredState(state)
            }
            is MyState.Success.NotRegistered -> {
                profileGroup.isGone = true
                loginRequiredGroup.isVisible = true
            }
        }
    }

    /* 3. 내정보 탭
      3-1. 구글 로그인 : 로그인 기록 없을 시 하게 함
    *  구글 로그인을 파이어베이스의 auth로 진행
    */
    private fun handleLoginState(state: MyState.Login) = with(binding) {
        binding.progressBar.isVisible = true
        val credential = GoogleAuthProvider.getCredential(state.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    viewModel.setUserInfo(user)
                } else {
                    firebaseAuth.signOut()
                    viewModel.setUserInfo(null)
                }
            }
    }

    /* 3. 내정보 탭
       3-2. 사용자 정보 :
    *  로그인 결과가 잘 작동 했을 때, 사용자의 정보를 표시한다.
    */
    private fun handleRegisteredState(state: MyState.Success.Registered) = with(binding) {
        profileGroup.isVisible = true
        loginRequiredGroup.isGone = true
        profileImageView.load(state.profileImageUri.toString(), 60f)
        userNameTextView.text = state.userName

        if (state.orderList.isEmpty()) {
            emptyResultTextView.isGone = false
            recyclerView.isGone = true
        } else {
            emptyResultTextView.isGone = true
            recyclerView.isGone = false
            adapter.submitList(state.orderList)
        }
    }

    private fun handleErrorState(state: MyState.Error) {
        Toast.makeText(requireContext(), state.messageId, Toast.LENGTH_SHORT).show()
    }

    companion object {

        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"

    }

}
