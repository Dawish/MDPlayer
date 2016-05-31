package com.danxx.mdplayer.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danxingxi on 2016/3/31.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder>{
    /**header view type**/
    public static final int TYPE_HEADER = 0;
    /**item view type**/
    public static final int TYPE_NORMAL = 1;
    private View mHeaderView;
    private OnItemClickListener itemClickListener;

    private List< T > mData = new ArrayList< T >();

    public void setData(List< T > data){
        mData.clear();
        mData.addAll(data);
    }


    public T getItemData(int position) {
        T res = null;

        if(position < mData.size()) {
            res = mData.get(position);
        }

        return res;
    }

    public void clearData(){
        if(mData != null){
            mData.clear();
        }
    }

    public void setOnItemClickListener(OnItemClickListener li) {
        itemClickListener = li;
    }

    /**
     * add header view
     * @param headerView
     */
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    /**
     *  get header view
     * @return
     */
    public View getHeaderView() {
        return mHeaderView;
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) return TYPE_NORMAL;
        if(position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    /**
     * 获得item的位置
     * @param holder
     * @return
     */
    public int getRealPosition(BaseRecyclerViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER) return new HeaderViewHolder(mHeaderView);
        return createItem(parent ,viewType);
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder holder, int position) {
        /**如果是header view就直接返回，不需要绑定数据**/
        if(getItemViewType(position) == TYPE_HEADER) return;
        final int pos = getRealPosition(holder);
        final T data = mData.get(pos);
        bindData(holder ,position);

        if(itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(pos, data);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mData.size() : mData.size() + 1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(BaseRecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if(lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    /**
     * 创建item view
     * @param parent
     * @param viewType
     * @return
     */
    protected  abstract BaseRecyclerViewHolder createItem(ViewGroup parent, int viewType);

    /**
     * 绑定数据
     * @param holder
     * @param position
     */
    protected abstract void bindData(BaseRecyclerViewHolder holder, int position);

    /**
     *header view ViewHolder
     */
    class HeaderViewHolder extends BaseRecyclerViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected View getView() {
            return null;
        }
    }

    /**
     * item 点击事件接口
     * @param <T>
     */
    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data);
    }
}

