package com.example.delivery.di

import com.example.delivery.data.TestOrderRepository
import com.example.delivery.data.TestRestaurantFoodRepository
import com.example.delivery.data.TestRestaurantRepository
import com.example.delivery.data.entity.locaion.LocationLatLngEntity
import com.example.delivery.data.repository.order.OrderRepository
import com.example.delivery.data.repository.restaurant.RestaurantRepository
import com.example.delivery.data.repository.restaurant.food.RestaurantFoodRepository
import com.example.delivery.screen.home.restaurant.RestaurantCategory
import com.example.delivery.screen.home.restaurant.RestaurantListViewModel
import com.example.delivery.screen.order.OrderMenuListViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appTestModule = module{
    viewModel { (restaurantCategory: RestaurantCategory, locationLatLngEntity: LocationLatLngEntity) ->
        RestaurantListViewModel(restaurantCategory, locationLatLngEntity, get())
    }

    viewModel { (firebaseAuth: FirebaseAuth) -> OrderMenuListViewModel(get(), get(), firebaseAuth) }

    single<RestaurantRepository> { TestRestaurantRepository() }

    single<RestaurantFoodRepository> { TestRestaurantFoodRepository() }

    single<OrderRepository> { TestOrderRepository() }
}