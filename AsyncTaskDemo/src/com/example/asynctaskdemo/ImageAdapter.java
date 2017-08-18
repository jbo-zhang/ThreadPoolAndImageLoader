package com.example.asynctaskdemo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private ArrayList<String> mUrList = new ArrayList<String>();
	private Context mContext;
	private Drawable mDefaultBitmapDrawable;

	private ImageLoader mImageLoader;
	private boolean mIsGridViewIdle = true;
	private boolean mCanGetBitmapFromNetWork = true;
	private int mImageWidth = 200;

	{
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3463226768,421579507&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3430625942,2154503364&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2478348785,905143055&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2917191212,3271723673&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1884738033,3459235394&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2888635565,3576638811&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1990601815,3596917195&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=190749879,1758335090&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3965895917,3845310417&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2114314313,2280521117&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3463226768,421579507&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3430625942,2154503364&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2478348785,905143055&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2917191212,3271723673&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1884738033,3459235394&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2888635565,3576638811&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1990601815,3596917195&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=190749879,1758335090&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3965895917,3845310417&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1045570639,546326319&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1045570639,546326319&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=11830911,2789307326&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1266709467,2634091832&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1275537332,4241650625&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1406668637,2493237568&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1430286656,456418205&fm=26&gp=0.jpg");
		mUrList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1442038534,817842994&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1443817543,4124882906&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1461512776,3902274546&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=167137552,1940157147&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=167422256,2437266230&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1742604591,821454748&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3463226768,421579507&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3430625942,2154503364&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2478348785,905143055&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2917191212,3271723673&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1884738033,3459235394&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2888635565,3576638811&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1990601815,3596917195&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=190749879,1758335090&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3965895917,3845310417&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2114314313,2280521117&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3463226768,421579507&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3430625942,2154503364&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2478348785,905143055&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2917191212,3271723673&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1884738033,3459235394&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2888635565,3576638811&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1990601815,3596917195&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=190749879,1758335090&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3965895917,3845310417&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1045570639,546326319&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1045570639,546326319&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=11830911,2789307326&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1266709467,2634091832&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1275537332,4241650625&fm=26&gp=0.jpg");
		mUrList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1406668637,2493237568&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1430286656,456418205&fm=26&gp=0.jpg");
		mUrList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1442038534,817842994&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1443817543,4124882906&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1461512776,3902274546&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=167137552,1940157147&fm=26&gp=0.jpg");
		mUrList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=167422256,2437266230&fm=26&gp=0.jpg");
		mUrList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1742604591,821454748&fm=26&gp=0.jpg");
	}

	public ImageAdapter(Context context) {
		this.mContext = context;
		mDefaultBitmapDrawable = context.getResources().getDrawable(
				R.drawable.ic_launcher);
		mImageLoader = ImageLoader.build(context);

		try {
			URL url = new URL(
					"https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fr=&sf=1&fmq=1462357247335_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word=%E9%AB%98%E6%B8%85%E5%8A%A8%E6%BC%AB");
			URLConnection openConnection = url.openConnection();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public int getCount() {
		return mUrList.size();
	}

	@Override
	public String getItem(int position) {
		return mUrList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.pic_item, parent, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ImageView imageView = holder.imageView;
		final String tag = (String) imageView.getTag();
		final String uri = getItem(position);
		if (!uri.equals(tag)) {
			imageView.setImageDrawable(mDefaultBitmapDrawable);
		}
		if (mIsGridViewIdle && mCanGetBitmapFromNetWork) {
			imageView.setTag(uri);
			mImageLoader.bindBitmap(uri, imageView, mImageWidth, mImageWidth);
		}

		return convertView;
	}

	static class ViewHolder {
		public ImageView imageView;
	}

	public void setGridViewIdle(boolean b) {
		// mIsGridViewIdle = b;
		mIsGridViewIdle = true;
	}

	public void setCanGetBitmapFromNetWork(boolean b) {
		mCanGetBitmapFromNetWork = b;
	}

}
