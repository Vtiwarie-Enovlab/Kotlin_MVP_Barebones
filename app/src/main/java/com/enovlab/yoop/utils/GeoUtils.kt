package com.enovlab.yoop.utils

class GeoUtils {
    companion object {
        fun getFullAddress(addressLine1: String?, addressLine2: String?, city: String?, geoRegionName: String?, zip: String?): String? {
            return "${addressLine1} ${addressLine2} ${city}, ${geoRegionName} ${zip}"
        }
    }
}

