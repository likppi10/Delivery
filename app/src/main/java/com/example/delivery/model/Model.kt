package com.example.delivery.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil


abstract class Model(
    open val id: Long,
    open val type: CellType,
) {

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Model> = object : DiffUtil.ItemCallback<Model>() {
            //리사이클러뷰에 아이템의 아이디가 같읁 확인
            override fun areItemsTheSame(@NonNull oldItem: Model, @NonNull newItem: Model): Boolean {
                return oldItem.id == newItem.id && oldItem.type == newItem.type
            }
            /*
            '==' : 값이 같은지 비교
            "===" : 주소가 같은지 비교
             */
            //왼전히 아이템이 같은지 비교
            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(@NonNull oldItem: Model, @NonNull newItem: Model): Boolean {
                return oldItem === newItem
            }
        }
    }
}

