package com.yiyou.gamesdk.core.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.container.MainActivity;
import com.yiyou.gamesdk.core.base.http.RequestManager;
import com.yiyou.gamesdk.core.ui.common.CommonTitlePrimaryFragment;
import com.yiyou.gamesdk.model.NativeTitleBarUpdateInfo;

public abstract class BaseFragment extends Fragment {

    protected String myTag = ((Object)this).getClass().getSimpleName();

    private static final String TAG_FRAGMENT_TITLE_BAR = "TagFragmentTitleBar";

    private boolean isFragmentVisible;
    private boolean isReuseView;
    private boolean isFirstVisible;

    //setUserVisibleHint()在Fragment创建时会先被调用一次，传入isVisibleToUser = false
    //如果当前Fragment可见，那么setUserVisibleHint()会再次被调用一次，传入isVisibleToUser = true
    //如果Fragment从可见->不可见，那么setUserVisibleHint()也会被调用，传入isVisibleToUser = false
    //总结：setUserVisibleHint()除了Fragment的可见状态发生变化时会被回调外，在new Fragment()时也会被回调
    //如果我们需要在 Fragment 可见与不可见时干点事，用这个的话就会有多余的回调了，那么就需要重新封装一个
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //setUserVisibleHint()有可能在fragment的生命周期外被调用
        if (contentView == null) {
            return;
        }
        if (isFirstVisible && isVisibleToUser) {
            onFragmentFirstVisible();
            isFirstVisible = false;
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true);
            isFragmentVisible = true;
            return;
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            onFragmentVisibleChange(false);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(myTag, myTag + " onActivityCreated");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
        Log.i(myTag, myTag + " onCreate " + (savedInstanceState==null));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(myTag, myTag + " onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(myTag, myTag + " onStop");
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.i(myTag, myTag + " onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(myTag, myTag + " onResume");
    }

    @Override
    public void onDestroyView() {
    	super.onDestroyView();
        Log.i(myTag, myTag + " onDestroyView");
      
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = view;
            if (getUserVisibleHint()) {
                if (isFirstVisible) {
                    onFragmentFirstVisible();
                    isFirstVisible = false;
                }
                onFragmentVisibleChange(true);
                isFragmentVisible = true;
            }
        }
        super.onViewCreated(isReuseView ? contentView : view, savedInstanceState);
        Log.i(myTag, myTag + " onViewCreated");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(myTag, myTag + " onAttach");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        initVariable();
        EventDispatcherAgent.defaultAgent()
                .removeEventListenersBySource(this);
        Log.i(myTag, myTag + " onDestroy");
    }

    private View contentView;
    private ViewGroup titleContainer;

    protected final ViewGroup getTitleContainer() {
        return titleContainer;
    }

    private Fragment titleBarFragment;
    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int contentViewResId = getCustomFragmentLayoutResId();
        if (contentView == null) {
            if (contentViewResId > 0) {
                contentView = inflater.inflate(contentViewResId, container,false);
            }else {
                contentView = inflater.inflate(getDefaultFragmentLayoutResId(), container,false);
            }
            titleContainer = (ViewGroup) contentView.findViewById(R.id.title_bar_container);
            titleBarFragment = getTitleBarFragment();
            if (titleBarFragment != null) {

                /**
                 * 先判断外部是否用传进来的参数
                 */
                Bundle outArgument = titleBarFragment.getArguments();

                if (outArgument == null)
                    outArgument = new Bundle();

                outArgument.putParcelable(CommonTitlePrimaryFragment.TITLE_BAR_ARG, getTitleBarConfig());


                getChildFragmentManager().beginTransaction().replace(R.id.title_bar_container,
                        titleBarFragment, TAG_FRAGMENT_TITLE_BAR).commit();
            }
            beforeSetFragmentContent(contentView.getContext(), titleBarFragment);
            ViewGroup contentContainer = (ViewGroup) contentView.findViewById(R.id.content_container);
            setFragmentContent(contentView.getContext(), contentContainer,titleBarFragment);
            afterSetFragmentContent(contentView.getContext(),titleBarFragment);
        }

