package com.intuitivus.pdi.steps.spreadsheet;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransPreviewFactory;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.EnterSelectionDialog;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;
import org.pentaho.di.ui.core.dialog.PreviewRowsDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.dialog.TransPreviewProgressDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.intuitivus.pdi.steps.spreadsheet.IntuitivusSpreadsheetStepMeta.HeaderType;
import com.intuitivus.pdi.steps.spreadsheet.util.Range;
import com.intuitivus.pdi.steps.spreadsheet.util.SpreadsheetUtil;

public class IntuitivusSpreadsheetStepDialog extends BaseStepDialog implements StepDialogInterface
{

	private static Class<?> PKG = IntuitivusSpreadsheetStepMeta.class;

	private IntuitivusSpreadsheetStepMeta meta;

	private TableView fieldsTable;

	private Label driveUserLabel;
	private FormData driveUserLabelFormData;
	private TextVar driveUserField;
	private FormData driveUserFieldFormData;

	private Label drivePasswordLabel;
	private FormData drivePasswordLabelFormData;
	private TextVar drivePasswordField;
	private FormData drivePasswordFieldFormData;

	private Label driveDocumentIdLabel;
	private FormData driveDocumentIdLabelFormData;
	private TextVar driveDocumentIdField;
	private FormData driveDocumentIdFieldFormData;

	private Label driveSheetLabel;
	private FormData driveSheetLabelFormData;
	private TextVar driveSheetField;
	private FormData driveSheetFieldFormData;
	private Button driveSheetRefresh;
	private FormData driveSheetRefreshFormData;

	private Label driveRangeLabel;
	private FormData driveRangeLabelFormData;
	private TextVar driveRangeField;
	private FormData driveRangeFieldFormData;

	private Label driveHeaderLabel;
	private FormData driveHeaderLabelFormData;
	private Combo driveHeaderField;
	private FormData driveHeaderFieldFormData;

	private Label driveEmptyLinesLabel;
	private FormData driveEmptyLinesLabelFormData;
	private Button driveEmptyLinesField;
	private FormData driveEmptyLinesFieldFormData;

	public IntuitivusSpreadsheetStepDialog(Shell parent, Object in, TransMeta transMeta, String sname)
	{
		super(parent, (BaseStepMeta) in, transMeta, sname);
		meta = (IntuitivusSpreadsheetStepMeta) in;
	}

	public String open()
	{

		final Shell parent = getParent();
		final Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, meta);

		changed = meta.hasChanged();

