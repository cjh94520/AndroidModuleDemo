package com.wss.module.market.ui.goods.cart;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wss.common.base.ActionBarActivity;
import com.wss.common.bean.Event;
import com.wss.common.constants.EventAction;
import com.wss.common.listener.OnListItemClickListener;
import com.wss.common.utils.ToastUtils;
import com.wss.module.market.R;
import com.wss.module.market.R2;
import com.wss.module.market.bean.Vendor;
import com.wss.module.market.ui.goods.cart.adapter.ShoppingCartAdapter;
import com.wss.module.market.ui.goods.cart.mvp.CartPresenter;
import com.wss.module.market.ui.goods.cart.mvp.contract.ShoppingCartContract;
import com.wss.module.market.utils.ShoppingCartUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Describe：购物车
 * Created by 吴天强 on 2018/11/5.
 */

public class ShoppingCartActivity extends ActionBarActivity<CartPresenter> implements ShoppingCartContract.View,
        OnListItemClickListener {


    @BindView(R2.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R2.id.iv_check_all)
    ImageView ivCheckAll;
    @BindView(R2.id.tv_total)
    TextView tvTotal;
    @BindView(R2.id.btn_buy)
    TextView btnBuy;

    @Override
    protected CartPresenter createPresenter() {
        return new CartPresenter();
    }

    private List<Vendor> mData = new ArrayList<>();
    private ShoppingCartAdapter adapter;


    @Override
    protected int getLayoutId() {

        return R.layout.market_activity_shopping_cart;
    }

    @Override
    protected void initView() {
        setTitleText("购物车");
        adapter = new ShoppingCartAdapter(mContext, mData, R.layout.market_item_of_shopping_cart_list, this);
        recycleView.setLayoutManager(new LinearLayoutManager(mContext));
        recycleView.setAdapter(adapter);
        actionBar.setRightText("清空", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingCartUtils.cleanLocal();
            }
        });
        presenter.start();
    }

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    public void onEventBus(Event event) {
        super.onEventBus(event);
        if (TextUtils.equals(EventAction.EVENT_SHOPPING_CART_REFRESH, event.getAction())) {
            //购物车有变化
            adapter.notifyDataSetChanged();
            ivCheckAll.setSelected(ShoppingCartUtils.isAllVendorChecked(mData));
            displayResult();
        } else if (TextUtils.equals(EventAction.EVENT_SHOPPING_CART_CLEAN, event.getAction())) {
            showEmptyView("车里空空如也");
        }
    }

    private void displayResult() {
        btnBuy.setSelected(ShoppingCartUtils.isCheckedLeastOne(mData));
        tvTotal.setText(String.format("%s%s", getString(R.string.total), ShoppingCartUtils.getCartCountPrice(mData)));
    }


    @Override
    public void cartData(List<Vendor> dataList) {
        this.mData.clear();
        this.mData.addAll(dataList);
        adapter.notifyDataSetChanged();
        //默认全部选中
        ShoppingCartUtils.checkAll(mData, true);
        ivCheckAll.setSelected(true);
        displayResult();
    }


    @OnClick({R2.id.iv_check_all, R2.id.btn_buy})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.iv_check_all) {
            boolean selected = view.isSelected();
            view.setSelected(!selected);
            ShoppingCartUtils.checkAll(mData, !selected);
            adapter.notifyDataSetChanged();
            displayResult();
        } else if (i == R.id.btn_buy) {
            ToastUtils.showToast(mContext, "可点击");
        }
    }

    @Override
    public void onEmpty(Object tag) {
        super.onEmpty(tag);
        showEmptyView("车里空空如也");
    }

    @Override
    public void onItemClick(View view, int position) {
        //TODO
    }
}
