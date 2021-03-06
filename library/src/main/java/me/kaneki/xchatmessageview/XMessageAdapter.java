package me.kaneki.xchatmessageview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.kaneki.xchatmessageview.anno.XItemLayoutResResolver;
import me.kaneki.xchatmessageview.holder.XViewHolder;

import java.util.List;

/**
 * @author yueqian
 * @Desctription
 * @date 2017/1/16
 * @email yueqian@mogujie.com
 */
public abstract class XMessageAdapter<T> extends RecyclerView.Adapter<XViewHolder<T>> {

    public static final int TYPE_LOADING_HEADER = 1000;
    public static final int TYPE_LOADING_FOOTER = 1001;

    private LayoutInflater layoutInflater;

    private List<T> mDatas;
    private int[] mIds;
    private int headerLayoutId;
    private int footerLayoutId;
    private boolean isNeedHeaderLoadMore;
    private boolean isNeedFooterLoadMore;

    public XMessageAdapter (Context context, List<T> mDatas) {
        this.mDatas = mDatas;
        this.mIds = XItemLayoutResResolver.resolve(this);
        this.layoutInflater = LayoutInflater.from(context);
        this.headerLayoutId = me.kaneki.xchatmessageview.R.layout.x_default_load;
        this.footerLayoutId = me.kaneki.xchatmessageview.R.layout.x_default_load;
        this.isNeedHeaderLoadMore = false;
        this.isNeedFooterLoadMore = false;
    }

    public abstract int getItemViewType(T t);

    public abstract XViewHolder<T> getViewHolder(View itemView, int viewType);

    public void setHeaderLayoutId(int headerLayoutId) {
        this.headerLayoutId = headerLayoutId;
    }

    public void setFooterLayoutId(int footerLayoutId) {
        this.footerLayoutId = footerLayoutId;
    }

    boolean isNeedHeaderLoadMore() {
        return isNeedHeaderLoadMore;
    }

    void setNeedHeaderLoadMore(boolean needHeaderLoadMore) {
        isNeedHeaderLoadMore = needHeaderLoadMore;
    }

    boolean isNeedFooterLoadMore() {
        return isNeedFooterLoadMore;
    }

    void setNeedFooterLoadMore(boolean needFooterLoadMore) {
        isNeedFooterLoadMore = needFooterLoadMore;
    }

    void addMessageAtLast(T t) {
        mDatas.add(t);
        notifyItemInserted(getItemCount());
    }

    void addMoreMessageAtLast(List<T> tList) {
        mDatas.addAll(tList);
        notifyItemInserted(getItemCount());
    }

    void addMoreMessageAtFirst(List<T> tList) {
        mDatas.addAll(0, tList);
        notifyItemRangeInserted(isNeedHeaderLoadMore ? 1 : 0, tList.size());
        notifyItemRangeChanged(tList.size() + (isNeedHeaderLoadMore ? 1 : 0), getItemCount() - tList.size());
    }

    void removeMessageAtPosition(int pos) {
        int realIndex = isNeedHeaderLoadMore ? pos - 1 : pos;
        mDatas.remove(realIndex);
        notifyItemRemoved(pos);
        // 加入如下代码保证position的位置正确性
        if (realIndex != getItemCount() - 1) {
            notifyItemRangeChanged(realIndex, getItemCount() - realIndex);
        }
    }

    void removeAllMessage() {
        isNeedFooterLoadMore = false;
        isNeedHeaderLoadMore = false;
        mDatas.clear();
        notifyDataSetChanged();
    }

    void refreshMessageAtPos(int pos) {
        notifyItemChanged(pos);
    }

    void refreshAllMessage() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isNeedHeaderLoadMore && position == 0)
            return TYPE_LOADING_HEADER;
        else if (isNeedFooterLoadMore && position == getItemCount() - 1)
            return TYPE_LOADING_FOOTER;
        else if (isNeedHeaderLoadMore)
            return getItemViewType(mDatas.get(position - 1));
        else
            return getItemViewType(mDatas.get(position));
    }

    @Override
    @SuppressWarnings("unchecked")
    public XViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        return getViewHolder((viewType == TYPE_LOADING_FOOTER || viewType == TYPE_LOADING_HEADER) ? layoutInflater.inflate(viewType == TYPE_LOADING_FOOTER ? footerLayoutId : headerLayoutId, parent, false) : layoutInflater.inflate(mIds[viewType], parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(XViewHolder<T> holder, int position) {
        if (isNeedHeaderLoadMore && position == 0)
            holder.bindView(null);
        else if (isNeedFooterLoadMore && position == getItemCount() - 1)
            holder.bindView(null);
        else if (isNeedHeaderLoadMore)
            holder.bindView(mDatas.get(position - 1));
        else
            holder.bindView(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        if (isNeedFooterLoadMore && isNeedHeaderLoadMore)
            return mDatas.size() + 2;
        else if (isNeedHeaderLoadMore || isNeedFooterLoadMore)
            return mDatas.size() + 1;
        else
            return mDatas.size();
    }
}
