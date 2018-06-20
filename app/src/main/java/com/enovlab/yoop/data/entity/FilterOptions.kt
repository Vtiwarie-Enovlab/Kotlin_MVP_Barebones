package com.enovlab.yoop.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by mtosk on 3/13/2018.
 */

@Entity(tableName = "filters")
data class FilterOptions (

    @PrimaryKey
    var id: Long,

    var locationName: String?,

    var locationId: String?,

    var locationLatitude: Double?,

    var locationLongitude: Double?,

    var searchRadius: Int,

    var saleState: SaleState?

) {

    fun radiusInMiles(): Long {
        return (searchRadius.toFloat() * MILES_MULTIPLY).toLong() * 1000L
    }

    fun isEmpty(): Boolean {
        return locationName == null
            && locationId == null
            && locationLatitude == null
            && locationLongitude == null
            && searchRadius == DEFAULT_RADIUS
            && saleState == SaleState.ALL
    }

    enum class SaleState {
        ALL, FIRST_ACCESS, ON_SALE
    }

    companion object {
        private const val MILES_MULTIPLY = 1.6F
        const val DEFAULT_RADIUS = 20

        fun empty() = FilterOptions(0L, null, null, null, null, DEFAULT_RADIUS, SaleState.ALL)
    }
}