# Rxjava学习例子

-------------------


## RxJava调度线程的切换

**SubscribeOn操作符**
>决定上游事件操作所处的线程与调用的位置无关，而且多次调用只有第一次调用时会指定Observable自己在哪个调度器执行。  
若线程切换只设置SubscribeOn,则决定上下游事件处理的整个线程,例如设置subscribeOn(Schedulers.newThread())则上下游均在新线程中执行(详情见Demo)
同时,SubscribeOn不仅可以指定Observable自身的调度器，也可以指定DoOnSubscribe执行的调度器。

**ObserveOn操作符**
>决定下游事件操作所处的线程
,ObserveOn多次调用只有最后一次有效.
若线程切换只设置ObserveOn,则上游事件默认是在主线程执行,ObserveOn决定下游事件所处的线程

``` java
        Observable.just("1")
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        Log.e("TAG", "call: thread" + Thread.currentThread().getName());
                        return null;
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.e("TAG", "call: thread" + Thread.currentThread().getName());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Log.e("TAG", "onNext: thread" + Thread.currentThread().getName());
                    }
                });
    }
```
>注意: 当调用订阅操作（即调用Observable.subscribe()方法）的时候，被观察者才真正开始发出事件。

## RxJava原理解析
相关文章 
*https://gank.io/post/560e15be2dca930e00da1083 (必看)*
*https://www.jianshu.com/p/e61e1307e538*

## RxJava调用顺序


