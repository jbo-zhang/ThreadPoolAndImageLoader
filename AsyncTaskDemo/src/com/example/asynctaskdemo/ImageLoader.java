package com.example.asynctaskdemo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
	private static final String TAG = "ImageLoader";

	private static final int TAG_KEY_URI = R.id.tag_first;
//	private static final int TAG_KEY_URI = 23;

	protected static final int MESSAGE_POST_RESULT = 1;
	private LruCache<String, Bitmap> mMemoryCache;
	private DiskLruCache mDiskLruCache;

	private Context mContext;

	private ImageResizer mImageResizer = new ImageResizer();

	private long DISK_CACHE_SIZE = 50 * 1024 * 1024;
	private static final int IO_BUFFER_SIZE = 8 * 1024;
	private static final int DISK_CACHE_INDEX = 0;

	private boolean mIsDiskLruCacheCreated = false;

	private ImageLoader(Context context) {
		mContext = context.getApplicationContext();
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};

		File diskCacheDir = getDiskCacheDir(mContext, "bitmap");
		if (!diskCacheDir.exists()) {
			diskCacheDir.mkdirs();
		}

		if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
			try {
				mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1,
						DISK_CACHE_SIZE);
				mIsDiskLruCacheCreated = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ImageLoader build(Context context) {
		return new ImageLoader(context);
	}

	public Bitmap loadBitmap(String uri, int reqWidth, int reqHeight) {
		Bitmap bitmap = loadBitmapFromMemCache(uri);
		if (bitmap != null) {
			Log.d(TAG, "loadBitmapFromMemCache, url: " + uri);
			return bitmap;
		}

		try {
			bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
			if (bitmap != null) {
				Log.d(TAG, "loadBitmapFromDisk, url: " + uri);
				return bitmap;
			}
			bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight);
			Log.d(TAG, "loadBitmapFromHttp, url: " + uri);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (bitmap == null && !mIsDiskLruCacheCreated) {
			Log.w(TAG, "encounter error, DiskLruCache is not created.");
			bitmap = downloadBitmapFromUrl(uri);
		}
		return bitmap;
	}

	private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			LoaderResult result = (LoaderResult) msg.obj;
			ImageView imageView = result.imageView;
			imageView.setImageBitmap(result.bitmap);
			String uri = (String) imageView.getTag(TAG_KEY_URI);
			if (uri.equals(result.uri)) {
				imageView.setImageBitmap(result.bitmap);
			} else {
				Log.w(TAG, "set image bitmap, but url has changed, ignored!");
			}
		}
	};

	private static final int CPU_COUNT = Runtime.getRuntime()
			.availableProcessors();
	private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
	private static final int KEEP_ALIVE = 1;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "ImageLoader #" + mCount.getAndIncrement());
		}
	};

	private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(
			128);

	/**
	 * An {@link Executor} that can be used to execute tasks in parallel.
	 */
	public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
			CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
			sPoolWorkQueue, sThreadFactory);

	public void bindBitmap(final String uri, final ImageView imageView) {
		bindBitmap(uri, imageView, 0, 0);
	}

	public void bindBitmap(final String uri, final ImageView imageView,
			final int reqWidth, final int reqHeight) {
		imageView.setTag(TAG_KEY_URI, uri);
		Bitmap bitmap = loadBitmapFromMemCache(uri);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			return;
		}

		Runnable loadBitmapTask = new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = loadBitmap(uri, reqWidth, reqHeight);
				if (bitmap != null) {
					LoaderResult result = new LoaderResult(imageView, uri,
							bitmap);
					mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result)
							.sendToTarget();
				}
			}
		};

		THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
	}

	private Bitmap downloadBitmapFromUrl(String urlString) {
		Bitmap bitmap = null;
		HttpURLConnection urlConnection = null;
		BufferedInputStream in = null;
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
			bitmap = BitmapFactory.decodeStream(in);
		} catch (final IOException e) {
			Log.e(TAG, "Error in downloadBitmap: " + e);
		} finally {
			if(urlConnection != null) {
				urlConnection.disconnect();
			}
			MyUtils.close(in);
		}
		return bitmap;
	}

	private Bitmap loadBitmapFromMemCache(String uri) {
		final String key = hashKeyFromUrl(uri);
		Bitmap bitmap = getBitmapFromMemCache(key);
		return bitmap;
	}

	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	private Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	private Bitmap loadBitmapFromHttp(String url, int reqWidth, int reqHeight)
			throws IOException {
		// 如果在主线程调用，抛出异常
		if (Looper.myLooper() == Looper.getMainLooper()) {
			throw new RuntimeException("can not visit network from UI Thread.");
		}

		if (mDiskLruCache == null) {
			return null;
		}

		String key = hashKeyFromUrl(url);

		DiskLruCache.Editor editor = mDiskLruCache.edit(key);
		if (editor != null) {
			OutputStream outputStream = editor
					.newOutputStream(DISK_CACHE_INDEX);
			if (downloadUrlToStream(url, outputStream)) {
				editor.commit();
			} else {
				editor.abort();
			}
			mDiskLruCache.flush();
		}

		return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
	}

	private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		
		URL url;
		try {
			url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
			out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
			int b;
			while((b = in.read()) != -1) {
				out.write(b);
			}
			return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "downloadBitmap failed." + e);
		} finally {
			if(urlConnection != null) {
				urlConnection.disconnect();
			}
			MyUtils.close(out);
			MyUtils.close(in);
		}
		
		return false;
	}

	private Bitmap loadBitmapFromDiskCache(String url, int reqWidth,
			int reqHeight) throws IOException {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			Log.w(TAG, "load bitmap from UI Thread, it's not recommended!");
		}
		if (mDiskLruCache == null) {
			return null;
		}

		Bitmap bitmap = null;
		String key = hashKeyFromUrl(url);

		DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);

		if (snapShot != null) {
			FileInputStream fileInputStream = (FileInputStream) snapShot
					.getInputStream(DISK_CACHE_INDEX);
			FileDescriptor fileDescriptor = fileInputStream.getFD();
			bitmap = mImageResizer.decodeSampleBitmapFromDescriptor(
					fileDescriptor, reqWidth, reqHeight);
			if (bitmap != null) {
				addBitmapToMemoryCache(key, bitmap);
			}

		}
		return bitmap;
	}

	private String hashKeyFromUrl(String url) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(url.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(url.hashCode());
		}
		return cacheKey;
	}
	
	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i< bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if(hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	private File getDiskCacheDir(Context context, String uniqueName) {
		boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		final String cachePath;
		if(externalStorageAvailable) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	@TargetApi(VERSION_CODES.GINGERBREAD)
	private long getUsableSpace(File path) {
		if(Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}
	
	private static class LoaderResult {
		public ImageView imageView;
		public String uri;
		public Bitmap bitmap;
		
		public LoaderResult(ImageView imageView, String uri, Bitmap bitmap) {
			this.imageView = imageView;
			this.uri = uri;
			this.bitmap = bitmap;
		}
	}

}
