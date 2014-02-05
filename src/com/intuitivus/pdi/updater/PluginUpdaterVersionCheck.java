package com.intuitivus.pdi.updater;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import com.intuitivus.pdi.updater.callbacks.PluginUpdaterVersionCheckCallback;

public class PluginUpdaterVersionCheck implements Runnable
{

	private Properties current;

	PluginUpdaterVersionCheckCallback checkCallback;

	public PluginUpdaterVersionCheck(Properties current, PluginUpdaterVersionCheckCallback checkCallback)
	{
		this.current = current;
		this.checkCallback = checkCallback;
	}

	@Override
	public void run()
	{
		try
		{
			URL url = new URL(current.getProperty("check"));
			Properties properties = new Properties();
			URLConnection connection = url.openConnection();
			properties.load(connection.getInputStream());

			int compare = compareVersionNumbers(current.getProperty("version"), properties.getProperty("version"));
			if (compare == -1)
			{
				checkCallback.onNewVersion(properties);
			}
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static int compareVersionNumbers(String local, String remote)
	{

		String[] localVer = local.split("\\.");
		String[] remoteVer = remote.split("\\.");

		int min = Math.min(localVer.length, remoteVer.length);

		for (int i = 0; i < min; i++)
		{
			int compare = Integer.compare(Integer.parseInt(localVer[i]), Integer.parseInt(remoteVer[i]));
			if (compare != 0)
				return compare;
		}

		return Integer.compare(localVer.length, remoteVer.length);

	}

}
