package com.example.delivery.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import com.example.delivery.di.appTestModule
import com.example.delivery.livedata.LiveDataTestObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule


@ExperimentalCoroutinesApi
internal abstract class ViewModelTest: KoinTest {

    //모킷토 룰 지정
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    //
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // 주입해줄 방법이 없기때문에 직접 선언
    @Mock
    private lateinit var context: Application

    //코루틴 테스트용

    private val dispatcher = TestCoroutineDispatcher()

    //코인에 대한 의존성 주입을 위해 사용
    @Before
    fun setup(){
        startKoin{
            androidContext(context)
            modules(appTestModule)
        }
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDone(){
        stopKoin()
        Dispatchers.resetMain()
    }

    protected fun <T> LiveData<T>.test(): LiveDataTestObserver<T> {
        val testObserver = LiveDataTestObserver<T>()
        observeForever(testObserver)
        return testObserver
    }
}