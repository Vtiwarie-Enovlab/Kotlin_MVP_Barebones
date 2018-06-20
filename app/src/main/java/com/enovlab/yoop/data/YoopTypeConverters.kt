package com.enovlab.yoop.data

import android.arch.persistence.room.TypeConverter
import com.enovlab.yoop.data.entity.FilterOptions
import com.enovlab.yoop.data.entity.enums.*
import java.util.*

object YoopTypeConverters {

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    @JvmStatic
    fun fromStringEventMediaType(value: String?): EventMediaType? {
        return if (value == null) null else EventMediaType.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun eventMediaTypeToString(type: EventMediaType?): String? {
        return type?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringMarketplaceType(value: String?): MarketplaceType? {
        return if (value == null) null else MarketplaceType.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun marketplaceTypeToString(type: MarketplaceType?): String? {
        return type?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringOfferStatus(value: String?): OfferStatus? {
        return if (value == null) null else OfferStatus.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun offerStatusToString(status: OfferStatus?): String? {
        return status?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringOfferSubStatus(value: String?): OfferSubStatus? {
        return if (value == null) null else OfferSubStatus.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun offerSubStatusToString(status: OfferSubStatus?): String? {
        return status?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringUserAuthState(value: String?): UserAuthenticationState? {
        return if (value == null) null else UserAuthenticationState.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun userAuthStateToString(state: UserAuthenticationState?): String? {
        return state?.name
    }

//    @TypeConverter
//    @JvmStatic
//    fun fromStringFilterSearchRadius(value: String?): FilterOptions.SearchRadius? {
//        return if (value == null) null else FilterOptions.SearchRadius.valueOf(value)
//    }
//
//    @TypeConverter
//    @JvmStatic
//    fun filterSearchRadiusToString(searchRadius: FilterOptions.SearchRadius?): String? {
//        return searchRadius?.name
//    }

    @TypeConverter
    @JvmStatic
    fun fromStringFilterSaleState(value: String?): FilterOptions.SaleState? {
        return if (value == null) null else FilterOptions.SaleState.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun filterSaleStateToString(saleState: FilterOptions.SaleState?): String? {
        return saleState?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringSeatConfigurationType(value: String?): SeatConfigurationType? {
        return if (value == null) null else SeatConfigurationType.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun seatConfigurationTypeToString(type: SeatConfigurationType?): String? {
        return type?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringAssignmentStatus(value: String?): AssignmentStatus? {
        return if (value == null) null else AssignmentStatus.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun assignmentStatusToString(status: AssignmentStatus?): String? {
        return status?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringDemand(value: String?): Demand? {
        return if (value == null) null else Demand.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun demandToString(demand: Demand?): String? {
        return demand?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringChance(value: String?): Chance? {
        return if (value == null) null else Chance.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun chanceToString(chance: Chance?): String? {
        return chance?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringCardType(value: String?): CardType? {
        return if (value == null) null else CardType.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun cardTypeToString(type: CardType?): String? {
        return type?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringNotificationType(value: String?): NotificationType? {
        return if (value == null) null else NotificationType.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun notificationTypeToString(type: NotificationType?): String? {
        return type?.name
    }
}