package com.example.delivery.screen.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

    /*
    * 액티비티의 틀로, onCreate 함수에서는 view를 바인딩해주고, 첫 상태를 initState 해준다.
    * initState에서는 상태에 따라 보여줄 view를 정하고, 해당 액티비티가 가지는 viewmodel에 따른 초기 fetchData를 해준다.
    * 그리고, 데이터 관찰을 위한 obseveData 메서드를 함께 실행한다.
    */

abstract class BaseActivity<VM: BaseViewModel, VB: ViewBinding>: AppCompatActivity() {

    abstract val viewModel: VM

    protected lateinit var binding: VB

    abstract fun getViewBinding(): VB

    private lateinit var fetchJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        initState()
    }

    open fun initState() {
        initViews()
        fetchJob = viewModel.fetchData()
        observeData()
    }

    open fun initViews() = Unit

    // 데이터를 관찰해서 처리해주기 위한 메서드
    abstract fun observeData()

    override fun onDestroy() {
        if (fetchJob.isActive) {
            fetchJob.cancel()
        }
        super.onDestroy()
    }

}
