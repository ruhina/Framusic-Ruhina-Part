package com.team8.framusicv2;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PreferenceSimpleAdapter extends SimpleAdapter {
	private int[] mTo;
	private String[] mFrom;
	private ViewBinder mViewBinder;
	private List<? extends Map<String, ?>> mData;
	private int mResource;
	private LayoutInflater mInflater;

	public PreferenceSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub

		mData = data;
		mResource = resource;
		mFrom = from;
		mTo = to;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mResource);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		View v;
		if (convertView == null) {
			v = mInflater.inflate(resource, parent, false);

			final int[] to = mTo;
			final int count = to.length;
			final View[] holder = new View[count];

			for (int i = 0; i < count; i++) {
				holder[i] = v.findViewById(to[i]);
			}

			v.setTag(holder);
		} else {
			v = convertView;
		}
		bindView(position, v);

		return v;
	}

	private void bindView(int position, View view) {
		final Map dataSet = mData.get(position);
		if (dataSet == null) {
			return;
		}
		final ViewBinder binder = mViewBinder;
		final View[] holder = (View[]) view.getTag();
		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

		if (position <= 2) {
			LinearLayout ll = (LinearLayout) view
					.findViewById(R.id.ContentOfElements);
			RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) ll
					.getLayoutParams();
			linearParams.height = 240;
			ll.setLayoutParams(linearParams);

			for (int i = 0; i < count; i++) {
				final View v = holder[i];
				if (v != null) {
					final Object data = dataSet.get(from[i]);
					String text = data == null ? "" : data.toString();
					if (text == null) {
						text = "";
					}
					boolean bound = false;
					if (binder != null) {
						bound = binder.setViewValue(v, data, text);
					}
					if (!bound) {
						if (v instanceof TextView) {
							((TextView) v).setText(text);
							((TextView) v).setTextSize(30);
						} else if (v instanceof ImageView) {
							
							v.setVisibility(View.GONE);
						}
					}
				}
			}
		}
		if (position > 2) {
			LinearLayout ll = (LinearLayout) view
					.findViewById(R.id.ContentOfElements);
			
			RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) ll
					.getLayoutParams();
			linearParams.height = 100;
	
			ll.setLayoutParams(linearParams);

			for (int i = 0; i < count; i++) {
				final View v = holder[i];
				if (v != null) {
					final Object data = dataSet.get(from[i]);
					String text = data == null ? "" : data.toString();
					if (text == null) {
						text = "";
					}
					boolean bound = false;
					if (binder != null) {
						bound = binder.setViewValue(v, data, text);
					}
					if (!bound) {
						if (v instanceof TextView) {
							((TextView) v).setText(text);
						} else if (v instanceof ImageView) {
							if (data instanceof Integer) {
								((ImageView) v)
										.setImageResource((Integer) data);
							}
						}
					}
				}
			}
		}
	}
}
