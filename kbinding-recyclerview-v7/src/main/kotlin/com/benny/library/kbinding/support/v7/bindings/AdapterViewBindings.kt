@file:Suppress("UNUSED_PARAMETER")

package com.benny.library.kbinding.support.v7.bindings

import android.support.v7.widget.RecyclerView
import rx.Observable
import rx.functions.Action1

import com.benny.library.kbinding.bind.*
import com.benny.library.kbinding.converter.*
import com.benny.library.kbinding.common.bindings.utils.AdapterViewPagingOnSubscribe
import com.benny.library.kbinding.common.adapter.AdapterPagingListener
import com.benny.library.kbinding.common.adapter.BaseListPagingAdapter
import com.benny.library.kbinding.common.bindings.paging
import com.benny.library.kbinding.support.v7.adapter.BaseRecyclerPagingAdapter

/**
 * Created by benny on 12/16/15.
 */

fun RecyclerView.swapAdapter() : Action1<RecyclerView.Adapter<RecyclerView.ViewHolder>> {
    return Action1 { it ->
        this.swapAdapter(it, false)
        (adapter as? BaseRecyclerPagingAdapter<*>)?.pagingListener = this.tag as? AdapterPagingListener
    }
}

fun RecyclerView.paging(): Observable<Pair<Int, Any?>> = Observable.create(AdapterViewPagingOnSubscribe(this))

//Event

fun RecyclerView.paging(path: String) : PropertyBinding = commandBinding(path, paging(), Action1 { (this.adapter as? BaseRecyclerPagingAdapter<*>)?.loadComplete(!it) })

//Property

fun RecyclerView.adapter(vararg paths: String, mode: OneWay = BindingMode.OneWay, converter: OneWayConverter<RecyclerView.Adapter<RecyclerView.ViewHolder>> = EmptyOneWayConverter()) : PropertyBinding = oneWayPropertyBinding(paths, swapAdapter(), false, converter)
fun RecyclerView.adapter(vararg paths: String, mode: OneTime, converter: OneWayConverter<RecyclerView.Adapter<RecyclerView.ViewHolder>> = EmptyOneWayConverter()) : PropertyBinding = oneWayPropertyBinding(paths, swapAdapter(), true, converter)
