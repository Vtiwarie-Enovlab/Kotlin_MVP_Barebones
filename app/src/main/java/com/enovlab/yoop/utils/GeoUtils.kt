package com.enovlab.yoop.utils

/**
 * Created by vishaantiwarie on 3/16/18.
 */


class GeoUtils {
    companion object {
        fun getFullAddress(addressLine1: String?, addressLine2: String?, city: String?, geoRegionName: String?, zip: String?): String? {
            return "${addressLine1} ${addressLine2} ${city}, ${geoRegionName} ${zip}"
        }
    }
}

