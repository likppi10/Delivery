package com.example.delivery.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.delivery.data.db.dao.FoodMenuBasketDao
import com.example.delivery.data.db.dao.LocationDao
import com.example.delivery.data.db.dao.RestaurantDao
import com.example.delivery.data.entity.locaion.LocationLatLngEntity
import com.example.delivery.data.entity.restaurant.RestaurantEntity
import com.example.delivery.data.entity.restaurant.RestaurantFoodEntity

@Database(
    entities = [LocationLatLngEntity::class, RestaurantFoodEntity::class, RestaurantEntity::class],
    version = 1,
    exportSchema = false
)
/* 4. 기본 설정 및 data :
*  현재 위치, 장바구니의 담겨 있는 메뉴들, 그 메뉴들의 가게 정보가 담겨 있다.
*/
abstract class ApplicationDatabase: RoomDatabase() {

    companion object {
        const val DB_NAME = "ApplicationDataBase.db"
    }

    abstract fun LocationDao(): LocationDao

    abstract fun FoodMenuBasketDao(): FoodMenuBasketDao

    abstract fun RestaurantDao(): RestaurantDao

}
