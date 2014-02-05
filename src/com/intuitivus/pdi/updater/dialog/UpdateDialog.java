package com.intuitivus.pdi.updater.dialog;

import static org.eclipse.swt.SWT.NONE;
import static org.pentaho.di.core.Const.FORM_MARGIN;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;

import com.intuitivus.pdi.updater.PluginUpdater;
import com.intuitivus.pdi.updater.callbacks.PluginUpdaterVersionCheckCallback;
import com.intuitivus.pdi.updater.callbacks.PluginUpdaterVersionDownloadCallback;

public class UpdateDialog extends Dialog
{
	private static Class<?> PKG = UpdateDialog.class;
	private static final int WIDTH = 250;

	private Display display;
	private Shell parent, shell;
	private PropsUI props;

	private PluginUpdater pluginUpdater;

	private ProgressBar progressBar;

	private Button downloadButton;
	private FormData progressBarFormData;

	public UpdateDialog(Shell parent, Properties version)
	{
		super(parent, SWT.NONE);
		this.parent = parent;
		this.display = parent.getDisplay();

		props = PropsUI.getInstance();

		PluginUpdaterVersionCheckCallback versionCheck = new PluginUpdaterVersionCheckCallback()
		{

			@Override
			public void onNewVersion(Properties newversion)
			{
				display.asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						open();
					}
				});
			}

		};

		PluginUpdaterVersionDownloadCallback downloadCheck = new PluginUpdaterVersionDownloadCallback()
		{

			@Override
			public void onData(Properties version, final int lengthReaded, final int lengthTotal)
			{
				display.asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						progressBar.setMinimum(0);
						progressBar.setMaximum(lengthTotal);
						progressBar.setSelection(lengthReaded);
					}
				});
			}

			@Override
			public void onFinish(Properties version)
			{
				display.asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						shell.close();
					}
				});
			}

			@Override
			public void onDataFinish(Properties version)
			{
				display.asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						progressBar = new ProgressBar(shell, SWT.INDETERMINATE);
						props.setLook(progressBar);
						progressBar.setLayoutData(progressBarFormData);
					}
				});
			}

		};

		pluginUpdater = new PluginUpdater(version, versionCheck, downloadCheck);
	}

	public void open()
	{

		Display display = parent.getDisplay();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN | NONE);
		shell.setImage(GUIResource.getInstance().getImageSpoon());
		shell.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.updater.dialog.title") + pluginUpdater.getRemote().getProperty("version"));
		props.setLook(shell);

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;
		shell.setLayout(formLayout);

		Label text = new Label(shell, SWT.WRAP);
		text.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.updater.dialog.intro"));
		props.setLook(text);
		FormData textFormData = new FormData();
		textFormData.left = new FormAttachment(0, 0);
		textFormData.right = new FormAttachment(100, 0);
		textFormData.top = new FormAttachment(0, 0);
		text.setLayoutData(textFormData);

		downloadButton = new Button(shell, SWT.PUSH);
		props.setLook(downloadButton);
		downloadButton.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.updater.dialog.download"));
		FormData downloadButtonFormData = new FormData();
		downloadButtonFormData.top = new FormAttachment(text, Const.FORM_MARGIN);
		downloadButtonFormData.left = new FormAttachment(0, 0);
		downloadButtonFormData.right = new FormAttachment(100, 0);
		downloadButton.setLayoutData(downloadButtonFormData);
		downloadButton.setVisible(true);
		downloadButton.addListener(SWT.Selection, new Listener()
		{
			@Override
			public void handleEvent(Event e)
			{
				downloadButton.setVisible(false);
				progressBar.setVisible(true);
				pluginUpdater.download();
			}
		});

		progressBar = new ProgressBar(shell, SWT.HORIZONTAL);
		props.setLook(progressBar);
		progressBarFormData = new FormData();
		progressBarFormData.top = new FormAttachment(downloadButton, 0, SWT.CENTER);
		progressBarFormData.left = new FormAttachment(0, 0);
		progressBarFormData.right = new FormAttachment(100, 0);
		progressBarFormData.bottom = new FormAttachment(downloadButton, 0, SWT.CENTER);
		progressBar.setLayoutData(progressBarFormData);
		progressBar.setVisible(false);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setSelection(0);

		Label restart = new Label(shell, SWT.WRAP);
		restart.setText(BaseMessages.getString(PKG, "com.intuitivus.pdi.updater.dialog.restart"));
		props.setLook(restart);
		FormData restartFormData = new FormData();
		restartFormData.left = new FormAttachment(0, 0);
		restartFormData.right = new FormAttachment(100, 0);
		restartFormData.top = new FormAttachment(downloadButton, Const.FORM_MARGIN);
		restart.setLayoutData(restartFormData);

		Point computeSize = shell.computeSize(WIDTH, SWT.DEFAULT, true);
		shell.setSize(new Point(WIDTH, computeSize.y));

		Point size = shell.getSize();
		Point parentLoc = parent.getLocation();
		Point parentSize = parent.getSize();
		Rectangle clientArea = display.getClientArea();

		int right = parentLoc.x + parentSize.x + Const.FORM_MARGIN;
		int left = parentLoc.x - FORM_MARGIN - size.x;

		if (left > 0)
		{
			shell.setLocation(left, parentLoc.y);
		} else if (right + size.x < clientArea.width)
		{
			shell.setLocation(right, parentLoc.y);
		}

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}

	}

	public void checkVersion()
	{
		pluginUpdater.checkVersion();
	}

}
