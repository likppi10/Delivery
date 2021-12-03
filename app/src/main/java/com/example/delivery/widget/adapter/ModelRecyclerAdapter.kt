package com.example.delivery.widget.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.delivery.DeliveryApplication
import com.example.delivery.model.CellType
import com.example.delivery.model.Model
import com.example.delivery.screen.base.BaseViewModel
import com.example.delivery.util.mapper.ModelViewHolderMapper
import com.example.delivery.util.provider.DefaultResourcesProvider
import com.example.delivery.util.provider.ResourcesProvider
import com.example.delivery.widget.adapter.listener.AdapterListener
import com.example.delivery.widget.adapter.viewholder.ModelViewHolder

class ModelRecyclerAdapter<M : Model, VM: BaseViewModel>(
    private var modelList: List<Model>,
    private var viewModel: VM,
    private val resourcesProvider: ResourcesProvider = DefaultResourcesProvider(DeliveryApplication.appContext!!),
    private val adapterListener: AdapterListener
) : ListAdapter<Model, ModelViewHolder<M>>(Model.DIFF_CALLBACK) {

    override fun getItemCount(): Int = modelList.size

    //ordinal : enum 클래스에서 해당하는 값을 불러오기 위한 인덱스로써 반환
    override fun getItemViewType(position: Int) = modelList[position].type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder<M> {
        return ModelViewHolderMapper.map(parent, CellType.values()[viewType], viewModel, resourcesProvider)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ModelViewHolder<M>, position: Int) {
        with(holder) {
            bindData(modelList[position] as M)
            bindViews(modelList[position] as M, adapterListener)
        }
    }

    //let : null이 아닐경우에 코드를 실행햐아 함
    override fun submitList(list: List<Model>?) {
        list?.let { modelList = it }
        super.submitList(list)
    }
}
