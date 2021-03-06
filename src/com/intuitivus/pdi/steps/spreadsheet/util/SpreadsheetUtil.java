package com.intuitivus.pdi.steps.spreadsheet.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.pentaho.di.core.encryption.Encr;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class SpreadsheetUtil
{

	public static WorksheetFeed connectWorksheetFeed(String user, String password, String documentId) throws AuthenticationException
	{

		try
		{
			SpreadsheetService service = new SpreadsheetService("");
			service.setUserCredentials(user, Encr.decryptPasswordOptionallyEncrypted(password));
			URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/worksheets/" + documentId + "/private/full");
			return service.getFeed(SPREADSHEET_FEED_URL, WorksheetFeed.class);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ServiceException e)
		{
			e.printStackTrace();
		}

		return null;

	}

	public static WorksheetEntry connectWorksheetFeed(String user, String password, String documentId, String sheet) throws AuthenticationException
	{

		WorksheetFeed feed = connectWorksheetFeed(user, password, documentId);
		List<WorksheetEntry> worksheets = feed.getEntries();

		WorksheetEntry worksheet = null;
		if (sheet == null || sheet.isEmpty())
		{
			worksheet = worksheets.get(0);
		} else
		{
			for (WorksheetEntry we : worksheets)
			{
				if (we.getTitle().getPlainText().equalsIgnoreCase(sheet))
				{
					worksheet = we;
				}
			}
		}
		return worksheet;

	}

	public static CellFeed connectCellFeed(String user, String password, String documentId, String sheet, Range range) throws AuthenticationException
	{
		WorksheetEntry worksheet = connectWorksheetFeed(user, password, documentId, sheet);
		return connectCellFeed(worksheet, range);
	}

	public static CellFeed connectCellFeed(WorksheetEntry worksheet, Range range) throws AuthenticationException
	{

		CellQuery query = new CellQuery(worksheet.getCellFeedUrl());
		if (range != null)
			query.setRange(range.toRange(worksheet));

		query.setReturnEmpty(false);

		try
		{
			return worksheet.getService().query(query, CellFeed.class);
		} catch (ServiceException e)
		{
		} catch (IOException e)
		{
		}

		return null;

	}

}