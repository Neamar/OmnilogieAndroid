package fr.omnilogie.app;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Cette classe permet d'adapter une Liste d'ArticleObject dans un ListView.
 * 
 * @author Benoit
 * 
 */
public class ArticleObjectAdapter extends BaseAdapter {

	private Context context;
	private List<ArticleObject> articleList;

	/**
	 * Adapte une liste d'ArticleObject dans un ListView
	 * 
	 * @param context
	 * @param articleList
	 */
	public ArticleObjectAdapter(Context context, List<ArticleObject> articleList) {
		this.context = context;
		this.articleList = articleList;
	}

	public int getCount() {
		return articleList.size();
	}

	public Object getItem(int position) {
		return articleList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * Surcharge : Crée la vue pour chaque article, à partir du layout
	 * list_item.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		try {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_liste_article, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArticleObject article = articleList.get(position);
		if (article != null) {
			TextView titreTextView = (TextView) v.findViewById(R.id.list_item_title);
			TextView accrocheTextView = (TextView) v.findViewById(R.id.list_item_subtitle);
			TextView auteurTextView = (TextView) v.findViewById(R.id.list_item_extra);
			ImageView banniereImageView = (ImageView) v.findViewById(R.id.list_item_image);

			// place le titre
			if (titreTextView != null)
				titreTextView.setText(Html.fromHtml(article.titre));

			// place l'accroche
			if (accrocheTextView != null)
				accrocheTextView.setText(Html.fromHtml(article.accroche));

			// place l'auteur
			if (auteurTextView != null)
				auteurTextView.setText(Html.fromHtml("par " + article.auteur + ", le "
						+ article.dateParution));

			// place l'image
			if (banniereImageView != null && article.banniere != ArticleObject.DEFAULT_IMAGE) {
				if (article.banniereBmp != null)
					banniereImageView.setImageBitmap(article.banniereBmp);
				else {
					ImageDownloader downloader = new ImageDownloader(banniereImageView, article);
					downloader.execute(article.banniere);
				}
			}
		}

		return v;
	}
}
