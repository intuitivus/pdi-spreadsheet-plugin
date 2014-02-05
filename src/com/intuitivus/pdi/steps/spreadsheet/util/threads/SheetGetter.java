package com.intuitivus.pdi.steps.spreadsheet.util.threads;

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.dialog.EnterSelectionDialog;
import org.pentaho.di.ui.core.widget.TextVar;

import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.intuitivus.pdi.steps.spreadsheet.IntuitivusSpreadsheetStepDialog;
import com.intuitivus.pdi.steps.spreadsheet.IntuitivusSpreadsheetStepMeta;
import com.intuitivus.pdi.steps.spreadsheet.util.SpreadsheetUtil;

public class SheetGetter implements Runnable
{

	private static Class<?> PKG = IntuitivusSpreadsheetStepDialog.class;

	private IntuitivusSpreadsheetStepMeta meta;
	private Shell shell;
	private Display display;
	private TextVar target;
	
	public SheetGetter(IntuitivusSpreadsheetStepMeta meta, Shell shell, TextVar target)
	{
		this.shell = shell;
		this.display = shell.getDisplay();
		this.meta = meta;
		this.target = target;
	}

	@Override
	public void run()
	{
		try
		{
			WorksheetFeed feed = SpreadsheetUtil.connectWorksheetFeed(meta.getDriveUser(), meta.getDrivePassword(), meta.getDriveDocumentId());
			List<WorksheetEntry> entries = feed.getEntries();
			final String[] entriesList = new String[entries.size()];

			for (int i = 0; i < entriesList.length; i++)
			{
				entriesList[i] = entries.get(i).getTitle().getPlainText();
			}

			display.syncExec(new Runnable()
			{
				@Override
				public void run()
				{			
					String title = BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveSheet.Search.Title");
					String description = BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveSheet.Search.Description");
					EnterSelectionDialog selection = new EnterSelectionDialog(shell, entriesList, title, description);
					selection.setMulti(false);
					String selectedSheet = selection.open();
					if (selectedSheet != null)
						target.setText(selectedSheet);
				}
			});

		} catch (AuthenticationException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
