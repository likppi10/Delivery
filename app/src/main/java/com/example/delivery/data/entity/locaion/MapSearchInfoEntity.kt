package com.example.delivery.data.entity.locaion

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//LocationLatLngEntity를 mapAPIService로 주소를 구해서 이제 갖다쓰려고 담아놓는 객체
@Parcelize
data class MapSearchInfoEntity(
    val fullAddress: String,
    val name: String,
    val locationLatLng: LocationLatLngEntity
): Parcelable
