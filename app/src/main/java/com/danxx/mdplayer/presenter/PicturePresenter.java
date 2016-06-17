package com.danxx.mdplayer.presenter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.mvp.BasePresenter;
import com.danxx.mdplayer.utils.DeviceUtils;
import com.danxx.mdplayer.view.PictureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Danxx on 2016/6/17.
 * 妹纸Presenter 妹纸分类和妹纸列表
 */
public class PicturePresenter extends BasePresenter<PictureView> {
    /**
     * 保存图片到本地
     */
    public void savePicture(@NonNull final Bitmap bitmapDrawable, @NonNull final Context context, @NonNull final Application application){
        this.mCompositeSubscription.add(this.getSavePictureObservable(bitmapDrawable, context, application)
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        PicturePresenter.this.mCompositeSubscription.remove(this);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(PicturePresenter.this.getMvpView() != null){
                            PicturePresenter.this.getMvpView().onSaveError(e);
                            PicturePresenter.this.mCompositeSubscription.remove(this);
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("danxx" ,"onNext");
                        if(s != null && PicturePresenter.this.getMvpView() != null){
                            PicturePresenter.this.getMvpView().onSaveSuccess(s);
                        }
                    }
                }));
    }

    /**
     * @param bitmapDrawable
     * @param context
     * @param application
     * @return
     */
    private Observable<String> getSavePictureObservable(
            @NonNull final Bitmap bitmapDrawable,
            @NonNull final Context context, @NonNull final Application application) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String dirPath = DeviceUtils.createAPPFolder(
                            context.getString(R.string.app_name), application);
                    File downloadFile = new File(new File(dirPath),
                            UUID.randomUUID().toString().replace("-", "") + ".jpg");
                    if (!downloadFile.exists()) {
                        File parent = downloadFile.getParentFile();
                        if (parent != null && !parent.exists()) parent.mkdirs();
                    }
                    FileOutputStream output = new FileOutputStream(downloadFile);
                    /****/
                    bitmapDrawable.compress(Bitmap.CompressFormat.JPEG, 100, output);
                    output.close();

                    // 更新相册
                    Uri uri = Uri.fromFile(downloadFile);
                    Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                    context.sendBroadcast(scannerIntent);
                    subscriber.onNext(downloadFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());
    }
}
