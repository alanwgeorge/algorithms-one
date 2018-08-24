package com.alangeorge.algorithums.one

import com.google.gson.GsonBuilder
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber

data class Foo(val s1: String, val i2: Int): IsCached()
data class Bar(val i1: Int, val s2: String): IsCached()

abstract class IsCached(var cached: Boolean = false)

class Repository(val service: Service, val cache: Cache) {

    fun getFoo(useCache: Boolean = true): Single<Foo> =
            get(useCache, cache.getFoo(), service.getFoo().doOnSuccess(cache.cacheFoo))

    fun getBar(useCache: Boolean = true): Single<Bar> =
            get(useCache, cache.getBar(), service.getBar().doOnSuccess(cache.cacheBar))

    private fun <T> get(useCache: Boolean, cached: Maybe<T>, fetchWithCaching: Single<T>): Single<T> =
        when (useCache) {
            true -> cached
                    .switchIfEmpty(fetchWithCaching.toMaybe())
                    .toSingle()
            false -> fetchWithCaching
        }
}

class Service {
    var counter: Int = 0
        get() {
            field = field.inc()
            println("inc()")
            return field
        }

    fun getFoo() = Single.create<Foo> { emitter ->
        emitter.onSuccess(Foo("foo", counter))
    }

    fun getBar() = Single.create<Bar> { emitter ->
        emitter.onSuccess(Bar(counter, "bar"))
    }
}

class Cache() {
    private val keyMap = mapOf(
            Foo::class.java to "foo",
            Bar::class.java to "bar"
    )

    private val cache = mutableMapOf<String, String>()
    private val gson = GsonBuilder().create()

    fun getBar(): Maybe<Bar> = get()

    fun getFoo(): Maybe<Foo> = get()

    private inline fun <reified T: IsCached> get(): Maybe<T> =
            Maybe.create { emitter ->
                keyMap[T::class.java]?.let { key ->
                    cache[key]?.let {
                        println("cache hit: $it")
                        emitter.onSuccess(gson.fromJson(it, T::class.java))
                    }
                } ?: emitter.onComplete()
            }

    val cacheFoo = Consumer<Foo> {
        cache(it.copy())
    }

    val cacheBar = Consumer<Bar> {
        cache(it.copy())
    }

    private inline fun <reified T: IsCached> cache(me: T) =
            with(me) {
                keyMap[T::class.java]?.let {
                    cached = true
                    cache.put(it, gson.toJson(this, T::class.java))
                }
            }

}

fun main(args: Array<String>) {
    val repo = Repository(Service(), Cache())

    val block: TestObserver<IsCached>.() -> Unit = {
        assertComplete()
        values().forEach {
            println("$it ${it.cached}")
        }
    }

    val fooBlock = block as TestObserver<Foo>.() -> Unit
    val barBlock = block as TestObserver<Bar>.() -> Unit

    with(repo.getFoo().test(), fooBlock)
    with(repo.getBar().test(), barBlock)
    with(repo.getFoo().test(), fooBlock)
    with(repo.getFoo(false).test(), fooBlock)
}