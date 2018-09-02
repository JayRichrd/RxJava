package com.tencent.cain.rxjava;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Main {
    public static void main(String[] args) {
//        demo();
        schedulerDemo();
    }

    /**
     * 经典使用RxJava三部曲
     */
    static void demo() {
        /**
         * 1.创建观察者Observer
         */
        Observer<String> observer = new Observer<String>() {
            public void onSubscribe(Disposable disposable) {

            }

            public void onNext(String s) {
                System.out.println("Observer#onNext()#s: " + s);
            }

            public void onError(Throwable throwable) {
                System.out.println("Observer#onError()#throwable: " + throwable.getMessage());
            }

            public void onComplete() {
                System.out.println("Observer#onComplete()");
            }
        };

        /**
         * 2.创建被观察者Observable
         */
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("from Observable#subscribe#onNext()");
                emitter.onComplete();
            }
        });

        /**
         * 3.创建被观察者和观察者之间的连接
         */
        observable.subscribe(observer);

        Observable<Integer> observable1 = Observable.just(1, 2, 3, 4, 5);
        observable1.subscribe(new Observer<Integer>() {
            public void onSubscribe(Disposable d) {
                System.out.println("Observer#onSubscribe()");
            }

            public void onNext(Integer integer) {
                System.out.println("Observer#onNext()#integer: " + integer);
            }

            public void onError(Throwable e) {

            }

            public void onComplete() {
                System.out.println("Observer#onComplete()");
            }
        });

        observable1.subscribe(new Consumer<Integer>() {
            public void accept(Integer integer) throws Exception {
                System.out.println("Consumer#accept()#integer: " + integer);
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) throws Exception {

            }
        }, new Action() {
            public void run() throws Exception {
                System.out.println("Action#run()");

            }
        });

    }

    /**
     * 线程操作的一些例子
     */
    static void schedulerDemo() {
//        .subscribeOn(Schedulers.io())
        // todo 为何添加上subscribeOn后就无法发射和接收了
        Observable.create(new ObservableOnSubscribe<Integer>() {
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("in " + Thread.currentThread().getName() + " ObservableOnSubscribe#subscribe()");
                emitter.onNext(1);
            }
        }).observeOn(Schedulers.newThread()).subscribe(new Consumer<Integer>() {
            public void accept(Integer integer) throws Exception {
                System.out.println("in " + Thread.currentThread().getName() + " Consumer#accept()#integer: " + integer);
            }
        });

    }
}
