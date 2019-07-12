package com.yy.kaitian.yl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class PointDatasPreference2Adapter extends BaseAdapter {
    private final Context context;
    private final List<Map<String, String>> mapList;

    public PointDatasPreference2Adapter(Context paramContext, List<Map<String, String>> mapList) {
        this.context = paramContext;
        this.mapList = mapList;
    }

    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Object getItem(int position) {
        return mapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup) {
        View localView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_customer_list_preference2, paramViewGroup, false);
        TextView tv_name = (TextView) localView.findViewById(R.id.tv_name);
        TextView tv_name2 = (TextView) localView.findViewById(R.id.tv_name2);
        TextView tv_start = (TextView) localView.findViewById(R.id.tv_start);
        ImageView iv_delete = (ImageView) localView.findViewById(R.id.iv_delete);

        tv_name.setText(mapList.get(paramInt).get("name"));
        tv_name2.setText(mapList.get(paramInt).get("summary"));
        tv_start.setText("解读");


        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditClick.setonEditClick(paramInt);
            }
        });
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartCheckClick.setOnStartCheckClick(paramInt);
            }
        });
        return localView;
    }

    OnEditClick onEditClick;

    public interface OnEditClick {
        void setonEditClick(int position);
    }

    public void setonEditClick(OnEditClick onEditClick) {
        this.onEditClick = onEditClick;
    }

    OnStartCheckClick onStartCheckClick;

    public interface OnStartCheckClick {
        void setOnStartCheckClick(int position);
    }

    public void setOnStartCheckClick(OnStartCheckClick onStartCheckClick) {
        this.onStartCheckClick = onStartCheckClick;
    }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.ProfilesArrayAdapter
 * JD-Core Version:    0.6.2
 */