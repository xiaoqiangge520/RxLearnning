package tanxing.reds.com.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * rxjava学习
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = new TextView(this);
        textView.setOnClickListener(this);

        //线程切换
//        ThreadChange();

        //操作符
//        rxFlatmap();

        //lift调用原理
//        rxLift();

        //背压
        rxBackPressure();
    }

    private void ThreadChange() {
        Observable.just("1", "2", "3")
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        //要在指定的线程来做准备工作，可以使用 doOnSubscribe() 方法
                        Log.e("TAG", "doOnSubscribe 做准备工作 doOnSubscribe: thread" + Thread.currentThread().getName());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        Log.e("TAG", "map Func1 call: thread" + Thread.currentThread().getName() + ",s=" + s);
                        return Integer.valueOf(s);
                    }
                })
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        //只能在subscriber所在的线程工作
                        Log.e("TAG", "Subscriber 做准备工作 onStart: thread" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(Integer i) {
                        Log.e("TAG", "Subscriber onNext: thread" + Thread.currentThread().getName() + ",i=" + i);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                });
    }

    public void rxFlatmap() {

        Student 小米 = new Student("小米");
        ArrayList<Course> courses1 = new ArrayList<>();
        courses1.add(new Course("小米1"));
        courses1.add(new Course("小米2"));
        courses1.add(new Course("小米3"));
        courses1.add(new Course("小米4"));
        courses1.add(new Course("小米5"));
        小米.mCourseList = courses1;

        Student 华为 = new Student("华为");
        ArrayList<Course> courses2 = new ArrayList<>();
        courses2.add(new Course("华为1"));
        courses2.add(new Course("华为2"));
        courses2.add(new Course("华为3"));
        华为.mCourseList = courses2;

        Student[] students = {小米, 华为};
        Subscriber<Course> subscriber = new Subscriber<Course>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Course course) {
                Log.d("TAG", "Subscriber onNext" + course.courseName);
            }
        };

        Observable.from(students)
                .flatMap(new Func1<Student, Observable<Course>>() {
                    @Override
                    public Observable<Course> call(Student student) {
                        Log.e("TAG", "Subscriber onNext" + student.name + ",size=" + student.mCourseList);
                        return Observable.from(student.mCourseList);
                    }
                })
                .subscribe(subscriber);
    }

    private void rxLift() {
        Observable.just("1", "2", "3")
                .compose(new Observable.Transformer<String, String>() { //对原始的Observable进行加工处理,输出自己需要的Observable
                    @Override
                    public Observable<String> call(Observable<String> stringObservable) {
                        return stringObservable.filter(new Func1<String, Boolean>() {//对原始的Observable进行加工处理,输出自己需要的Observable
                            @Override
                            public Boolean call(String s) {
                                return !s.equals("2");
                            }
                        });
                    }
                })
                .lift(new Observable.Operator<String, String>() {

                    @Override
                    public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {//原来的subscriber
                        return new Subscriber<String>() {//代理的subscriber
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();//原来的subscriber执行操作
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(String s) {
                                //func,action操作
                                s += s + "000";
                                Log.e("TAG", "代理的Subscriber onNext: thread" + Thread.currentThread().getName());
                                subscriber.onNext(s);
                            }
                        };
                    }
                })
                .subscribe(new Subscriber<String>() { //原来的subscriber

                    @Override
                    public void onStart() {
                        super.onStart();
                        //只能在subscriber所在的线程工作
                        Log.e("TAG", "Subscriber 做准备工作 onStart: thread" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(String i) {
                        Log.e("TAG", "Subscriber onNext: thread" + Thread.currentThread().getName() + ",i=" + i);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                });
    }

    private void rxBackPressure() {
        //被观察者将产生100000个事件
        Observable observable = Observable.range(1, 100000);


        observable.observeOn(Schedulers.newThread())
                .subscribe(new MySubscriber());
    }

    class MySubscriber extends Subscriber<Object> {
        @Override
        public void onStart() {
            //一定要在onStart中通知被观察者先发送一个事件
            request(1);
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(Object n) {
            //处理完毕之后，在通知被观察者发送下一个事件
            request(1);
        }
    }

    class Student {
        String name;

        public Student(String name) {
            this.name = name;
        }

        List<Course> mCourseList;
    }

    class Course {
        String courseName;

        public Course(String courseName) {
            this.courseName = courseName;
        }
    }

    @Override
    public void onClick(View v) {

    }
}
