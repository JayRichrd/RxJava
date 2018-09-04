package com.tencent.cain.rxjava;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        demo();
//        schedulerDemo();
        operation();
    }

    /**
     * 操作符练习
     */
    private static void operation() {
        // map操作符
        Observable.just(1, 2, 3).map(new Function<Integer, String>() {
            public String apply(Integer integer) throws Exception {
                return ++integer + "";
            }
        }).subscribe(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("accept s: " + s);
            }
        });

        List<SimulationData> datas = creatData();
        Observable.fromIterable(datas).flatMap(new Function<SimulationData, ObservableSource<Integer>>() {
            public ObservableSource<Integer> apply(SimulationData simulationData) throws Exception {
                return Observable.fromIterable(simulationData.getList());
            }
        }).subscribe(new Consumer<Integer>() {
            public void accept(Integer integer) throws Exception {
                System.out.println("flatmap accept integer: " + integer);
            }
        });

        Integer[] array = new Integer[]{1,2,3};
        Observable.fromArray(array).flatMap(new Function<Integer, ObservableSource<Integer>>() {
            public ObservableSource<Integer> apply(Integer integer) throws Exception {
                return Observable.just(integer+1);
            }
        }).filter(new Predicate<Integer>() {
            public boolean test(Integer integer) throws Exception {
                return integer > 2;
            }
        }).take(1).subscribe(new Consumer<Integer>() {
            public void accept(Integer integer) throws Exception {
                System.out.println("收到的数字为：" + integer);
            }
        });
    }

    private static List<SimulationData> creatData() {
        List<SimulationData> datas = new ArrayList<SimulationData>();
        for (int i = 0; i < 3; i++) {
            SimulationData simulationData = new SimulationData();
            List<Integer> list = new ArrayList<Integer>();
            for (int j = 0; j < 5; j++) {
                list.add(i * j);
            }
            simulationData.setIndex(i);
            simulationData.setList(list);
            datas.add(simulationData);
        }
        return datas;
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
