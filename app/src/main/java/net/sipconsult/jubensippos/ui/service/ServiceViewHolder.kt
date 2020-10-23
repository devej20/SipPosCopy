package net.sipconsult.jubensippos.ui.service

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.jubensippos.data.models.ServiceItem

class ServiceViewHolder(itemView: View, onServiceClick: (ServiceItem) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var _service: ServiceItem
    fun bind(service: ServiceItem, position: Int) {
        _service = service
    }

    init {
        itemView.setOnClickListener { onServiceClick(_service) }
    }
}