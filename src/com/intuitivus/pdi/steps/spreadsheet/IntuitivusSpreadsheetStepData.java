package com.intuitivus.pdi.steps.spreadsheet;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.google.gdata.data.spreadsheet.Cell;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.util.AuthenticationException;
import com.intuitivus.pdi.steps.spreadsheet.util.SpreadsheetUtil;

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
	
	public void calculateRangeSize(String range) {
		int[] rangeSize = IntuitivusSpreadsheetStepMeta.calculateRangeSize(range);
		int[] offset = IntuitivusSpreadsheetStepMeta.calculateOffset(range);
		rows = rangeSize[0];
		cols = rangeSize[1];
		offsetrow = offset[0];
		offsetcol = offset[1];
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

	public CellEntry[] getNextCellRow()
	{
		if (this.currentRow < this.rows)
		{
			return this.cellEntries[this.currentRow++];
		} else
		{
			return null;
		}
	}
	
	public static IntuitivusSpreadsheetStepData getData(IntuitivusSpreadsheetStepMeta meta) throws AuthenticationException {
		IntuitivusSpreadsheetStepData data = new IntuitivusSpreadsheetStepData();
		data.cellFeed = SpreadsheetUtil.connectCellFeed(meta.getDriveUser(), meta.getDrivePassword(), meta.getDriveDocumentId(), meta.getDriveSheet(), meta.getDriveRange());
		data.calculateRangeSize(meta.getDriveRange());
		data.refreshCachedData();
		return data;
	}

}