        if (contentView.getParent() != null) {
            ((ViewGroup)contentView.getParent()).removeView(contentView);
            Log.e("TTViews", this+" contentView reuse. remove from parent.");
        }

        return contentView;
    }


    protected void beforeSetFragmentContent(Context context, Fragment titleBarFragment) {

    }

    /**
     * 此步骤运行与onCreateView末段。用于设置fragment显示内容。
     */
    protected abstract void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment);

    protected void afterSetFragmentContent(Context context, Fragment titleBarFragment) {

    }

    /**
     *
     * @return 自定义布局资源ID.
     */
    protected int getCustomFragmentLayoutResId() {
        return 0;
    }

    /**
     * 自定义资源无效时将使用
     * @return 默认布局资源ID.
     */
    protected int getDefaultFragmentLayoutResId() {
        return R.layout.tt_sdk_fragment_base_default;
    }

    protected Fragment getTitleBarFragment() {
        return null;
    }


    /**
     * 唯一标志每个framgent实例 用来取消与该实例相关的所有请求 see {@link com.android.volley1.Request#setTag(Object)}
     * @return hashcode
     */
    protected int getVolleyTag(){
        return hashCode();
    }

    @Override
    public void onDetach() {
        Log.i(myTag, myTag + " onDetach");
        /**
         * 当fragment从activity脱离时候 取消所有在该framgent发出的未完成请求
         */
        RequestManager.getInstance(getActivity()).cancelRequest(getVolleyTag());

        super.onDetach();
    }


    /**
     *子类可以重写此方法来修改titlebar配置
     * @return NativeTitleBarUpdateInfo
     */
    public NativeTitleBarUpdateInfo getTitleBarConfig() {


        return null;
    }

//    public void startFragment(Fragment fragment){
//        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
//        ft.add(android.R.id.content, fragment);
//        ft.addToBackStack("");
//        ft.commit();
//    }

     public String getTabName(){
         return null;
     }

    private void initVariable() {
        isFirstVisible = true;
        isFragmentVisible = false;
        contentView = null;
        isReuseView = true;
    }

    /**
     * 设置是否使用 view 的复用，默认开启
     * view 的复用是指，ViewPager 在销毁和重建 Fragment 时会不断调用 onCreateView() -> onDestroyView()
     * 之间的生命函数，这样可能会出现重复创建 view 的情况，导致界面上显示多个相同的 Fragment
     * view 的复用其实就是指保存第一次创建的 view，后面再 onCreateView() 时直接返回第一次创建的 view
     *
     * @param isReuse
     */
    protected void reuseView(boolean isReuse) {
        isReuseView = isReuse;
    }

    /**
     * 去除setUserVisibleHint()多余的回调场景，保证只有当fragment可见状态发生变化时才回调
     * 回调时机在view创建完后，所以支持ui操作，解决在setUserVisibleHint()里进行ui操作有可能报null异常的问题
     *
     * 可在该回调方法里进行一些ui显示与隐藏，比如加载框的显示和隐藏
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    protected void onFragmentVisibleChange(boolean isVisible) {

    }

    /**
     * 在fragment首次可见时回调，可在这里进行加载数据，保证只在第一次打开Fragment时才会加载数据，
     * 这样就可以防止每次进入都重复加载数据
     * 该方法会在 onFragmentVisibleChange() 之前调用，所以第一次打开时，可以用一个全局变量表示数据下载状态，
     * 然后在该方法内将状态设置为下载状态，接着去执行下载的任务
     * 最后在 onFragmentVisibleChange() 里根据数据下载状态来控制下载进度ui控件的显示与隐藏
     */
    protected void onFragmentFirstVisible() {

    }

    protected boolean isFragmentVisible() {
        return isFragmentVisible;
    }

    public void startFragment(BaseFragment fragment){
        startFragment(fragment, false);
    }

    /**
     * 打开新fragment
     * @param fragment  目标fragment
     * @param flag  是否需要关闭当前fragment， true：关闭当前fragment； false：不需要关闭当前fragment
     */
    public void startFragment(BaseFragment fragment, boolean flag){
        ((MainActivity)getActivity()).startFragment(fragment, flag);
    }
    public boolean onBackPressed(){
        return false;
    }
}
