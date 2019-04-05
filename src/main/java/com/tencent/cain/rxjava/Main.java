package com.tencent.cain.rxjava;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.*;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
//        demo();
//        schedulerDemo();
//        doAction();
//        coldOrHotObservable();
//        singleAction();
//        maybeAction();
//        subjectAsy();
//        behaviorSubject();
//        replaySubject();
//        publishSubject();
//        creatFun();
//        repeatWhenFun();
//        repeatUntilFun();
//        deferFun();
        intervalFun();
//        fromFun();
//        operation();
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

        Integer[] array = new Integer[]{1, 2, 3};
        Observable.fromArray(array).flatMap(new Function<Integer, ObservableSource<Integer>>() {
            public ObservableSource<Integer> apply(Integer integer) throws Exception {
                return Observable.just(integer + 1);
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

    static void doAction() {
        Observable.just("Hello World").doOnNext(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("doOnNext: " + s);
            }
        }).doAfterNext(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("doAfterNext: " + s);
            }
        }).doOnComplete(new Action() {
            public void run() throws Exception {
                System.out.println("doOnComplete");
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            public void accept(Disposable disposable) throws Exception {
                System.out.println("doOnSubscribe: " + disposable.getClass().getSimpleName() + ", isDiapose: " + disposable.isDisposed());
            }
        }).doAfterTerminate(new Action() {
            public void run() throws Exception {
                System.out.println("doAfterTerminate");
            }
        }).doFinally(new Action() {
            public void run() throws Exception {
                System.out.println("doFinally");
            }
        }).doOnEach(new Consumer<Notification<String>>() {
            public void accept(Notification<String> stringNotification) throws Exception {
                System.out.println("doOnEach: " + stringNotification.toString());
            }
        }).doOnLifecycle(new Consumer<Disposable>() {
            public void accept(Disposable disposable) throws Exception {
                System.out.println("doOnLifecycle#accept: " + disposable.getClass().getSimpleName() + "is dispose: " + disposable.isDisposed());
            }
        }, new Action() {
            public void run() throws Exception {
                System.out.println("doOnLifecycle#run");
            }
        }).subscribe(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("收到的数据：" + s);
            }
        });
    }

    static void coldOrHotObservable() {
        Consumer<Long> consumer1 = new Consumer<Long>() {
            public void accept(Long aLong) throws Exception {
                System.out.println("consumer1: " + aLong);
            }
        };
        Consumer<Long> consumer2 = new Consumer<Long>() {
            public void accept(Long aLong) throws Exception {
                System.out.println("    consumer2: " + aLong);
            }
        };
        Consumer<Long> consumer3 = new Consumer<Long>() {
            public void accept(Long aLong) throws Exception {
                System.out.println("        consumer3: " + aLong);
            }
        };

        ConnectableObservable<Long> connectableObservable = Observable.create(new ObservableOnSubscribe<Long>() {
            public void subscribe(final ObservableEmitter<Long> emitter) throws Exception {
                Observable.interval(10, TimeUnit.MICROSECONDS, Schedulers.computation()).take(Integer.MAX_VALUE).subscribe(new Consumer<Long>() {
                    public void accept(Long aLong) throws Exception {
                        emitter.onNext(aLong);
                    }
                });
            }
        }).observeOn(Schedulers.newThread()).publish();
        connectableObservable.connect();
        Observable<Long> observable = connectableObservable.refCount();
        Disposable disposable1 = observable.subscribe(consumer1);
        Disposable disposable2 = observable.subscribe(consumer2);

//        Observable<Long> observable = Observable.create(new ObservableOnSubscribe<Long>() {
//            public void subscribe(final ObservableEmitter<Long> emitter) throws Exception {
//                Observable.interval(10, TimeUnit.MICROSECONDS, Schedulers.computation()).take(Integer.MAX_VALUE).subscribe(new Consumer<Long>() {
//                    public void accept(Long aLong) throws Exception {
//                        emitter.onNext(aLong);
//                    }
//                });
//            }
//        }).observeOn(Schedulers.newThread());
//        PublishSubject<Long> subject = PublishSubject.create();
//        observable.subscribe(subject);
//        subject.subscribe(consumer1);
//        subject.subscribe(consumer2);
        try {
            Thread.sleep(20L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        disposable1.dispose();
        disposable2.dispose();
    }

    static void singleAction() {
        Single.create(new SingleOnSubscribe<String>() {
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                emitter.onSuccess("single test.");
            }
        }).subscribe(new BiConsumer<String, Throwable>() {
            public void accept(String s, Throwable throwable) throws Exception {
                System.out.println("receive: " + s);
            }
        });
    }

    static void maybeAction() {
        Maybe.create(new MaybeOnSubscribe<String>() {
            public void subscribe(MaybeEmitter<String> emitter) throws Exception {
                emitter.onComplete();
                emitter.onSuccess("Maybe test.");
                emitter.onSuccess("Maybe test.");
            }
        }).subscribe(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("s: " + s);
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) throws Exception {
                System.out.println(throwable.getMessage());
            }
        }, new Action() {
            public void run() throws Exception {
                System.out.println("---run()---");
            }
        });
    }

    static void subjectAsy() {
        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        asyncSubject.onNext("asyncSubject1");
        asyncSubject.onNext("asyncSubject2");
        asyncSubject.subscribe(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("s :" + s);
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) throws Exception {
                System.out.println(throwable.getMessage());
            }
        }, new Action() {
            public void run() throws Exception {
                System.out.println("---run()---");
            }
        });
        asyncSubject.onNext("asyncSubject3");
        asyncSubject.onComplete();
    }

    static void behaviorSubject() {
        BehaviorSubject<String> behaviorSubject = BehaviorSubject.createDefault("behaviorSubjectDefault");
        behaviorSubject.onNext("behaviorSubject1");
        behaviorSubject.subscribe(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("s: " + s);
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) throws Exception {
                System.out.println(throwable.getMessage());
            }
        }, new Action() {
            public void run() throws Exception {
                System.out.println("---run()---");
            }
        });
        behaviorSubject.onNext("behaviorSubject2");
    }

    static void replaySubject() {
        ReplaySubject<String> replaySubject = ReplaySubject.createWithSize(1);
        replaySubject.onNext("replaySubject1");
        replaySubject.onNext("replaySubject2");
        replaySubject.subscribe(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("s: " + s);
            }
        });
        replaySubject.onNext("replaySubject3");
        replaySubject.onNext("replaySubject4");
    }

    static void publishSubject() {
        PublishSubject<String> publishSubject = PublishSubject.create();
        publishSubject.onNext("publishSubject1");
        publishSubject.onNext("publishSubject2");
        publishSubject.subscribe(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("s: " + s);
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) throws Exception {
                System.out.println(throwable.getMessage());
            }
        }, new Action() {
            public void run() throws Exception {
                System.out.println("---run()---");
            }
        });
