package com.tencent.cain.rxjava;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class Main {
    public static void main(String[] args) {
        demo();
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
//                emitter.onError(new Throwable("from Observable#subscribe#onError()"));
            }
        });

        /**
         * 3.创建被观察者和观察者之间的连接
         */
        observable.subscribe(observer);

        Observable<Integer> observable1 = Observable.just(1,2,3,4,5);
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

            }
        });

    }
}
