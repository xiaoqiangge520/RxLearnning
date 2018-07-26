package tanxing.reds.com.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * rxjava学习
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //线程切换
        ThreadChange();
    }

    /**
     * SubscribeOn这个操作符，决定上游事件操作所处的线程
     * 与调用的位置无关，而且多次调用只有第一次调用时会指定Observable自己在哪个调度器执行。
     * 若线程切换只设置SubscribeOn,则决定上下游事件处理的整个线程,例如设置subscribeOn(Schedulers.newThread())则上下游均在新线程中执行
     * SubscribeOn不仅可以指定Observable自身的调度器，也可以指定DoOnSubscribe执行的调度器。
     * <p>
     * ObserveOn这个操作符，决定下游事件操作所处的线程
     * ObserveOn多次调用只有最后一次有效
     * 若线程切换只设置ObserveOn,则上游事件默认是在主线程执行,ObserveOn决定下游事件所处的线程
     */
    private void ThreadChange() {
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
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

}