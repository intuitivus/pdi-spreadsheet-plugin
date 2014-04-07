package com.intuitivus.pdi.steps.spreadsheet.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.intuitivus.pdi.steps.spreadsheet.IntuitivusSpreadsheetStepMeta.HeaderType;

public class Range
{

	private Cell from, to;

	public static final String PATTERN = "(" + Cell.PATTERN + "):(" + Cell.PATTERN + ")";

	public Range()
	{
	}

	public Range(String rangeRef)
	{
		final Pattern pattern = Pattern.compile(PATTERN);
		final Matcher matcher = pattern.matcher(rangeRef);
		if (matcher.find())
		{
			String from = matcher.group(1);
			String to = matcher.group(4);
			this.from = Cell.parse(from);
			this.to = Cell.parse(to);
		} else
		{
			this.from = new Cell();
			this.to = new Cell();
		}
	}

	public Cell getFrom()
	{
		return from;
	}

	public void setFrom(Cell from)
	{
		this.from = from;
	}

	public Cell getTo()
	{
		return to;
	}

	public void setTo(Cell to)
	{
		this.to = to;
	}

	public String toRange()
	{
		return toRange(null);
	}

	public String toRange(WorksheetEntry worksheet)
	{

		StringBuilder builder = new StringBuilder();

		Range real = this.clone();
		real.realistic(worksheet);

		builder.append(real.from.getColRef());
		builder.append(real.from.getRow());
		builder.append(":");
		builder.append(real.to.getColRef());
		builder.append(real.to.getRow());

		return builder.toString();

	}

	public int getWidth()
	{
		return to.getCol() - from.getCol() + 1;
	}

	public int getHeight()
	{
		return to.getRow() - from.getRow() + 1;
	}

	public Range clone()
	{
		Range range = new Range();
		range.from = new Cell(this.from.getRow(), this.from.getCol());
		range.to = new Cell(this.to.getRow(), this.to.getCol());
		return range;
	}

	public Range getRangeHeader(HeaderType type)
	{

		Range newRange = clone();

		switch (type)
		{
		case ROW1:
			newRange.from.setRow(1);
			newRange.to.setRow(1);
			return newRange;

		case FIRST:
			newRange.to.setRow(newRange.from.getRow());
			return newRange;

		case NONE:
		default:
			return null;

		}

	}

	public Range getRangeBody(HeaderType type)
	{

		Range newRange = clone();

		switch (type)
		{
		case FIRST:
			newRange.from.setRow(newRange.from.getRow() + 1);
			return newRange;

		case ROW1:
			if(newRange.from.getRow() == 1)
				newRange.from.setRow(newRange.from.getRow() + 1);
			return newRange;
			
		case NONE:
		default:
			return newRange;
		}

	}

	public void realistic(WorksheetEntry worksheet)
	{
		if (from.getCol() == Cell.BOUNDARY)
		{
			from.setCol(1);
		}

		if (from.getRow() == Cell.BOUNDARY)
		{
			from.setRow(1);
		}

		int colCount = worksheet.getColCount();
		if (to.getCol() == Cell.BOUNDARY || to.getCol() > colCount)
		{
			to.setCol(colCount);
		}

		int rowCount = worksheet.getRowCount();
		if (to.getRow() == Cell.BOUNDARY || to.getRow() > rowCount)
		{
			to.setRow(rowCount);
		}
	}

}