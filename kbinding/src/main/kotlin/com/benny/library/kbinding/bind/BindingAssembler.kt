package com.benny.library.kbinding.bind

import android.util.Log
import com.benny.library.kbinding.converter.*
import rx.Observable
import rx.functions.Action1
import rx.subscriptions.CompositeSubscription
import java.util.*

/**
 * Created by benny on 11/18/15.
 */

public inline fun <T, R> oneWayPropertyBinding(key: String, observable: Observable<T>, converter: OneWayConverter<R> = EmptyOneWayConverter<R>()) : OneWayPropertyBinding<T, R> {
    return OneWayPropertyBinding(key, observable, converter)
}

public inline fun <T> oneWayPropertyBinding(observer: Action1<in T>, oneTime: Boolean, backConverter: OneWayConverter<T> = EmptyOneWayConverter<T>(), vararg keys: String) : PropertyBinding {
    return if(keys.size == 1) OneWayPropertyBinding<T, Any>(keys[0], observer, oneTime, backConverter) else MultiplePropertyBinding(keys.toList(), observer, oneTime, backConverter as MultipleConverter<T>)
}

public inline fun <T> oneWayPropertyBinding(key: String, observer: Action1<in T>, oneTime: Boolean, backConverter: OneWayConverter<T> = EmptyOneWayConverter<T>()) : OneWayPropertyBinding<T, Any> {
    return OneWayPropertyBinding(key, observer, oneTime, backConverter)
}

public inline fun <T> multiplePropertyBinding(keys: List<String>, observer: Action1<in T>, oneTime: Boolean, multipleConverter: MultipleConverter<T>) : MultiplePropertyBinding<T> {
    return MultiplePropertyBinding(keys, observer, oneTime, multipleConverter)
}

public inline fun <T, R> twoWayPropertyBinding(key: String, observable: Observable<T>, observer: Action1<in T>, converter: TwoWayConverter<T, R> = EmptyTwoWayConverter<T, R>()) : TwoWayPropertyBinding<T, R> {
    return TwoWayPropertyBinding(key, observable, observer, converter)
}

public inline fun <T> commandBinding(key: String, trigger: Observable<T>, canExecute: Action1<in Boolean> = Action1 {}) : CommandBinding<T> {
    return CommandBinding(key, trigger, canExecute)
}

open public class BindingAssembler {
    private val oneWayPropertyBindings = ArrayList<OneWayPropertyBinding<*, *>>()
    private val multiplePropertyBindings = ArrayList<MultiplePropertyBinding<*>>()
    private val twoWayPropertyBindings = ArrayList<TwoWayPropertyBinding<*, *>>()
    private var commandBindings = ArrayList<CommandBinding<*>>()

    public fun addBinding(propertyBinding: PropertyBinding): Unit {
        when (propertyBinding) {
            is CommandBinding<*> -> commandBindings.add(propertyBinding)
            is OneWayPropertyBinding<*, *> -> oneWayPropertyBindings.add(propertyBinding)
            is MultiplePropertyBinding<*> -> multiplePropertyBindings.add(propertyBinding)
            is TwoWayPropertyBinding<*, *> -> twoWayPropertyBindings.add(propertyBinding)
            else -> { }
        }
    }

    public fun bindTo(bindingDisposer: BindingDisposer, viewModel: IViewModel): Unit {
        val cs: CompositeSubscription = CompositeSubscription()
        oneWayPropertyBindings.forEach { propertyBinding -> cs.add(viewModel.bind(propertyBinding)) }
        twoWayPropertyBindings.forEach { propertyBinding -> cs.add(viewModel.bind(propertyBinding)) }
        multiplePropertyBindings.forEach { propertyBinding -> cs.add(viewModel.bind(propertyBinding)) }
        commandBindings.forEach { commandBinding: CommandBinding<*> -> cs.add(viewModel.bind(commandBinding)) }
        bindingDisposer.add { cs.unsubscribe() }
        clear()
    }

    public fun merge(prefix: String, assembler: BindingAssembler) {
        assembler.oneWayPropertyBindings.forEach { it -> oneWayPropertyBindings.add(it.prefix(prefix)) }
        assembler.multiplePropertyBindings.forEach { it -> multiplePropertyBindings.add(it.prefix(prefix)) }
        assembler.twoWayPropertyBindings.forEach { it -> twoWayPropertyBindings.add(it.prefix(prefix)) }
        assembler.commandBindings.forEach { it -> commandBindings.add(it.prefix(prefix)) }
    }

    private fun clear() {
        oneWayPropertyBindings.clear()
        twoWayPropertyBindings.clear()
        multiplePropertyBindings.clear()
        commandBindings.clear()
    }
}