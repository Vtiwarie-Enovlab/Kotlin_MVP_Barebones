package com.enovlab.yoop.api.serialize

import com.enovlab.yoop.data.entity.event.Fee
import com.enovlab.yoop.data.entity.event.TokenPrice
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import java.lang.reflect.Type

class TokenPriceDeserializer : JsonDeserializer<TokenPrice> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TokenPrice {
        val obj = json.asJsonObject
        val totalAmount = obj.get("totalAmount").asDouble
        val totalFees = obj.get("totalFees").asDouble
        val totalTax = obj.get("totalTax").asDouble
        val totalTokenAmount = obj.get("totalTokenAmount").asDouble

        var fee: Fee? = null

        val feesObj = obj.get("feeBreakdown")
        if (feesObj != null && feesObj !is JsonNull) {
            val fees = feesObj.asJsonArray
            if (fees.size() > 0) {
                val feeObj = fees.first().asJsonObject
                val feeAmount = feeObj.get("amount").asDouble
                val feeValue = feeObj.get("value").asDouble
                val feeName = feeObj.get("name").asString
                fee = Fee(feeAmount, feeValue, feeName)
            }
        }

        return TokenPrice(totalAmount, totalFees, totalTax, totalTokenAmount, fee)
    }
}