package com.enovlab.yoop.data.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by mtosk on 3/13/2018.
 */

@Entity(
    tableName = "cities",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class City (

    @PrimaryKey
    @SerializedName("cityId")
    var id: String,

    @SerializedName("cityName")
    var name: String?,

    @SerializedName("geoRegionId")
    @ColumnInfo(name = "region_id")
    var regionId: String?,

    @SerializedName("geoRegionName")
    @ColumnInfo(name = "region_name")
    var regionName: String?,

    @SerializedName("geoRegionAbbreviation")
    @ColumnInfo(name = "region_abbreviation")
    var regionAbbreviation: String?,

    @SerializedName("countryId")
    @ColumnInfo(name = "country_id")
    var countryId: String?,

    @SerializedName("countryName")
    @ColumnInfo(name = "country_name")
    var countryName: String?,

    @SerializedName("continentId")
    @ColumnInfo(name = "continent_id")
    var continentId: String?,

    @SerializedName("continentName")
    @ColumnInfo(name = "continent_name")
    var continentName: String?
)