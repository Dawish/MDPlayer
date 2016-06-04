package com.danxx.mdplayer.otto;

/**
 * Created by Danxx on 2016/6/1.
 * EventBus消息类
 */
public class MyEvent {

    /**
     * 消息类型
     * 参见: { @link com.danxx.mdplayer.application.Common }
     */
    public int eventType;

    public MyEvent(int type) {
        this.eventType =type;
    }

}
