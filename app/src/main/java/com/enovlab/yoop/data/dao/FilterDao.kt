package com.enovlab.yoop.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.enovlab.yoop.data.entity.City
import com.enovlab.yoop.data.entity.FilterOptions
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by mtosk on 3/13/2018.
 */

@Dao
abstract class FilterDao {

    @Query("SELECT * FROM cities WHERE name LIKE :query || '%' OR region_abbreviation LIKE :query || '%' ORDER BY name ASC")
    abstract fun getCities(query: String): Flowable<List<City>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveCities(cities: List<City>)

    @Query("SELECT * FROM filters LIMIT 1")
    abstract fun getFilterOptions(): Single<FilterOptions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveFilterOptions(filterOptions: FilterOptions)

    @Query("DELETE FROM filters WHERE id = :id")
    abstract fun deleteFilter(id: Long = 0L)
}