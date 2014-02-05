package com.intuitivus.pdi.updater.callbacks;

import java.util.Properties;

public interface PluginUpdaterVersionDownloadCallback
{	
	public void onData( Properties version, int lengthReaded, int lengthTotal );
	public void onDataFinish( Properties version );
	public void onFinish( Properties version );
}
