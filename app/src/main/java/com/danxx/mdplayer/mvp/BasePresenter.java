package com.danxx.mdplayer.mvp;

import android.util.Log;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Description：BasePresenter
 * <p>
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 * <p>
 */
public class BasePresenter<T extends MvpView> implements Presenter<T> {

    private T mMvpView;
    /**CompositeSubscription来持有所有的Subscriptions，然后在onDestroy()或者onDestroyView()里取消所有的订阅。**/
    public CompositeSubscription mCompositeSubscription;

    /**
     *
     * @param mvpView  把Activity或者Fragment的引用复制过来，方便在调用View接口做回调
     */
    @Override
    public void attachView(T mvpView) {
        Log.d("danxx","attachView");
        this.mMvpView = mvpView;
        /****/
        this.mCompositeSubscription = new CompositeSubscription();
    }


    @Override
    public void detachView() {
        Log.d("danxx","detachView");
        this.mMvpView = null;
        /**取消Subscriber事件订阅**/
        this.mCompositeSubscription.unsubscribe();
        this.mCompositeSubscription = null;
    }

    /**
     * 添加subscriber订阅者到mCompositeSubscription，
     * 方便在detachView的时候取消订阅，防止内存泄露
     * @param subscriber
     */
    public void addSubscriberToCompositeSubscription(Subscription subscriber){
        if(subscriber != null){
            this.mCompositeSubscription.add(subscriber);
        }
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }


    public T getMvpView() {
        return mMvpView;
    }


    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }


    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
