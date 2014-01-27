package com.intuitivus.pdi.steps.spreadsheet.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.PropsUI;

import com.intuitivus.pdi.steps.spreadsheet.IntuitivusSpreadsheetStepDialog;
import com.intuitivus.pdi.steps.spreadsheet.IntuitivusSpreadsheetStepMeta;

public class DialogTest
{
	
	static protected Properties prop;
	
	{
		
		prop = new Properties();
		FileInputStream input = null;
	 
		try {
			input = new FileInputStream("build.properties");
			prop.load(input);	 
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
		}
		
	}

	@Test
	public void test() throws KettleException
	{
		
		System.setProperty("user.dir", prop.getProperty("kettle.dir"));
		
		KettleEnvironment.init();
		PropsUI.init(Display.getDefault(), 0);
		IntuitivusSpreadsheetStepMeta meta = new IntuitivusSpreadsheetStepMeta();
		IntuitivusSpreadsheetStepDialog dialog = new IntuitivusSpreadsheetStepDialog(new Shell(), meta, new TransMeta(), "Step");
		dialog.open();
		
	}

}
