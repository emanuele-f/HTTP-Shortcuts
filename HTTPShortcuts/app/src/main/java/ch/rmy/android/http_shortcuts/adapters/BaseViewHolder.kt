package ch.rmy.android.http_shortcuts.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ch.rmy.android.http_shortcuts.realm.models.HasId
import io.realm.RealmObject

abstract class BaseViewHolder<in T>(parent: ViewGroup, layoutRes: Int, baseAdapter: BaseAdapter<T>) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)) where T : RealmObject, T : HasId {

    private var item: T? = null

    init {
        itemView.setOnClickListener {
            baseAdapter.clickListener?.onItemClicked(item!!)
        }
        itemView.setOnLongClickListener {
            baseAdapter.clickListener?.onItemLongClicked(item!!)
            true
        }
    }

    fun setItem(item: T) {
        this.item = item
        updateViews(item)
    }

    protected abstract fun updateViews(item: T)

}