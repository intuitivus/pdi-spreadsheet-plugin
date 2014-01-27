package com.intuitivus.pdi.steps.spreadsheet.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
			service.setUserCredentials(user, password);
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

	public static CellFeed connectCellFeed(String user, String password, String documentId, String sheet, String range) throws AuthenticationException
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

		CellQuery query = new CellQuery(worksheet.getCellFeedUrl());
		query.setRange(range);
		query.setReturnEmpty(true);

		try
		{
			return feed.getService().query(query, CellFeed.class);
		} catch (ServiceException e)
		{
		} catch (IOException e)
		{
		}
		
		return null;

	}

}
