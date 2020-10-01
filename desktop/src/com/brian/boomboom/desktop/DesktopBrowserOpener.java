package com.brian.boomboom.desktop;

import com.brian.boomboom.BrowserOpener;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class DesktopBrowserOpener implements BrowserOpener
{

	@Override
	public void OpenUrl(String url)
	{
		try
		{
			openWebpage(new URI(url));
		}
		catch (URISyntaxException e)
		{
		}
	}

	public static void openWebpage(URI uri)
	{
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
		{
			try
			{
				desktop.browse(uri);
			}
			catch (Exception e)
			{
			}
		}
	}

	public static void openWebpage(URL url)
	{
		try
		{
			openWebpage(url.toURI());
		}
		catch (URISyntaxException e)
		{
		}
	}
}
