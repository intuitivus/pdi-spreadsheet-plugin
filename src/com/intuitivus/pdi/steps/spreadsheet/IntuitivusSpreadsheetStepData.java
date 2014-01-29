package com.intuitivus.pdi.steps.spreadsheet;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.google.gdata.data.spreadsheet.Cell;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.intuitivus.pdi.steps.spreadsheet.util.Range;

public class IntuitivusSpreadsheetStepData extends BaseStepData implements StepDataInterface
{

	public RowMetaInterface outputRowMeta;
	public ValueMetaInterface[] conversionMeta;

	public CellEntry[][] cellEntries;

	int rows, cols, offsetrow, offsetcol, currentRow;

	public CellFeed cellFeed;

	public IntuitivusSpreadsheetStepData()
	{
		super();
	}

	public void calculateRangeSize(Range range)
	{
		rows = range.getHeight();
		cols = range.getWidth();
		offsetrow = range.getFrom().getRow();
		offsetcol = range.getFrom().getCol();
	}

	public void refreshCachedData()
	{
		cellEntries = new CellEntry[this.rows][this.cols];
		for (CellEntry cellEntry : cellFeed.getEntries())
		{
			Cell cell = cellEntry.getCell();
			cellEntries[cell.getRow() - offsetrow][cell.getCol() - offsetcol] = cellEntry;
		}
	}

	public boolean isEmptyRow(CellEntry[] entries)
	{
		boolean hasData = false;
		for (int i = 0; i < entries.length; i++)
		{
			if (entries[i] != null)
			{
				hasData = true;
				break;
			}
		}
		return !hasData;
	}

	public CellEntry[] getNextCellRow(boolean acceptEmptyLines)
	{
		if (this.currentRow < this.rows)
		{
			CellEntry[] entry = this.cellEntries[this.currentRow++];

			if (!acceptEmptyLines)
			{
				while (entry != null && isEmptyRow(entry))
				{
					entry = getNextCellRow(acceptEmptyLines);
				}
			}

			return entry;

		} else
		{
			return null;
		}
	}

}
