package fr.omnilogie.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Ce provider inutile permet d'éviter les warnings d'Android.
 * 
 * En effet, le fait de passer par des URI impose normalement la création d'un
 * Provider. C'était une erreur de notre part, et cette classe sert de rustine
 * dégueulasse pour corriger un problème qui n'aurait pas dû exister.
 * 
 * @author neamar
 * 
 */
public class OmnilogieProvider extends ContentProvider {
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// Auto-generated method stub
		return 0;
	}

}
