package com.danxx.mdplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.model.VideoListBean;
import com.danxx.mdplayer.ui.RecyclerVideoViewActivity;
import com.danxx.mdplayer.utils.DeviceUtils;

/**
 * Created by Dawish on 2016/10/3.
 */

public class VideoViewAdapater extends BaseRecyclerViewAdapter<VideoListBean>{

    Context mContext;

    public VideoViewAdapater(Context context){
        this.mContext = context;
    }

    @Override
    protected BaseRecyclerViewHolder createItem(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_video_layout, null);
        VideoViewHolder videoViewHolder = new VideoViewHolder(view);
        return videoViewHolder;
    }

    @Override
    protected void bindData(BaseRecyclerViewHolder holder, int position) {
        ((VideoViewHolder)holder).update(position);
    }


    class VideoViewHolder extends BaseRecyclerViewHolder{
        /**
         * 点击开始播放的控件，里面就是一张图片
         */
        public RelativeLayout playerControlLayout;
        /**
         * 播放器的容器
         */
        public RelativeLayout playerLayout;

        public VideoViewHolder(View itemView) {
            super(itemView);
            playerControlLayout = (RelativeLayout) itemView.findViewById(R.id.adapter_player_control);
            playerLayout = (RelativeLayout) itemView.findViewById(R.id.adapter_super_video_layout);
            if (playerLayout!=null){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playerLayout.getLayoutParams();
                layoutParams.height = (int) (DeviceUtils.deviceWidth(mContext) * 0.5652f);//这值是网上抄来的，我设置了这个之后就没有全屏回来拉伸的效果，具体为什么我也不太清楚
                playerLayout.setLayoutParams(layoutParams);
            }
        }

        @Override
        protected View getView() {
            return null;
        }

        public void update(final int position) {
            //点击回调 播放视频
            playerControlLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playclick != null)
                        playclick.onPlayclick(position, playerControlLayout);
                }
            });
        }

    }

    private onPlayClick playclick;

    public void setPlayClick(onPlayClick playclick) {
        this.playclick = playclick;
    }

    public interface onPlayClick {
        void onPlayclick(int position, RelativeLayout image);
    }

}
