package com.danxx.mdplayer.utils;

import java.io.File;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by danxingxi
 * Observable创建工具类
 */
public class RxUtil {


    /**
     * make a operate change to observable
     * @param func
     * @param <T>
     * @return
     */
    public static  <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(func.call());
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    public static  <T> Observable<T> makeObservableWithError(final Callable<T> func,final Throwable throwable) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(func.call());
                    subscriber.onError(throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * rxjava递归查询内存中的视频文件
     * @param f
     * @return
     */
    public static Observable<File> listFiles(final File f){
        if(f.isDirectory()){
            return Observable.from(f.listFiles()).flatMap(new Func1<File, Observable<File>>() {
                @Override
                public Observable<File> call(File file) {
                    /**如果是文件夹就递归**/
                    return listFiles(file);
                }
            });
        } else {
            /**filter操作符过滤视频文件,是视频文件就通知观察者**/
            return Observable.just(f).filter(new Func1<File, Boolean>() {
                @Override
                public Boolean call(File file) {
                    return f.exists() && f.canRead() && FileUtils.isVideo(f);
                }
            });
        }
    }


}
