package com.example.delivery.data.repository.map

import com.example.delivery.data.entity.locaion.LocationLatLngEntity
import com.example.delivery.data.network.MapApiService
import com.example.delivery.data.response.address.AddressInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultMapRepository(
    //4
    private val mapApiService: MapApiService,
    private val ioDispatcher: CoroutineDispatcher
): MapRepository {

    //String이 아닌 addressInfo라는 객체로 주소값을 받는다.
    override suspend fun getReverseGeoInformation(
        locationLatLngEntity: LocationLatLngEntity
    ): AddressInfo? = withContext(ioDispatcher) {
        val response = mapApiService.getReverseGeoCode(
            lat = locationLatLngEntity.latitude,
            lon = locationLatLngEntity.longitude
        )
        if (response.isSuccessful) {
            response.body()?.addressInfo
        } else {
            null
        }
    }
}
