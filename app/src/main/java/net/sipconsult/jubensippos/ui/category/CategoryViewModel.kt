package net.sipconsult.jubensippos.ui.category

import androidx.lifecycle.ViewModel
import net.sipconsult.jubensippos.data.repository.CategoryRepository
import net.sipconsult.jubensippos.internal.lazyDeferred

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categories by lazyDeferred {
        categoryRepository.getCategories()
    }
}
