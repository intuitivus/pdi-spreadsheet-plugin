package com.intuitivus.pdi.updater;

import java.util.Properties;

import com.intuitivus.pdi.updater.callbacks.PluginUpdaterVersionCheckCallback;
import com.intuitivus.pdi.updater.callbacks.PluginUpdaterVersionDownloadCallback;

public class PluginUpdater
{

	private Properties current;
	private Properties remote;

	PluginUpdaterVersionCheckCallback checkCallback;
	PluginUpdaterVersionDownloadCallback downloadCallback;

	public PluginUpdater(Properties current, final PluginUpdaterVersionCheckCallback checkCallback, PluginUpdaterVersionDownloadCallback downloadCallback)
	{
		this.current = current;
		this.checkCallback = new PluginUpdaterVersionCheckCallback()
		{

			@Override
			public void onNewVersion(Properties newversion)
			{
				remote = newversion;
				checkCallback.onNewVersion(newversion);
			}
		};
		this.downloadCallback = downloadCallback;
	}

	public void checkVersion()
	{
		new Thread(new PluginUpdaterVersionCheck(current, checkCallback)).start();
	}

	public void download()
	{
		new Thread(new PluginUpdaterDownload(remote, downloadCallback)).start();
	}

	public Properties getRemote()
	{
		return remote;
	}

}
