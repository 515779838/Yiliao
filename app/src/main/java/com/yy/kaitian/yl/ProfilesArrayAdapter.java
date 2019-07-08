package com.yy.kaitian.yl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilesArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public ProfilesArrayAdapter(Context paramContext, String[] paramArrayOfString) {
        super(paramContext, R.layout.profiles_list, paramArrayOfString);
        this.context = paramContext;
        this.values = paramArrayOfString;
    }

    public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup) {
        View localView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.profiles_list, paramViewGroup, false);
        TextView tv_start = (TextView) localView.findViewById(R.id.tv_start);
        TextView tv_edit = (TextView) localView.findViewById(R.id.tv_edit);
        TextView localTextView = (TextView) localView.findViewById(R.id.label);
        ImageView localImageView = (ImageView) localView.findViewById(R.id.logo);
        localTextView.setText(this.values[paramInt]);
        localImageView.setImageResource(R.mipmap.avatar_default);

        tv_edit.setOnClickListener(new View.OnClickListener() {
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