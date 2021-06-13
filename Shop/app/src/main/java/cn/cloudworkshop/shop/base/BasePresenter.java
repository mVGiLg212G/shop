package cn.cloudworkshop.shop.base;

/**
 * Author：Libin on 2018/10/9 17:14
 * Email：1993911441@qq.com
 * Describe：
 */
public interface BasePresenter<T extends BaseView> {
    public void attach(T mView);

    public void detach();
}
