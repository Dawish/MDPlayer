package com.danxx.mdplayer.meizhi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.danxx.mdplayer.R;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Rxjava基础训练
 */
public class RxTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_test);
        rxTest();
    }

    private void rxTest(){
        /***************************************正常步骤******************************************/
        /**消息源，被观察着**/
        Observable <String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext("Hello world!!");
                        subscriber.onCompleted();
                    }
                }
        );
        /**接受处，观察者**/
        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.d("danxx" ,"onNext--->"+s);
            }
        };
        /**注册观察者**/
        myObservable.subscribe(mySubscriber);

        /***************************************简化步骤******************************************/
        /**Integer类型参数**/
        Observable.just(6).subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d("danxx" ,"Integer--->"+integer);
                    }
                }
        );
        /**String类型参数**/
        Observable.just("String args").subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("danxx" ,"String--->"+s);
                    }
                }
        );

        /***************************************map操作******************************************/
        /**map感觉就是一个中间加工厂，可以把结果做处理，减少观察者的处理步骤**/
        Observable.just("I am ->")
        .map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                /**map处理结果**/
                return s+"map";
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d("danxx" ,"map 处理后的结果 String--->"+s);
            }
        });
    }
}
