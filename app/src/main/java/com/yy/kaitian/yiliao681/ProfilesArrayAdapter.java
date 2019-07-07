package com.yy.kaitian.yiliao681;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilesArrayAdapter extends ArrayAdapter<String>
{
  private final Context context;
  private final String[] values;

  public ProfilesArrayAdapter(Context paramContext, String[] paramArrayOfString)
  {
    super(paramContext, R.layout.profiles_list, paramArrayOfString);
    this.context = paramContext;
    this.values = paramArrayOfString;
  }

  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    View localView = ((LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.profiles_list, paramViewGroup, false);
    TextView localTextView = (TextView)localView.findViewById(R.id.label);
    ImageView localImageView = (ImageView)localView.findViewById(R.id.logo);
    localTextView.setText(this.values[paramInt]);
    localImageView.setImageResource(R.drawable.ios_logo);
    return localView;
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.ProfilesArrayAdapter
 * JD-Core Version:    0.6.2
 */