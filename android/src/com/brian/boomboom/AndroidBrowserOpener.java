package com.brian.boomboom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AndroidBrowserOpener implements BrowserOpener
{
	Context context;

	public AndroidBrowserOpener(Context context)
	{
		this.context = context;
	}

	public void OpenUrl(String url)
	{
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(i);
	}

}
