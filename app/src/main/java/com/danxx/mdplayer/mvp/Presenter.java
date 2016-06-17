package com.danxx.mdplayer.mvp;

/**
 * Description：Presenter
 * <p>
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 * <p>
 */
public interface Presenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
