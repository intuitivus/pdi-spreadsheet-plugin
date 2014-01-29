package com.intuitivus.pdi.steps.spreadsheet.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cell
{

	private int row = BOUNDARY, col = BOUNDARY;

	public static final int BOUNDARY = -1;
	public static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String PATTERN = "([A-Z]+)?([0-9]+)?";

	public Cell()
	{

	}

	public Cell(int row, int col)
	{
		this.row = row;
		this.col = col;
	}

	public int getRow()
	{
		return row;
	}

	public void setRow(int row)
	{
		this.row = row;
	}

	public int getCol()
	{
		return col;
	}

	public String getColRef()
	{
		return colToCharIndex(col);
	}

	public void setCol(int col)
	{
		this.col = col;
	}

	public static Cell parse(String reference)
	{
		Cell cell = new Cell();
		cell.calculateCell(reference);
		return cell;
	}

	public void calculateCell(String cellRef)
	{

		this.row = 1;
		this.col = 1;

		final Pattern pattern = Pattern.compile(PATTERN);
		final Matcher matcher = pattern.matcher(cellRef);
		if (matcher.find())
		{
			String col = matcher.group(1);
			if (col != null)
				this.col = Cell.colToIntIndex(col);
			else
				this.col = Cell.BOUNDARY;

			String row = matcher.group(2);
			if (row != null)
				this.row = Integer.parseInt(row);
			else
				this.row = Cell.BOUNDARY;
		}

	}

	public static int colToIntIndex(String col)
	{

		int colIndex = 0;
		int length = LETTERS.length();

		col = new StringBuilder(col).reverse().toString();
		char[] colLetters = col.trim().toCharArray();
		for (int i = 0; i < colLetters.length; i++)
		{
			int letterValue = LETTERS.indexOf(colLetters[i]);
			colIndex += (((int) Math.pow(length, i)) * (letterValue + 1));
		}

		return colIndex;

	}

	public static String colToCharIndex(int column)
	{
		StringBuilder colFinal = new StringBuilder();
		while (column > 0)
		{
			int n = (column - 1) % 26;
			colFinal.append(LETTERS.charAt(n));
			column = (column - n - 1) / 26;
		}
		return colFinal.reverse().toString();
	}

}
