package com.intuitivus.pdi.steps.spreadsheet;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.util.AuthenticationException;
import com.intuitivus.pdi.steps.spreadsheet.util.SpreadsheetUtil;

public class IntuitivusSpreadsheetStep extends BaseStep implements StepInterface
{

	private IntuitivusSpreadsheetStepMeta meta;
	private IntuitivusSpreadsheetStepData data;

	public IntuitivusSpreadsheetStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis)
	{
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi)
	{
		meta = (IntuitivusSpreadsheetStepMeta) smi;
		data = (IntuitivusSpreadsheetStepData) sdi;

		return super.init(meta, data);
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi)
	{
		meta = (IntuitivusSpreadsheetStepMeta) smi;
		data = (IntuitivusSpreadsheetStepData) sdi;

		super.dispose(meta, data);
	}

	@SuppressWarnings("deprecation")
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException
	{

		IntuitivusSpreadsheetStepMeta meta = (IntuitivusSpreadsheetStepMeta) smi;
		IntuitivusSpreadsheetStepData data = (IntuitivusSpreadsheetStepData) sdi;

		if (first)
		{
			first = false;

			String user = meta.getDriveUser();
			String password = meta.getDrivePassword();
			String documentId = meta.getDriveDocumentId();
			String sheet = meta.getDriveSheet();
			String range = meta.getDriveRange();

			String cells[] = range.split(":");
			int rowNumber;
			
			switch (meta.getDriveHeader())
			{
			case FIRST:
				rowNumber = Integer.parseInt(cells[0].replaceAll("([A-Z]+)", ""));
				cells[0] = cells[0].replaceAll("([0-9]+)", Integer.toString(rowNumber+1));
				break;
			case NONE:
				break;
			case ROW1:
				rowNumber = Integer.parseInt(cells[0].replaceAll("([A-Z]+)", ""));
				if(rowNumber == 1)
					cells[0] = cells[0].replaceAll("([0-9]+)", Integer.toString(rowNumber+1));
				break;
			default:
				break;

			}
			
			range = cells[0] + ":" + cells[1];

			try
			{
				data.cellFeed = SpreadsheetUtil.connectCellFeed(user, password, documentId, sheet, range);
			} catch (AuthenticationException e)
			{
				throw new KettleStepException();
			}

			data.calculateRangeSize(range);
			data.refreshCachedData();

			data.outputRowMeta = new RowMeta();
			if (meta.adoptOutput())
				meta.getFields(data.outputRowMeta, getStepname(), null, null, this, null, null);
			else
				meta.getFieldsForPreview(data.outputRowMeta, getStepname(), data.cols);

			data.conversionMeta = new ValueMetaInterface[meta.getOutputField().length];
			for (int i = 0; i < meta.getOutputField().length; i++)
			{
				ValueMetaInterface returnMeta = data.outputRowMeta.getValueMeta(i);
				ValueMetaInterface conversionMeta = returnMeta.clone();
				conversionMeta.setType(ValueMetaInterface.TYPE_STRING);
				data.conversionMeta[i] = conversionMeta;
			}
		}

		int size = data.outputRowMeta.size();
		CellEntry[] entry = data.getNextCellRow();
		if (entry != null)
		{
			if (!meta.adoptOutput())
				size = entry.length;

			Object[] outputRow = RowDataUtil.allocateRowData(size);
			for (int i = 0; i < size; i++)
			{
				if (meta.adoptOutput()) {
					
					Object value;
					
					ValueMetaInterface valueMeta = data.outputRowMeta.getValueMeta(i);
					int type = valueMeta.getType();
					
					switch(type) {
					default:
					case ValueMetaInterface.TYPE_STRING:
						value = entry[i].getCell().getValue();
						break;
						
					case ValueMetaInterface.TYPE_NUMBER:
						value = entry[i].getCell().getNumericValue();
						break;
					}
					
					outputRow[i] = data.outputRowMeta.getValueMeta(i).convertData(data.conversionMeta[i], value);
					
				} else
					outputRow[i] = entry[i].getCell().getValue();
			}

			putRow(data.outputRowMeta, outputRow);
			return true;
		} else
		{
			setOutputDone();
			return false;
		}
	}

}
