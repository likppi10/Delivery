package com.example.delivery.data.entity.locaion

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

//위치 정보 구하려고 위, 경도 값 구해서 객체로 보내는 역할
@Parcelize
@Entity
data class LocationLatLngEntity(
    val latitude: Double,
    val longitude: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1,
): Parcelable {

    override fun equals(other: Any?): Boolean {
        return if (other is LocationLatLngEntity) {
            (latitude == other.latitude
                && longitude == other.longitude)
        } else {
            this === other
        }
    }

}