		final Listener lsMod = new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				if (!meta.hasChanged())
				{
					shell.setText(shell.getText() + " *");
				}
				meta.setChanged();
				wPreview.setEnabled(meta.hasDataToConnect());
			}
		};

		lsDef = new SelectionAdapter()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				ok();
			}
		};

		lsOK = new Listener()
		{
			public void handleEvent(Event e)
			{
				ok();
			}
		};

		lsGet = new Listener()
		{
			public void handleEvent(Event e)
			{
				get();
			}
		};

		lsPreview = new Listener()
		{
			public void handleEvent(Event e)
			{
				preview();
			}
		};

		lsCancel = new Listener()
		{
			public void handleEvent(Event e)
			{
				cancel();
			}
		};

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;
		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.title"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		//
		// Stepname line
		//
		fdStepname = new FormData();
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		wStepname.setLayoutData(fdStepname);
		props.setLook(wStepname);

		fdlStepname = new FormData();
		fdlStepname.top = new FormAttachment(wStepname, 0, SWT.CENTER);
		fdlStepname.bottom = new FormAttachment(wStepname, 0, SWT.CENTER);
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.StepName.Label"));
		wlStepname.setLayoutData(fdlStepname);
		props.setLook(wlStepname);

		//
		// Drive User
		//
		driveUserFieldFormData = new FormData();
		driveUserFieldFormData.top = new FormAttachment(wStepname, margin);
		driveUserFieldFormData.left = new FormAttachment(middle, 0);
		driveUserFieldFormData.right = new FormAttachment(100, 0);
		driveUserField = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		driveUserField.setLayoutData(driveUserFieldFormData);
		props.setLook(driveUserField);

		driveUserLabelFormData = new FormData();
		driveUserLabelFormData.top = new FormAttachment(driveUserField, 0, SWT.CENTER);
		driveUserLabelFormData.bottom = new FormAttachment(driveUserField, 0, SWT.CENTER);
		driveUserLabelFormData.left = new FormAttachment(0, 0);
		driveUserLabelFormData.right = new FormAttachment(middle, -margin);
		driveUserLabel = new Label(shell, SWT.RIGHT);
		driveUserLabel.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveUser.Label"));
		driveUserLabel.setLayoutData(driveUserLabelFormData);
		props.setLook(driveUserLabel);

		//
		// Drive Password
		//
		drivePasswordFieldFormData = new FormData();
		drivePasswordFieldFormData.top = new FormAttachment(driveUserField, margin);
		drivePasswordFieldFormData.left = new FormAttachment(middle, 0);
		drivePasswordFieldFormData.right = new FormAttachment(100, 0);
		drivePasswordField = new TextVar(transMeta, shell, SWT.PASSWORD | SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		drivePasswordField.setLayoutData(drivePasswordFieldFormData);
		props.setLook(drivePasswordField);

		drivePasswordLabelFormData = new FormData();
		drivePasswordLabelFormData.top = new FormAttachment(drivePasswordField, 0, SWT.CENTER);
		drivePasswordLabelFormData.bottom = new FormAttachment(drivePasswordField, 0, SWT.CENTER);
		drivePasswordLabelFormData.left = new FormAttachment(0, 0);
		drivePasswordLabelFormData.right = new FormAttachment(middle, -margin);
		drivePasswordLabel = new Label(shell, SWT.RIGHT);
		drivePasswordLabel.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DrivePassword.Label"));
		drivePasswordLabel.setLayoutData(drivePasswordLabelFormData);
		props.setLook(drivePasswordLabel);

		//
		// Drive DocumentId
		//
		driveDocumentIdFieldFormData = new FormData();
		driveDocumentIdFieldFormData.top = new FormAttachment(drivePasswordField, margin);
		driveDocumentIdFieldFormData.left = new FormAttachment(middle, 0);
		driveDocumentIdFieldFormData.right = new FormAttachment(100, 0);
		driveDocumentIdField = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		driveDocumentIdField.setLayoutData(driveDocumentIdFieldFormData);
		props.setLook(driveDocumentIdField);

		driveDocumentIdLabelFormData = new FormData();
		driveDocumentIdLabelFormData.top = new FormAttachment(driveDocumentIdField, 0, SWT.CENTER);
		driveDocumentIdLabelFormData.bottom = new FormAttachment(driveDocumentIdField, 0, SWT.CENTER);
		driveDocumentIdLabelFormData.left = new FormAttachment(0, 0);
		driveDocumentIdLabelFormData.right = new FormAttachment(middle, -margin);
		driveDocumentIdLabel = new Label(shell, SWT.RIGHT);
		driveDocumentIdLabel.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveDocumentId.Label"));
		driveDocumentIdLabel.setLayoutData(driveDocumentIdLabelFormData);
		props.setLook(driveDocumentIdLabel);

		//
		// Drive Sheet
		//
		driveSheetFieldFormData = new FormData();
		driveSheetFieldFormData.top = new FormAttachment(driveDocumentIdField, margin);
		driveSheetFieldFormData.left = new FormAttachment(middle, 0);
		driveSheetFieldFormData.right = new FormAttachment(80, 0);
		driveSheetField = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		driveSheetField.setLayoutData(driveSheetFieldFormData);
		props.setLook(driveSheetField);

		driveSheetRefreshFormData = new FormData();
		driveSheetRefreshFormData.top = new FormAttachment(driveSheetField, 0, SWT.CENTER);
		driveSheetRefreshFormData.bottom = new FormAttachment(driveSheetField, 0, SWT.CENTER);
		driveSheetRefreshFormData.left = new FormAttachment(driveSheetField, 0, SWT.RIGHT);
		driveSheetRefreshFormData.right = new FormAttachment(100, 0);
		driveSheetRefresh = new Button(shell, SWT.PUSH | SWT.LEFT | SWT.BORDER);
		driveSheetRefresh.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveSheet.Search"));
		driveSheetRefresh.setAlignment(SWT.CENTER);
		driveSheetRefresh.setLayoutData(driveSheetRefreshFormData);
		props.setLook(driveSheetRefresh);

		driveSheetLabelFormData = new FormData();
		driveSheetLabelFormData.top = new FormAttachment(driveSheetField, 0, SWT.CENTER);
		driveSheetLabelFormData.bottom = new FormAttachment(driveSheetField, 0, SWT.CENTER);
		driveSheetLabelFormData.left = new FormAttachment(0, 0);
		driveSheetLabelFormData.right = new FormAttachment(middle, -margin);
		driveSheetLabel = new Label(shell, SWT.RIGHT);
		driveSheetLabel.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveSheet.Label"));
		driveSheetLabel.setLayoutData(driveSheetLabelFormData);
		props.setLook(driveSheetLabel);

		//
		// Drive Range
		//
		driveRangeFieldFormData = new FormData();
		driveRangeFieldFormData.top = new FormAttachment(driveSheetField, margin);
		driveRangeFieldFormData.left = new FormAttachment(middle, 0);
		driveRangeFieldFormData.right = new FormAttachment(100, 0);
		driveRangeField = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		driveRangeField.setLayoutData(driveRangeFieldFormData);
		props.setLook(driveRangeField);

		driveRangeLabelFormData = new FormData();
		driveRangeLabelFormData.top = new FormAttachment(driveRangeField, 0, SWT.CENTER);
		driveRangeLabelFormData.bottom = new FormAttachment(driveRangeField, 0, SWT.CENTER);
		driveRangeLabelFormData.left = new FormAttachment(0, 0);
		driveRangeLabelFormData.right = new FormAttachment(middle, -margin);
		driveRangeLabel = new Label(shell, SWT.RIGHT);
		driveRangeLabel.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveRange.Label"));
		driveRangeLabel.setLayoutData(driveRangeLabelFormData);
		props.setLook(driveRangeLabel);

		//
		// Drive Header
		//
		driveHeaderFieldFormData = new FormData();
		driveHeaderFieldFormData.top = new FormAttachment(driveRangeField, margin);
		driveHeaderFieldFormData.left = new FormAttachment(middle, 0);
		driveHeaderFieldFormData.right = new FormAttachment(100, 0);
		driveHeaderField = new Combo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		driveHeaderField.add(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveHeader.Type.None"));
		driveHeaderField.add(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveHeader.Type.Row1"));
		driveHeaderField.add(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveHeader.Type.FirstRow"));
		driveHeaderField.select(0);
		driveHeaderField.setLayoutData(driveHeaderFieldFormData);
		props.setLook(driveHeaderField);

		driveHeaderLabelFormData = new FormData();
		driveHeaderLabelFormData.top = new FormAttachment(driveHeaderField, 0, SWT.CENTER);
		driveHeaderLabelFormData.bottom = new FormAttachment(driveHeaderField, 0, SWT.CENTER);
		driveHeaderLabelFormData.left = new FormAttachment(0, 0);
		driveHeaderLabelFormData.right = new FormAttachment(middle, -margin);
		driveHeaderLabel = new Label(shell, SWT.RIGHT);
		driveHeaderLabel.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveHeader.Label"));
		driveHeaderLabel.setLayoutData(driveHeaderLabelFormData);
		props.setLook(driveHeaderLabel);

		//
		// Drive Empty Lines
		//
		driveEmptyLinesFieldFormData = new FormData();
		driveEmptyLinesFieldFormData.top = new FormAttachment(driveHeaderField, margin);
		driveEmptyLinesFieldFormData.left = new FormAttachment(middle, 0);
		driveEmptyLinesFieldFormData.right = new FormAttachment(100, 0);

		driveEmptyLinesField = new Button(shell, SWT.CHECK | SWT.LEFT | SWT.BORDER);
		driveEmptyLinesField.setLayoutData(driveEmptyLinesFieldFormData);
		props.setLook(driveEmptyLinesField);

		driveEmptyLinesLabelFormData = new FormData();
		driveEmptyLinesLabelFormData.top = new FormAttachment(driveEmptyLinesField, 0, SWT.CENTER);
		driveEmptyLinesLabelFormData.bottom = new FormAttachment(driveEmptyLinesField, 0, SWT.CENTER);
		driveEmptyLinesLabelFormData.left = new FormAttachment(0, 0);
		driveEmptyLinesLabelFormData.right = new FormAttachment(middle, -margin);
		driveEmptyLinesLabel = new Label(shell, SWT.RIGHT);
		driveEmptyLinesLabel.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveEmptyLines.Label"));
		driveEmptyLinesLabel.setLayoutData(driveEmptyLinesLabelFormData);
		props.setLook(driveEmptyLinesLabel);

		//
		// Output table
		//
		int keyWidgetCols = 9;
		int keyWidgetRows = 1;

		ColumnInfo[] ciKeys = new ColumnInfo[keyWidgetCols];
		ciKeys[0] = new ColumnInfo(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.table.column.ValueField"), ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciKeys[1] = new ColumnInfo(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.table.column.Default"), ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciKeys[2] = new ColumnInfo(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.table.column.Type"), ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMeta.getTypes());
		ciKeys[3] = new ColumnInfo(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.table.column.Format"), ColumnInfo.COLUMN_TYPE_FORMAT, 4);
		ciKeys[4] = new ColumnInfo(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.table.column.Length"), ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciKeys[5] = new ColumnInfo(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.table.column.Precision"), ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciKeys[6] = new ColumnInfo(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.table.column.Currency"), ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciKeys[7] = new ColumnInfo(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.table.column.Decimal"), ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciKeys[8] = new ColumnInfo(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.table.column.Group"), ColumnInfo.COLUMN_TYPE_TEXT, false);

		fieldsTable = new TableView(transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, ciKeys, keyWidgetRows, null, props);

		FormData fdReturn = new FormData();
		fdReturn.top = new FormAttachment(driveEmptyLinesField, margin);
		fdReturn.bottom = new FormAttachment(100, -50);
		fdReturn.left = new FormAttachment(0, 0);
		fdReturn.right = new FormAttachment(100, 0);
		fieldsTable.setLayoutData(fdReturn);

		populateDialog();

		wStepname.addListener(SWT.Modify, lsMod);
		wStepname.addSelectionListener(lsDef);
		driveUserField.getTextWidget().addListener(SWT.Modify, lsMod);
		driveUserField.addSelectionListener(lsDef);
		drivePasswordField.getTextWidget().addListener(SWT.Modify, lsMod);
		drivePasswordField.addSelectionListener(lsDef);
		driveDocumentIdField.getTextWidget().addListener(SWT.Modify, lsMod);
		driveDocumentIdField.addSelectionListener(lsDef);
		driveSheetField.getTextWidget().addListener(SWT.Modify, lsMod);
		driveSheetField.addSelectionListener(lsDef);
		driveSheetRefresh.addListener(SWT.Modify, lsMod);
		driveSheetRefresh.addListener(SWT.Selection, new Listener()
		{
			@Override
			public void handleEvent(Event e)
			{
				IntuitivusSpreadsheetStepMeta meta = new IntuitivusSpreadsheetStepMeta();
				getInfo(meta);
				if (meta.hasDataToConnect())
				{
					String title = BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveSheet.Search.Title");
					String description = BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.dialog.DriveSheet.Search.Description");
					EnterSelectionDialog selection = new EnterSelectionDialog(shell, getSheets(meta), title, description);
					selection.setMulti(false);
					String selectedSheet = selection.open();
					if (selectedSheet != null)
						driveSheetField.setText(selectedSheet);
				}
			}
		});
		driveRangeField.getTextWidget().addListener(SWT.Modify, lsMod);
		driveRangeField.addSelectionListener(lsDef);
		driveHeaderField.addListener(SWT.Selection, lsMod);
		driveEmptyLinesField.addListener(SWT.Selection, lsMod);
		fieldsTable.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent arg0)
			{
				lsMod.handleEvent(null);
			}
		});

		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wOK.addListener(SWT.Selection, lsOK);

		wPreview = new Button(shell, SWT.PUSH);
		wPreview.setText(BaseMessages.getString(PKG, "System.Button.Preview"));
		wPreview.addListener(SWT.Selection, lsPreview);

		wGet = new Button(shell, SWT.PUSH);
		wGet.setText(BaseMessages.getString(PKG, "System.Button.GetFields"));
		wGet.addListener(SWT.Selection, lsGet);

		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
		wCancel.addListener(SWT.Selection, lsCancel);

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wGet, wPreview, wCancel }, margin, fieldsTable);

		shell.addShellListener(new ShellAdapter()
		{
			public void shellClosed(ShellEvent e)
			{
				cancel();
			}
		});

		setSize();

		meta.setChanged(changed);

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}

		return stepname;
	}

	private void populateDialog()
	{
		wStepname.selectAll();
		if (meta.getDriveUser() != null)
		{
			driveUserField.setText(meta.getDriveUser());
		}

		if (meta.getDrivePassword() != null)
		{
			drivePasswordField.setText(meta.getDrivePassword());
		}

		if (meta.getDriveDocumentId() != null)
		{
			driveDocumentIdField.setText(meta.getDriveDocumentId());
		}

		if (meta.getDriveRange() != null)
		{
			driveRangeField.setText(meta.getDriveRange());
		}

		if (meta.getDriveSheet() != null)
		{
			driveSheetField.setText(meta.getDriveSheet());
		}

		if (meta.getDriveHeader() != null)
		{
			driveHeaderField.select(meta.getDriveHeader().ordinal());
		}

		driveEmptyLinesField.setSelection(meta.isDriveAcceptEmptyLines());

		populateTable(meta);

	}

	private void populateTable(IntuitivusSpreadsheetStepMeta meta)
	{

		if (meta.getOutputField() != null && meta.getOutputField().length > 0)
		{
			fieldsTable.removeAll();
			fieldsTable.table.setItemCount(meta.getOutputField().length);
			for (int i = 0; i < meta.getOutputField().length; i++)
			{

				TableItem item = fieldsTable.table.getItem(i);

				if (meta.getOutputField()[i] != null)
				{
					item.setText(1, meta.getOutputField()[i]);
				}

				if (meta.getOutputDefault()[i] != null)
				{
					item.setText(2, meta.getOutputDefault()[i]);
				}

				item.setText(3, ValueMeta.getTypeDesc(meta.getOutputType()[i]));

				if (meta.getOutputFormat()[i] != null)
				{
					item.setText(4, meta.getOutputFormat()[i]);
				}
				item.setText(5, meta.getOutputLength()[i] < 0 ? "" : "" + meta.getOutputLength()[i]);
				item.setText(6, meta.getOutputPrecision()[i] < 0 ? "" : "" + meta.getOutputPrecision()[i]);

				if (meta.getOutputCurrency()[i] != null)
				{
					item.setText(7, meta.getOutputCurrency()[i]);
				}

				if (meta.getOutputDecimal()[i] != null)
				{
					item.setText(8, meta.getOutputDecimal()[i]);
				}

				if (meta.getOutputGroup()[i] != null)
				{
					item.setText(9, meta.getOutputGroup()[i]);
				}

			}
		}

		fieldsTable.setRowNums();
		fieldsTable.optWidth(true);
	}

	private void cancel()
	{
		stepname = null;
		meta.setChanged(changed);
		dispose();
	}

	private void ok()
	{
		stepname = wStepname.getText();
		getInfo(meta);
		dispose();
	}

	private void preview()
	{
		IntuitivusSpreadsheetStepMeta meta = new IntuitivusSpreadsheetStepMeta();
		getInfo(meta);

		boolean useDefinedOutput = fieldsTable.table.getItemCount() > 0;
		meta.setAdoptOutput(useDefinedOutput);

		String stepName = wStepname.getText();

		TransMeta previewMeta = TransPreviewFactory.generatePreviewTransformation(transMeta, meta, stepName);

		TransPreviewProgressDialog progressDialog = new TransPreviewProgressDialog(shell, previewMeta, new String[] { stepName }, new int[] { Integer.MAX_VALUE });
		progressDialog.open();

		Trans trans = progressDialog.getTrans();
		String loggingText = progressDialog.getLoggingText();

		if (!progressDialog.isCancelled())
		{
			if (trans.getResult() != null && trans.getResult().getNrErrors() > 0)
			{
				EnterTextDialog etd = new EnterTextDialog(shell, BaseMessages.getString(PKG, "System.Dialog.PreviewError.Title"), BaseMessages.getString(PKG, "System.Dialog.PreviewError.Message"),
						loggingText, true);
				etd.setReadOnly();
				etd.open();
			}
		}

		PreviewRowsDialog prd = new PreviewRowsDialog(shell, transMeta, SWT.NONE, stepName, progressDialog.getPreviewRowsMeta(stepName), progressDialog.getPreviewRows(stepName), loggingText);
		prd.open();
	}

	private void get()
	{

		if (meta.getDriveHeader() == HeaderType.NONE)
			return;

		IntuitivusSpreadsheetStepMeta meta = new IntuitivusSpreadsheetStepMeta();
		getInfo(meta);
		meta.setAdoptOutput(false);
		try
		{

			WorksheetEntry worksheet = SpreadsheetUtil.connectWorksheetFeed(meta.getDriveUser(), meta.getDrivePassword(), meta.getDriveDocumentId(), meta.getDriveSheet());

			Range range = new Range(meta.getDriveRange());
			range.realistic(worksheet);

			Range headerRange = range.getRangeHeader(meta.getDriveHeader());
			Range bodyRange = range.getRangeBody(meta.getDriveHeader());

			IntuitivusSpreadsheetStepData header = new IntuitivusSpreadsheetStepData();
			header.cellFeed = SpreadsheetUtil.connectCellFeed(worksheet, headerRange);
			header.calculateRangeSize(headerRange);
			header.refreshCachedData();

			meta.allocate(header.cols);

			CellEntry[] headerRow = header.getNextCellRow(false);
			for (int i = 0; i < headerRow.length; i++)
			{
				meta.getOutputField()[i] = headerRow[i].getCell().getValue();
			}

			IntuitivusSpreadsheetStepData body = new IntuitivusSpreadsheetStepData();
			body.cellFeed = SpreadsheetUtil.connectCellFeed(worksheet, bodyRange);
			body.calculateRangeSize(bodyRange);
			body.refreshCachedData();

			CellEntry[] bodyRow = body.getNextCellRow(false);
			for (int i = 0; i < bodyRow.length; i++)
			{
				if (bodyRow[i] == null || bodyRow[i].getCell().getNumericValue() == null)
					meta.getOutputType()[i] = ValueMetaInterface.TYPE_STRING;
				else
				{
					if (bodyRow[i].getCell().getValue().contains("/"))
					{
						meta.getOutputType()[i] = ValueMetaInterface.TYPE_DATE;
					} else
					{
						meta.getOutputType()[i] = ValueMetaInterface.TYPE_NUMBER;
					}
				}
			}

			populateTable(meta);
			this.meta.setChanged();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private String[] getSheets(IntuitivusSpreadsheetStepMeta meta)
	{
		try
		{
			WorksheetFeed feed = SpreadsheetUtil.connectWorksheetFeed(meta.getDriveUser(), meta.getDrivePassword(), meta.getDriveDocumentId());
			List<WorksheetEntry> entries = feed.getEntries();
			String[] entriesList = new String[entries.size()];

			for (int i = 0; i < entriesList.length; i++)
			{
				entriesList[i] = entries.get(i).getTitle().getPlainText();
			}

			return entriesList;
		} catch (AuthenticationException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private void getInfo(IntuitivusSpreadsheetStepMeta meta)
	{
		meta.setDriveUser(driveUserField.getText());
		meta.setDrivePassword(drivePasswordField.getText());
		meta.setDriveDocumentId(driveDocumentIdField.getText());
		meta.setDriveRange(driveRangeField.getText());
		meta.setDriveSheet(driveSheetField.getText());
		meta.setDriveHeader(driveHeaderField.getSelectionIndex());
		meta.setDriveAcceptEmpty(driveEmptyLinesField.getSelection());

		int total = fieldsTable.nrNonEmpty();
		meta.allocate(total);

		for (int i = 0; i < total; i++)
		{
			TableItem item = fieldsTable.getNonEmpty(i);
			meta.getOutputField()[i] = item.getText(1);
			meta.getOutputDefault()[i] = item.getText(2);
			meta.getOutputType()[i] = ValueMeta.getType(item.getText(3));

			if (meta.getOutputType()[i] < 0)
			{
				meta.getOutputType()[i] = ValueMetaInterface.TYPE_STRING;
			}

			meta.getOutputFormat()[i] = item.getText(4);
			meta.getOutputLength()[i] = Const.toInt(item.getText(5), -1);
			meta.getOutputPrecision()[i] = Const.toInt(item.getText(6), -1);
			meta.getOutputCurrency()[i] = item.getText(7);
			meta.getOutputDecimal()[i] = item.getText(8);
			meta.getOutputGroup()[i] = item.getText(9);

		}

	}

}
