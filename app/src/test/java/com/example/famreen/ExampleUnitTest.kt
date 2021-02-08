package com.example.famreen

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import java.util.concurrent.TimeUnit


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    var count = 2
    private val obs = Observable.create<Long> {
        it.onNext(1)

    }
    private val updateSubject = PublishSubject.create<Void>()
    @Test
    fun addition_isCorrect() {
        /*val f: Flowable<String> = Flowable.defer(){
            val int = AtomicInteger()
            Flowable.just("a","2","3","4").map { it + " "+ int.incrementAndGet() }
        }
        f.subscribe { println(it) }
        f.subscribe { println(it) }*/
       /* val s = System.currentTimeMillis()
        t(s)
        val o = Observable.interval(120,2,TimeUnit.MILLISECONDS).doOnNext { print("interval - ")
            t(s) }

        obs.concatMap { i-> Observable.just(i).delay(20, TimeUnit.MILLISECONDS) }.skipUntil(o).subscribe { print(
            "test$it  "
        )
            t(s) }
        Thread.sleep(400L)*/
        val myObservable = Observable.just("a", "b", "c", "d").retryWhen { Observable.error<String>(NullPointerException())}


    }
    fun t(s: Long){
        println("time: " + (System.currentTimeMillis() - s))
    }
    @Test
    fun test(){
        obs.onErrorResumeNext(Observable.error(NullPointerException())).doOnError{ println("error") }.doOnComplete { println(
            "complete"
        )}.subscribe { println(it) }
        Thread.sleep(2000L)
    }
    @Test
    fun subscribeOnTest(){
        Observable.create<Int> {
            it.onNext(1)
            gtn()
            println("hi from emitter")
        }
            .doOnNext { gtn() }
            .subscribeOn(Schedulers.io())
            .doOnNext { gtn() }
            .subscribeOn(Schedulers.computation())
            .doOnNext { gtn() }
            .observeOn(Schedulers.newThread())
            .doOnNext { gtn() }
            .subscribe {
                println("Thread: [" + Thread.currentThread().name + "]")
            }

    }
    fun gtn(){
        println("Thread: [" + Thread.currentThread().name + "]")
    }
    @Test
    fun asd(){
        val a = Observable.just(1,2,3).delay(25,TimeUnit.MILLISECONDS)
        val b = Observable.just(6,7,8).delay(50,TimeUnit.MILLISECONDS)
        val list = ArrayList<Observable<Int>>()
        list.add(a)
        list.add(b)
        Observable.amb(list).subscribe { println("IFRST !!! - $it") }

        Thread.sleep(500L)
    }
}
