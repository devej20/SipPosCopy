package net.sipconsult.jubensippos.data.repository.discountType

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.DiscountTypesItem

interface DiscountTypeRepository {

    suspend fun getDiscountTypes(): LiveData<List<DiscountTypesItem>>
}