//        publishSubject.onComplete();
        publishSubject.onNext("publishSubject3");
    }

    static void creatFun() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                if (emitter.isDisposed()) {
                    return;
                }
                for (int i = 0; i < 10; i++) {
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }
        }).repeat(3).subscribe(new Consumer<Integer>() {
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) throws Exception {
                System.out.println(throwable.getMessage());
            }
        }, new Action() {
            public void run() throws Exception {
                System.out.println("---run---");
            }
        });
    }

    static void fromFun() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> future = executorService.submit(new Callable<String>() {
            public String call() throws Exception {
                System.out.println("模拟一些耗时任务.");
                Thread.sleep(5000);
                return "Ok";
            }
        });
        Observable.fromFuture(future, 4, TimeUnit.SECONDS).subscribe(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("s: " + s);
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) throws Exception {
                System.out.println("error: " + throwable.getMessage());
            }
        });
    }

    static void repeatWhenFun() {
        Observable.range(0, 9).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                return Observable.timer(10, TimeUnit.SECONDS);
            }
        }).subscribe(new Consumer<Integer>() {
            public void accept(Integer integer) throws Exception {
                System.out.println("integer: " + integer);
            }
        });

        try {
            Thread.sleep(12 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void repeatUntilFun() {
        final long startTime = System.currentTimeMillis();
        Observable.interval(100, TimeUnit.MILLISECONDS).take(5).repeatUntil(new BooleanSupplier() {
            public boolean getAsBoolean() throws Exception {
                long duration = System.currentTimeMillis() - startTime;
                System.out.println("duration: " + duration);
                return duration > 5000;
            }
        }).subscribe(new Consumer<Long>() {
            public void accept(Long aLong) throws Exception {
                System.out.println("aLong: " + aLong);
            }
        });

        try {
            Thread.sleep(6 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void deferFun() {
        Observable<String> observable = Observable.defer(new Callable<ObservableSource<? extends String>>() {
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just("Hello defer");
            }
        });

        observable.subscribe(new Consumer<String>() {
            public void accept(String s) throws Exception {
                System.out.println("s: " + s);
            }
        });
    }

    static void intervalFun() {
        Observable.interval(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            public void accept(Long aLong) throws Exception {
                System.out.println("aLong: " + aLong);
            }
        });

        Observable.timer(2,TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            public void accept(Long aLong) throws Exception {
                System.out.println("timer: " + aLong);
            }
        });

        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
