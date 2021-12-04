package com.example.delivery.di

import com.example.delivery.data.entity.locaion.LocationLatLngEntity
import com.example.delivery.data.entity.locaion.MapSearchInfoEntity
import com.example.delivery.data.entity.restaurant.RestaurantEntity
import com.example.delivery.data.entity.restaurant.RestaurantFoodEntity
import com.example.delivery.data.preference.AppPreferenceManager
import com.example.delivery.data.repository.map.DefaultMapRepository
import com.example.delivery.data.repository.map.MapRepository
import com.example.delivery.data.repository.order.DefaultOrderRepository
import com.example.delivery.data.repository.order.OrderRepository
import com.example.delivery.data.repository.restaurant.DefaultRestaurantRepository
import com.example.delivery.data.repository.restaurant.RestaurantRepository
import com.example.delivery.data.repository.restaurant.food.DefaultRestaurantFoodRepository
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository
import com.example.delivery.data.repository.restaurant.review.DefaultRestaurantReviewRepository
import com.example.delivery.data.repository.restaurant.review.RestaurantReviewRepository
import com.example.delivery.data.repository.user.DefaultUserRepository
import com.example.delivery.data.repository.user.UserRepository
import com.example.delivery.screen.MainViewModel
import com.example.delivery.screen.home.HomeViewModel
import com.example.delivery.screen.home.restaurant.RestaurantCategory
import com.example.delivery.screen.home.restaurant.RestaurantListViewModel
import com.example.delivery.screen.home.restaurant.detail.RestaurantDetailViewModel
import com.example.delivery.screen.home.restaurant.detail.menu.RestaurantMenuListViewModel
import com.example.delivery.screen.home.restaurant.detail.review.RestaurantReviewListViewModel
import com.example.delivery.screen.like.RestaurantLikeListViewModel
import com.example.delivery.screen.my.MyViewModel
import com.example.delivery.screen.mylocation.MyLocationViewModel
import com.example.delivery.screen.order.OrderMenuListViewModel
import com.example.delivery.screen.review.gallery.GalleryPhotoRepository
import com.example.delivery.screen.review.gallery.GalleryViewModel
import com.example.delivery.util.event.MenuChangeEventBus
import com.example.delivery.util.provider.DefaultResourcesProvider
import com.example.delivery.util.provider.ResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    // 메인 액티비티와 하단 탭 화면에서 쓰는 뷰모델
    viewModel { MainViewModel() }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { RestaurantLikeListViewModel(get()) }
    viewModel { MyViewModel(get(), get(), get()) }

    factory { (restaurantCategory: RestaurantCategory, locationLatLngEntity: LocationLatLngEntity) ->
        RestaurantListViewModel(restaurantCategory, locationLatLngEntity, get())
    }

    viewModel { (mapSearchInfoEntity: MapSearchInfoEntity) ->
        MyLocationViewModel(mapSearchInfoEntity, get(), get())
    }

    viewModel { (restaurantEntity: RestaurantEntity) -> RestaurantDetailViewModel(restaurantEntity, get(), get()) }

    viewModel { (restaurantId: Long, restaurantFoodList: List<RestaurantFoodEntity>) ->
        RestaurantMenuListViewModel(restaurantId, restaurantFoodList, get())
    }

    viewModel { (restaurantTitle: String) -> RestaurantReviewListViewModel(restaurantTitle, get()) }

    viewModel { OrderMenuListViewModel(get(), get(), get()) }

    viewModel { GalleryViewModel(get()) }

    single<MapRepository> { DefaultMapRepository(get(), get()) }
    single<RestaurantRepository> { DefaultRestaurantRepository(get(), get(), get()) }
    single<UserRepository> { DefaultUserRepository(get(), get(), get()) }
    single<RestaurantFoodRepository> { DefaultRestaurantFoodRepository(get(), get(), get()) }
    single<OrderRepository> { DefaultOrderRepository(get(), get()) }
    single<RestaurantReviewRepository> { DefaultRestaurantReviewRepository(get(), get()) }
    single { GalleryPhotoRepository(androidApplication()) }

    // retrofit을 위한 요소
    single { provideGsonConverterFactory() }
    single { buildOkHttpClient() }

    single(named("map")) { provideMapRetrofit(get(), get()) }
    single(named("food")) { provideFoodRetrofit(get(), get()) }

    //3
    single { provideMapApiService(get(qualifier = named("map"))) }
    single { provideFoodApiService(get(qualifier = named("food"))) }

    //ProvideDB
    single { provideDB(androidApplication()) }
    single { provideLocationDao(get()) }
    single { provideFoodMenuBasketDao(get()) }
    single { provideRestaurantDao(get()) }

    // ResourcesProvider
    single<ResourcesProvider> { DefaultResourcesProvider(androidApplication()) }
    single { AppPreferenceManager(androidContext()) }

    single { MenuChangeEventBus() }

    //Coroutine에 필요한 요소
    single { Dispatchers.IO }
    single { Dispatchers.Main }

    single { Firebase.firestore }
    single { Firebase.storage }
    single { FirebaseAuth.getInstance() }

}
