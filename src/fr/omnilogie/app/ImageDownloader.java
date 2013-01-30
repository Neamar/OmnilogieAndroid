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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Download asynchronously images, using a cache on SD card.
 * 
 * @author Benoit
 * 
 * @see Load images and data asynchronously on your Android applications
 *      http://bench87.tistory.com/56
 * @see Android Image Caching
 *      http://pivotallabs.com/users/tyler/blog/articles/1754
 *      -android-image-caching
 **/

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

	static boolean isCacheReady = false;
	private final WeakReference<ImageView> imageViewReference;
	private final WeakReference<ArticleObject> articleReference;

	/**
	 * Download a picture in the background
	 * 
	 * @param imageView
	 *            l'imageView dans lequel l'image devra ensuite être insérée
	 * @param defaultImage
	 */
	public ImageDownloader(ImageView imageView, ArticleObject article) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		articleReference = new WeakReference<ArticleObject>(article);

		// cache need to be reinitialized if no SD card mounted
		String sdState = android.os.Environment.getExternalStorageState();
		if (!sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			isCacheReady = false;
		}

		if (!isCacheReady) {
			// create cache routine in a SD card directory
			isCacheReady = prepareCache();
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

	/**
	 * Initialize cache routine, in order to be used by {@link URLConnection}
	 * 
	 * @see Cache routine
	 *      http://docs.oracle.com/javase/1.5.0/docs/guide/net/images/cache.gif
	 * 
	 * @return success
	 */
	protected boolean prepareCache() {
		final File cacheDir;
		final boolean success;

		// Initialize cache if SD card mounted
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {

			// Prepare a folder on SD card to cache images
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdDir, "data/fr.omnilogie.app/cache/bannieres");

			if (!cacheDir.exists())
				cacheDir.mkdirs();

			success = true;

			// Set cache routine
			ResponseCache.setDefault(new ResponseCache() {
				@Override
				public CacheResponse get(URI uri, String s, Map<String, List<String>> headers)
						throws IOException {
					final File file = new File(cacheDir, escape(uri.getPath()));
					if (file.length() > 500) {
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
						file.getCanonicalFile().delete();
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
		} else
			success = false;

		return success;
	}
}