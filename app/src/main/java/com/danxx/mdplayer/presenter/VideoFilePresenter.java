package com.danxx.mdplayer.presenter;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.danxx.mdplayer.model.VideoBean;
import com.danxx.mdplayer.mvp.BasePresenter;
import com.danxx.mdplayer.utils.FileUtils;
import com.danxx.mdplayer.utils.RxUtil;
import com.danxx.mdplayer.view.IMVPView;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Dawish on 2016/6/19.
 */
public class VideoFilePresenter extends BasePresenter<IMVPView> {
    /**
     * 获取所有包含视频的文件
     */
    public void getFileData(){
        File rootFile = Environment.getExternalStorageDirectory();
        if(rootFile != null){
            Observable.just(rootFile)
                .flatMap(new Func1<File, Observable<File>>() {
                    @Override
                    public Observable<File> call(File file) {
                        return listFiles(file);
                    }
                })
                .subscribe(
                    new Subscriber<File>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(File file) {
                        }
                    }
                );
        }
    }

    /**
     * 获取某一个文件夹中的视频文件
     * @param directory
     */
    public void getVideoData(File directory){

    }
    /**
     * rxjava递归查询内存中的视频文件
     * @param f
     * @return
     */
    public Observable<File> listFiles(final File f){
        if(f.isDirectory()){
            return Observable.from(f.listFiles()).flatMap(new Func1<File, Observable<File>>() {
                @Override
                public Observable<File> call(File file) {
                    /**如果是文件夹就递归**/
                    return listFiles(file);
                }
            });
        } else {
            /**是视频文件就通知观察者**/
            if (f.exists() && f.canRead() && FileUtils.isVideo(f)) {
                return Observable.just(f);
            }else{
                /**非视频文件就返回null**/
                return null;
            }
        }
    }
}
