package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

public class PropertyListDialog extends Dialog {

	protected Shell shell;
	private ChannelTreeByPropertyWidget control;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @wbp.parser.constructor
	 */
	public PropertyListDialog(ChannelTreeByPropertyWidget widget) {
		this(widget.getShell(), SWT.DIALOG_TRIM, "Select properties...");
		this.control = widget;
		addInitialValues("channels", widget.getChannelQuery().getResult().channels);
		addInitialValues("selectedProperties", widget.getProperties());
	}
	
	protected PropertyListDialog(Shell shell, int style, String title) {
		super(shell, style);
		setText(title);
	}
	
	public void open(SelectionEvent evt) {
		System.out.println(evt.getSource() + " " +new Point(evt.x, evt.y) );
		Point point = evt.display.map((Control) evt.getSource(), null, new Point(evt.x, evt.y));
		open(point.x, point.y);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public void open(int x, int y) {
		createContents();
		shell.open();
		shell.layout();
		shell.setBounds(Math.min(x, shell.getDisplay().getClientArea().width - shell.getBounds().width),
				Math.min(y, shell.getDisplay().getClientArea().height - shell.getBounds().height),
				shell.getBounds().width, shell.getBounds().height);
	}
	
	private PropertyListSelectionWidget propertyListSelectionWidget;
	private List<String> initialProperties = new ArrayList<String>();
	private Map<String, Object> initialValues = new HashMap<String, Object>();
	
	protected void addInitialValues(String name, Object value) {
		initialProperties.add(name);
		initialValues.put(name, value);
	}
	
	protected void onPropertyChange(PropertyChangeEvent evt) {
		control.setProperties(getWidget().getSelectedProperties());
	}
	
	@SuppressWarnings("unchecked")
	private void populateInitialValues() {
		propertyListSelectionWidget.setChannels((Collection<Channel>) initialValues.get("channels"));
		propertyListSelectionWidget.setSelectedProperties((List<String>) initialValues.get("selectedProperties"));
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(550, 450);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		propertyListSelectionWidget = new PropertyListSelectionWidget(shell, SWT.NONE);
		FormData fd_propertyListSelectionWidget = new FormData();
		fd_propertyListSelectionWidget.left = new FormAttachment(0);
		fd_propertyListSelectionWidget.right = new FormAttachment(0, 544);
		fd_propertyListSelectionWidget.top = new FormAttachment(0);
		propertyListSelectionWidget.setLayoutData(fd_propertyListSelectionWidget);
		
		Button btnCancel = new Button(shell, SWT.NONE);
		fd_propertyListSelectionWidget.bottom = new FormAttachment(btnCancel, -6);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				populateInitialValues();
				shell.close();
			}
		});
		
		Button btnApply = new Button(shell, SWT.NONE);
		FormData fd_btnApply = new FormData();
		fd_btnApply.bottom = new FormAttachment(btnCancel, 0, SWT.BOTTOM);
		fd_btnApply.right = new FormAttachment(btnCancel, -6);
		btnApply.setLayoutData(fd_btnApply);
		btnApply.setText("Apply");
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		
		populateInitialValues();
		propertyListSelectionWidget.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onPropertyChange(evt);
			}
		});
	}
	
	public PropertyListSelectionWidget getWidget() {
		return propertyListSelectionWidget;
	}
	
}