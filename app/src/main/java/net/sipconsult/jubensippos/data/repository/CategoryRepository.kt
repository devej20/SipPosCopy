package net.sipconsult.jubensippos.data.repository

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.network.response.ProductCategories

interface CategoryRepository {
    suspend fun getCategories(): LiveData<ProductCategories>
}