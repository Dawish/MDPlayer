package com.danxx.mdplayer.presenter;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.danxx.mdplayer.model.CacheManager;
import com.danxx.mdplayer.model.FileBean;
import com.danxx.mdplayer.model.VideoBean;
import com.danxx.mdplayer.mvp.BasePresenter;
import com.danxx.mdplayer.utils.FileUtils;
import com.danxx.mdplayer.view.IMVPView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

/**
 * Created by Dawish on 2016/6/19.
 */
public class VideoFilePresenter extends BasePresenter<IMVPView> {
    private static final String cacheKey = "fileBeansCacheData";
    private String cacheStr = "";
    private boolean inited = false;
    private Gson gson = new Gson();
    /**包含有视频文件夹集合**/
    private List<FileBean> fileBeans = new ArrayList<FileBean>();
    /**
     * 某一个文件夹中所有的视频文件
     **/
    private List<VideoBean> videoBeans = new ArrayList<VideoBean>();
    private List<File> files = new ArrayList<File>();
    /**
     * 获取所有包含视频的文件
     * @param isRefresh 是否为下拉刷新
     */
    public void getFileData(boolean isRefresh){
        fileBeans.clear();
        files.clear();
        cacheStr = CacheManager.getInstance().getAsString(cacheKey);
        if(isRefresh){
            cacheStr = null;
        }
        /**如果有缓存数据那就先显示缓存数据**/
        if(cacheStr != null && !TextUtils.isEmpty(cacheStr)){
            Gson gson = new Gson();
            // json转为带泛型的list
            List<FileBean> dataList = gson.fromJson(cacheStr,
                    new TypeToken<List<FileBean>>() {
                    }.getType());
            if(dataList.size()>0){
                if(VideoFilePresenter.this.getMvpView() != null){
                    VideoFilePresenter.this.getMvpView().getDataSuccess(dataList);
                    inited = true;
                    VideoFilePresenter.this.getMvpView().hideProgress();
                }
            }else{
                inited = false;
            }
        }else{
            inited = false;
            if(VideoFilePresenter.this.getMvpView() != null){
                VideoFilePresenter.this.getMvpView().showProgress();
            }
        }
        File rootFile = Environment.getExternalStorageDirectory();
        if(rootFile != null){
            this.addSubscriberToCompositeSubscription(
                Observable.just(rootFile)
                    .flatMap(new Func1<File, Observable<File>>() {
                        @Override
                        public Observable<File> call(File file) {
                            return listFiles(file);
                        }
                    })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<File>() {
                            @Override
                            public void onCompleted() {
                                /**对所有的视频文件进行分组操作**/
                                groupByFiles(files);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if(VideoFilePresenter.this.getMvpView() != null){
                                    VideoFilePresenter.this.getMvpView().getDataError(e);
                                }
                            }

                            @Override
                            public void onNext(File file) {
                                /**将遍历得到的所有视频文件存放在list中**/
                                files.add(file);
//                                addFile(file);
                            }
                        }
                )
            );
        }
    }

    /**
     * 对所有的视频文件进行分组操作
     * 统计一个文件夹里面有多少个视频文件
     * 参考:http://blog.csdn.net/axuanqq/article/details/50739660
     * @param datas
     */
    private void groupByFiles(List<File> datas){
        this.addSubscriberToCompositeSubscription(
            Observable.from(datas).groupBy(new Func1<File, File>() {
                @Override
                public File call(File file) {
                    /**以视频文件的父文件夹路径进行分组**/
                    return file.getParentFile();
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<GroupedObservable<File, File>>() {
                @Override
                public void onCompleted() {
                    Log.d("danxx", "groupBy onCompleted");
                    if(!inited){
                        if(VideoFilePresenter.this.getMvpView() != null){
                            VideoFilePresenter.this.getMvpView().getDataSuccess(fileBeans);
                            VideoFilePresenter.this.getMvpView().hideProgress();
                            inited = true;
                        }
                        String cacheStr = gson.toJson(fileBeans);
                        if(!TextUtils.isEmpty(cacheStr)){
                            CacheManager.getInstance().remove(cacheKey);
                            CacheManager.getInstance().put(cacheKey, cacheStr);
                        }
                    }else{
                        String cacheStr = gson.toJson(fileBeans);
                        if(!TextUtils.isEmpty(cacheStr)){
                            CacheManager.getInstance().remove(cacheKey);
                            CacheManager.getInstance().put(cacheKey ,cacheStr);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if(VideoFilePresenter.this.getMvpView() != null){
                        VideoFilePresenter.this.getMvpView().getDataError(e);
                        VideoFilePresenter.this.getMvpView().hideProgress();
                    }
                }

                @Override
                public void onNext(final GroupedObservable<File, File> fileGroupedObservable) {
                    fileGroupedObservable.subscribe(new Subscriber<File>() {
                        int count = 0;
                        String parentName;
                        @Override
                        public void onCompleted() {
                            fileBeans.add(new FileBean(String.valueOf(fileGroupedObservable.getKey()), parentName, count));
                            count = 0;
                            parentName = null;
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(File file) {
                            count++;
                            parentName = file.getParentFile().getName();
                        }
                    });
                }
            })
        );
    }

    int count = 0;
    File parentFile = Environment.getExternalStorageDirectory();
    File tempFile;

    /**
     * 使用Rxjava递归遍历文件目录查找视频文件，返回的视频文件无法区分目录，
     * 尝试使用groupby操作符对结果进行分组但是达不到预想的效果，这里只能靠
     * file.getParentFile的方式开判断一个文件夹里的视频文件数量,
     *
     * 此方法最后没有采用，可行，但是显得太笨了。
     *
     * 最后的办法就是把遍历得到的结果存在一个list里面，之后再对list进行groupby操作，
     * 应该会有更加优雅地方式来完成统计包含有视频的文件夹以及文件夹里面的视频文件数量
     * 的方法，RXjava的操作符很强大，组合使用有无限可能。
     * 只能怪自己学艺不精啊！！！
     *
     * @param file
     */
    private void addFile(File file){
      if (file.getParentFile().equals(parentFile)){
          count++;
          tempFile = parentFile;
      }else{
          if(count == 1){
              fileBeans.add(new FileBean(parentFile.getPath(),parentFile.getName(),count));
              count=0;
          }
          if(count>1){
              fileBeans.add(new FileBean(tempFile.getPath(),tempFile.getName(),count));
              count=0;
          }
          count++;
          parentFile = file.getParentFile();
      }
    }
    /**
     * 获取某一个文件夹中的视频文件
     * @param directory
     */
    public void getVideoData(File directory){
        if(VideoFilePresenter.this.getMvpView() != null){
            VideoFilePresenter.this.getMvpView().showProgress();
        }
        if( directory != null && directory.isDirectory()){
            this.addSubscriberToCompositeSubscription(
                    Observable.from(directory.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.exists() && f.canRead() && FileUtils.isVideo(f);
                        }
                    })).subscribe(
                            new Subscriber<File>() {
                                @Override
                                public void onCompleted() {
                                    if(VideoFilePresenter.this.getMvpView() != null){
                                        VideoFilePresenter.this.getMvpView().getDataSuccess(videoBeans);
                                        VideoFilePresenter.this.getMvpView().hideProgress();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if(VideoFilePresenter.this.getMvpView() != null){
                                        VideoFilePresenter.this.getMvpView().getDataError(e);
                                        VideoFilePresenter.this.getMvpView().hideProgress();
                                    }
                                }

                                @Override
                                public void onNext(File file) {
                                    String name = file.getName();
                                    String size = FileUtils.showFileSize(file.length());
                                    String path = file.getPath();
                                    videoBeans.add(new VideoBean(name, path, size));
                                }
                            }
                    )
            );
        }
    }
    /**
     * rxjava递归查询内存中的视频文件
     * 在此方法中应该可以做groupby操作，这样能简化操作，日后再研究
     * @param f
     * @return
     */
    public Observable<File> listFiles(final File f){
        if(f.isDirectory()){
            return Observable.from(f.listFiles())
                .flatMap(new Func1<File, Observable<File>>() {
                @Override
                public Observable<File> call(File file) {
                    /**如果是文件夹就递归**/
                    return listFiles(file);
                }
            });
        } else {
            /**filter操作符过滤视频文件,是视频文件就通知观察者**/
            return Observable.just(f)
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        return f.exists() && f.canRead() && FileUtils.isVideo(f);
                }
            });
        }
    }
}
