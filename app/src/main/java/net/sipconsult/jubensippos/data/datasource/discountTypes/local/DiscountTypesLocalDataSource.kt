package net.sipconsult.jubensippos.data.datasource.discountTypes.local

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.DiscountTypesItem

interface DiscountTypesLocalDataSource {

    val discountTypes: LiveData<List<DiscountTypesItem>>

    fun updateDiscountTypes(discountTypes: List<DiscountTypesItem>)
}