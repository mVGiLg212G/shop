package cn.cloudworkshop.shop.mvp.guestrecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.cloudworkshop.shop.R;
import cn.cloudworkshop.shop.base.BaseMvpActivity;
import cn.cloudworkshop.shop.bean.CustomerListBean;
import cn.cloudworkshop.shop.bean.GuestRecordBean;
import cn.cloudworkshop.shop.mvp.customerlist.CustomerListActivity;
import cn.cloudworkshop.shop.utils.ToastUtils;
import cn.cloudworkshop.shop.view.LoadingView;

/**
 * Author：Libin on 2018/11/29 16:06
 * Email：1993911441@qq.com
 * Describe：
 */
public class GuestRecordActivity extends BaseMvpActivity<GuestRecordContract.Presenter> implements GuestRecordContract.View {
    @BindView(R.id.rv_customer_list)
    RecyclerView rvRecord;
    @BindView(R.id.sfr_customer)
    SmartRefreshLayout sfrRecord;
    @BindView(R.id.loading_view)
    LoadingView loadingView;

    private int guestId;
    private int page = 1;
    //0:init, 1: refresh, 2:load
    private int type;

    private List<GuestRecordBean.DataBean> dataList = new ArrayList<>();
    private CommonAdapter<GuestRecordBean.DataBean> adapter;

    @Override
    protected GuestRecordContract.Presenter initPresenter() {
        return new GuestRecordPresenter();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);
        ButterKnife.bind(this);
        getData();
        initView();
    }

    private void getData() {
        guestId = getIntent().getIntExtra("guest_id", 0);
    }

    private void initView() {
        loadingView.setState(LoadingView.State.LOADING);
        mPresenter.initData(guestId, page, type);
        rvRecord.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommonAdapter<GuestRecordBean.DataBean>(this, R.layout.rv_shop_list_item, dataList) {
            @Override
            protected void convert(ViewHolder holder, GuestRecordBean.DataBean dataBean, int position) {
                holder.setText(R.id.tv_item, dataBean.getRecent_visit_at());
            }

        };
        rvRecord.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });


        sfrRecord.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page++;
                type = 2;
                mPresenter.initData(guestId, page, type);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                type = 1;
                mPresenter.initData(guestId, page, type);
            }
        });

        loadingView.setOnRetryListener(new LoadingView.OnRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.initData(guestId, page, type);
            }
        });
    }

    @Override
    public void finishRefresh() {
        sfrRecord.finishRefresh();
    }

    @Override
    public void finishLoad() {
        sfrRecord.finishLoadMore();
    }

    @Override
    public void loadSuccess(List<GuestRecordBean.DataBean> recordList) {
        if (type != 2) {
            dataList.clear();
        }
        dataList.addAll(recordList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void loadFail(String msg) {
        ToastUtils.showToast(GuestRecordActivity.this, msg);
    }

    @Override
    public void hideLoading() {
        loadingView.setState(LoadingView.State.LOAD_DONE);
    }

    @Override
    public void loadError() {
        loadingView.setState(LoadingView.State.LOAD_ERROR);
    }
}
