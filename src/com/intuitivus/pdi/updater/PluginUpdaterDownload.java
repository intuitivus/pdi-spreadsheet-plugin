package com.intuitivus.pdi.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import com.intuitivus.pdi.updater.callbacks.PluginUpdaterVersionDownloadCallback;

public class PluginUpdaterDownload implements Runnable
{
	private PluginUpdaterVersionDownloadCallback downloadCallback;
	private Properties newversion;

	public PluginUpdaterDownload(Properties newversion, PluginUpdaterVersionDownloadCallback downloadCallback)
	{
		this.newversion = newversion;
		this.downloadCallback = downloadCallback;
	}

	@Override
	public void run()
	{
		try
		{
			File plugin = downloadPlugin(newversion.getProperty("download"));
			downloadCallback.onDataFinish(newversion);

			File pluginFolder = unzipPlugin(plugin);
			replacePlugin(pluginFolder);

			downloadCallback.onFinish(newversion);

		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private File downloadPlugin(String url) throws IOException
	{

		URL download = new URL(url);
		HttpURLConnection con = (HttpURLConnection) download.openConnection();
		File outputFile = File.createTempFile(newversion.getProperty("id"), null);

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK)
		{
			InputStream inputStream = con.getInputStream();
			OutputStream outputStream = new FileOutputStream(outputFile);

			int lengthTotal = con.getContentLength();
			int lengthReaded = 0;

			int bytesRead;
			byte[] buffer = new byte[8 * 1024];
			while ((bytesRead = inputStream.read(buffer)) > 0)
			{

				outputStream.write(buffer, 0, bytesRead);
				lengthReaded += bytesRead;

				downloadCallback.onData(newversion, lengthReaded, lengthTotal);

			}

			outputStream.close();
			inputStream.close();
		}

		return outputFile;
	}

	private File unzipPlugin(File zipFile)
	{

		try
		{

			File outputFolder = new File(zipFile.getParent());
			File pdiOutput = new File(outputFolder, newversion.getProperty("id"));
			if (pdiOutput.exists())
			{
				FileUtils.deleteDirectory(pdiOutput);
			}

			ZipInputStream zip = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry entry = null;
			int len;
			byte[] buffer = new byte[1024];

			while ((entry = zip.getNextEntry()) != null)
			{
				if (!entry.isDirectory())
				{
					File file = new File(outputFolder, entry.getName());
					if (!new File(file.getParent()).exists())
						new File(file.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(file);
					while ((len = zip.read(buffer)) > 0)
					{
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
			}

			zip.close();

			if (pdiOutput.exists())
			{
				return pdiOutput;
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private void replacePlugin(File plugin)
	{

		File kettleStepFolder = new File(System.getProperty("user.dir"), "plugins" + File.separator + "steps");
		File pluginFolder = new File(kettleStepFolder, newversion.getProperty("id"));
		try
		{
			if (pluginFolder.exists())
			{
				FileUtils.deleteDirectory(pluginFolder);
			}
		} catch (Exception e)
		{
		}

		try
		{
			FileUtils.copyDirectory(plugin, pluginFolder);
		} catch (Exception e)
		{
		}
	}

}
