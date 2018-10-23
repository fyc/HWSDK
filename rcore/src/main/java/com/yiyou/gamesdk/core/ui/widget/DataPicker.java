package com.yiyou.gamesdk.core.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yiyou.gamesdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by levyyoung on 15/6/16.
 */
public class DataPicker<T extends DataPicker.IDataWrapper> {

    private PopupWindow popupWindow;
    private ListView listView;
    private DataListAdapter<T> dataListAdapter = new DataListAdapter<>();
    private DataPickerListener<T> dataPickerListener;
    private int originHeight;
    private int itemHeight;

    private static final int PADDING = 2;

    public DataPicker(final Context context, int width, int height, boolean focus) {
        LinearLayout container = new LinearLayout(context);
        listView = new ListView(context);
        listView.setAdapter(dataListAdapter);
        listView.setDividerHeight(0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int paddingPx = Math.round(context.getResources().getDisplayMetrics().density * PADDING);
        listView.setPadding(paddingPx,0,paddingPx,paddingPx);
        container.addView(listView, params);
        container.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.tt_sdk_popup_normal_bg));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dataListAdapter.setSelectedPosition(position);
                if (dataPickerListener != null) {
                    dataPickerListener.onDataSelected(DataPicker.this, position);
                }
                dataListAdapter.notifyDataSetChanged();
                DataPicker.this.dismiss();
            }
        });
        popupWindow = new PopupWindow(container, width, height, focus);
        ColorDrawable cd = new ColorDrawable(Color.parseColor("#00f1f5fb"));
        popupWindow.setBackgroundDrawable(cd);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (dataPickerListener != null) {
                    dataPickerListener.onDataPickerDestroy();
                }
            }
        });
        originHeight = height;
        itemHeight = Math.round(context.getResources().getDisplayMetrics().density * 40);
    }

    public List<T> getSourceData() {
        return dataListAdapter.getData();
    }

    public void deleteItem(int position) {
        if (dataListAdapter.selectedPos == position) {
            dataListAdapter.selectedPos = ListView.INVALID_POSITION;
        }
        T removedItem = dataListAdapter.removeItemAtPosition(position);
        if (removedItem != null) {
            if (dataPickerListener != null) {
                dataPickerListener.onDataDeleted(DataPicker.this, (T) removedItem);
            }
            dataListAdapter.notifyDataSetChanged();
            DataPicker.this.dismiss();
        }
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (popupWindow.isShowing() || popupWindow.getContentView() == null) {
            return;
        }
        popupWindow.showAtLocation(parent, gravity, x, y);
        if (dataPickerListener != null) {
            dataPickerListener.onDataPickerShow(this);
        }
    }

    public void showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
    }

    public void showAsDropDown(View anchor,int x, int y) {
        showAsDropDown(anchor, x, y, Gravity.TOP | Gravity.START);
    }

    @TargetApi(19)
    public void showAsDropDown(View anchor, int x, int y, int gravity) {
        int listHeight = dataListAdapter.getCount() * itemHeight;
        if (listHeight < originHeight){
            popupWindow.setHeight(listHeight);
        }
        if (popupWindow.isShowing() || popupWindow.getContentView() == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            popupWindow.showAsDropDown(anchor, x, y, gravity);
        } else {
            popupWindow.showAsDropDown(anchor, x, y);
        }
        if (dataPickerListener != null) {
            dataPickerListener.onDataPickerShow(this);
        }
    }

    public void setSelectedPosition(int position) {
        if (position >= 0 && position < dataListAdapter.getCount()) {
            dataListAdapter.setSelectedPosition(position);
        }
        dataListAdapter.notifyDataSetChanged();
    }

    public void setSelectedData(T data) {
        if (popupWindow == null || listView == null || !popupWindow.isShowing()) {
            return;
        }
        int pos = dataListAdapter.getItemPosition(data);
        if (pos != ListView.INVALID_POSITION) {
            dataListAdapter.setSelectedPosition(pos);
        }
        dataListAdapter.notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        if (popupWindow == null || listView == null || !popupWindow.isShowing()) {
            return ListView.INVALID_POSITION;
        }
        return dataListAdapter.getSelectedPos();
    }

    @SuppressWarnings("unchecked")
    public T getSelectedData() {
        if (popupWindow == null || listView == null || !popupWindow.isShowing()) {
            return null;
        }

        return dataListAdapter.getItem(dataListAdapter.getSelectedPos());
    }

    public synchronized void setDataSource(List<T> source) {
        dataListAdapter.setData(source);
    }

    public synchronized void appendDataSource(List<T> source) {
        dataListAdapter.appendData(source);
    }

    public synchronized void setDataPickerListener(DataPickerListener<T> listener) {
        dataPickerListener = listener;
    }

    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private class DataListAdapter<D extends IDataWrapper> extends BaseAdapter {
        private List<D> dataList = new ArrayList<>();
        private int selectedPos = -1;

        public void setSelectedPosition(int selectedPos) {
            this.selectedPos = selectedPos;
        }

        public int getSelectedPos() {
            return selectedPos;
        }

        public void setData(List<D> newDataList) {
            if (newDataList == null) {
                return;
            }
            dataList.clear();
            dataList.addAll(newDataList);
        }

        public List<D> getData() {
            return new ArrayList<>(dataList);
        }

        public void appendData(List<D> dataToBeAppended) {
            if (dataToBeAppended == null) {
                return;
            }
            dataList.addAll(dataToBeAppended);
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public D getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            DataViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.tt_sdk_item_data_pick, null);
                holder = new DataViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (DataViewHolder) convertView.getTag();
            }

            D data = getItem(position);
            holder.name.setText(data.dataName());
            if (selectedPos == position) {
                holder.greenDot.setVisibility(View.VISIBLE);
            } else {
                holder.greenDot.setVisibility(View.INVISIBLE);
            }
            
            if ((getCount()-1)==position) {
				holder.viewLine.setVisibility(View.GONE);
			}else {
				holder.viewLine.setVisibility(View.VISIBLE);
			}
            
            holder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    D closedItem = getItem(position);
                    if (closedItem != null) {
                        if (dataPickerListener != null) {
                            dataPickerListener.onDataItemClose(DataPicker.this, position, (T) closedItem);
                        }
                    }
                }
            });

            return convertView;
        }

        public D removeItemAtPosition(int position) {
            if (position >= 0 && position < getCount()) {
                D data = dataList.get(position);
                dataList.remove(data);
                return data;
            }
            return null;
        }

        public int getItemPosition(D data) {
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).equals(data)) {
                    return i;
                }
            }
            return ListView.INVALID_POSITION;
        }

        class DataViewHolder{

            public DataViewHolder(View convertView) {
                greenDot =  convertView.findViewById(R.id.dp_green_dot);
                close =  convertView.findViewById(R.id.dp_close);
                name = (TextView) convertView.findViewById(R.id.dp_data_name);
                viewLine = (View) convertView.findViewById(R.id.view_line) ;
            }

            public View greenDot;
            public View close;
            public TextView  name;
            public View viewLine ;
        }

    }
    public interface DataPickerListener<D extends IDataWrapper>{

        void onDataSelected(DataPicker picker, int position);

        void onDataDeleted(DataPicker picker, D data);

        void onDataPickerShow(DataPicker picker);

        void onDataPickerDestroy();

        void onDataItemClose(DataPicker picker, int position, D data);

    }

    public interface IDataWrapper{
        public String dataName();
        public String key();
        public Object data();
    }

}
