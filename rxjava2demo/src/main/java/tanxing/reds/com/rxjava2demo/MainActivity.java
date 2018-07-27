package tanxing.reds.com.rxjava2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.observers.SubscriberCompletableObserver;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //背压
        rxBackPressure();

        //新的操作符和改变
        newRx();
    }


    private void rxBackPressure() {
        //常规方式支持背压
        Flowable.range(1, 10).subscribe(new Subscriber<Integer>() {
            public Subscription subScription;

            @Override
            public void onSubscribe(Subscription s) {

                //准备工作在此处

                subScription = s;
                subScription.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                subScription.request(1);
                Log.e("TAG", "onNext: " + integer);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });

        //create方式支持背压
        Flowable.create(new FlowableOnSubscribe<Object>() {

            @Override
            public void subscribe(FlowableEmitter<Object> emitter) throws Exception {

            }
        }, BackpressureStrategy.BUFFER)
                .subscribe(new Subscriber<Object>() {

            public Subscription subScription;

            @Override
            public void onSubscribe(Subscription s) {
                subScription = s;
                subScription.request(1);
            }

            @Override
            public void onNext(Object o) {

                //最终的工作在此处

                subScription.request(1);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void newRx() {

        /**
         * {@code Single} behaves the same as {@link Observable} except that it can only emit either a single successful
         * value, or an error (there is no "onComplete" notification as there is for {@link Observable})
         *
         * Like an {@link Observable}, a {@code Single} is lazy, can be either "hot" or "cold", synchronous or
         * asynchronous.
         */

        Single.create(new SingleOnSubscribe<Object>() {

            @Override
            public void subscribe(SingleEmitter<Object> emitter) throws Exception {

            }
        }).subscribe(new SingleObserver<Object>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {

            }
        }).subscribe(new SubscriberCompletableObserver<>(new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        }));

    }
}
