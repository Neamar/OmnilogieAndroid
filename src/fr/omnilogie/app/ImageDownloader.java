package fr.omnilogie.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.MalformedURLException;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * Download asynchronously images, using a cache. 
 * 
 * @see Load images and data asynchronously on your Android applications http://bench87.tistory.com/56
 * @see Android Image Caching http://pivotallabs.com/users/tyler/blog/articles/1754-android-image-caching
 **/

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

	static boolean initializeCache = true;
	private final WeakReference<ImageView> imageViewReference;
	private final WeakReference<ArticleObject> articleReference;

	/**
	 * Download a picture in the background
	 * @param imageView l'imageView dans lequel l'image devra ensuite être insérée
	 * @param defaultImage
	 * @param context
	 */
	public ImageDownloader(ImageView imageView, ArticleObject article, Context context) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		articleReference = new WeakReference<ArticleObject>(article);
		
		if(initializeCache)
		{
			// create cache
			initializeCache(context);
			initializeCache = false;
		}
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		String url = params[0];
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setUseCaches(true);
			return BitmapFactory.decodeStream(connection.getInputStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (isCancelled()) {
			result = null;
		}
		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				imageView.setImageBitmap(result);
			}
			
			// stockage de l'image dans l'article 
			if (articleReference != null) {
				ArticleObject article = articleReference.get();
				if (article != null) {
					article.banniereBmp = result;
				}
			}
		}
	}
	
	protected void initializeCache(Context context)
	{
		final File cacheDir;
		
		// Find the dir to save cached images
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();    
			cacheDir = new File(sdDir,"data/fr.omnilogie.app/cache/bannieres");
		}
		else
			cacheDir = context.getCacheDir();
		
		if(!cacheDir.exists())
			cacheDir.mkdirs();
		
		
		ResponseCache.setDefault(new ResponseCache() {
		    @Override
		    public CacheResponse get(URI uri, String s, Map<String, List<String>> headers) throws IOException {
		        final File file = new File(cacheDir, escape(uri.getPath()));
		        if (file.exists()) {
		        	Log.v("omni_cache","Load cached image: "+ file.getName());
		            return new CacheResponse() {
		                @Override
		                public Map<String, List<String>> getHeaders() throws IOException {
		                    return null;
		                }

		                @Override
		                public InputStream getBody() throws IOException {
		                    return new FileInputStream(file);
		                }
		            };
		        } else {
		            return null;
		        }
		    }

		    @Override
		    public CacheRequest put(URI uri, URLConnection urlConnection) throws IOException {
		        final File file = new File(cacheDir, escape(urlConnection.getURL().getPath()));
		        return new CacheRequest() {
		            @Override
		            public OutputStream getBody() throws IOException {
		                return new FileOutputStream(file);
		            }

		            @Override
		            public void abort() {
		                file.delete();
		            }
		        };
		    }

		    private String escape(String url) {
		       return url.replace("/", "-").replace(".", "-");
		    }

		});
	}
}