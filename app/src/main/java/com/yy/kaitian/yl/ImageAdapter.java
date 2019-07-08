package com.yy.kaitian.yl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    LayoutInflater mInflater;
    private ArrayList<Item> mItems = new ArrayList();

    public ImageAdapter(Context paramContext) {
        this.mContext = paramContext;
        this.mInflater = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }

    public void addItem(int paramInt1, String paramString, int paramInt2) {
        this.mItems.add(new Item(paramInt1, paramString, paramInt2));
    }

    public int getCount() {
        return this.mItems.size();
    }

    public Object getItem(int paramInt) {
        return this.mItems.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        ViewHolder localViewHolder;
        if (paramView == null) {
            paramView = this.mInflater.inflate(R.layout.detect_image_item, null);
            localViewHolder = new ViewHolder();
            localViewHolder.text = ((TextView) paramView.findViewById(R.id.grid_item_label));
            localViewHolder.icon = ((ImageView) paramView.findViewById(R.id.grid_item_image));
            paramView.setTag(localViewHolder);
        } else {
            localViewHolder = (ViewHolder) paramView.getTag();
        }
        localViewHolder.text.setText(((Item) this.mItems.get(paramInt)).imageTitle);
        localViewHolder.icon.setImageResource(((Item) this.mItems.get(paramInt)).imageId);
        localViewHolder.text.setTextColor(((Item) this.mItems.get(paramInt)).titleColor);
        return paramView;
    }

    public void setItemColor(int paramInt1, int paramInt2) {
        ((Item) this.mItems.get(paramInt1)).titleColor = paramInt2;
    }

    public class Item {
        public int imageId;
        public String imageTitle;
        public int titleColor;

        public Item(int paramString, String paramInt1, int arg4) {
            this.imageId = paramString;
            this.imageTitle = paramInt1;
//      int i = arg4;
            this.titleColor = arg4;
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView text;
    }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.ImageAdapter
 * JD-Core Version:    0.6.2
 */