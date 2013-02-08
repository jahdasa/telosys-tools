package org.telosys.tools.eclipse.plugin.wizards.vobean;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;

public class VOAttributeTable
{

	private static final String FIRST_COL_PROPERTY = "firstcol";

	private static final String ATTRIBUTE_PROPERTY = "attribute";

	private static final String TYPE_PROPERTY      = "type";

	private static final String INITIAL_VALUE_PROPERTY = "initvalue";

	private static final String GETTER_PROPERTY = "getter";

	private static final String SETTER_PROPERTY = "setter";

	private TableViewer viewer;

	// public VOAttributeTable(Composite parent, NewVOWizard wizard)
	public VOAttributeTable(Composite parent, GridData gd) 
	{
		//super(parent, SWT.BORDER);
		//buildControls();
		buildControls(parent, gd);
	}

	/**
	 * Construit la table et le TableViewer correspondant Le TableViewer permet
	 * l'édition des cases du tableau
	 */
	protected void buildControls(Composite parent, GridData gd) 
	{
//		FillLayout compositeLayout = new FillLayout();
//		setLayout(compositeLayout);

//		final Table table = new Table(this, SWT.FULL_SELECTION);
		int iTableStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL 
		| SWT.FULL_SELECTION 
		//| SWT.HIDE_SELECTION | SWT.CHECK 
		;

		final Table table = new Table(parent, iTableStyle);
		
		table.setLayoutData(gd);
		
		viewer = buildAndLayoutTable(table);

		attachContentProvider(viewer);
		attachLabelProvider(viewer);
		attachCellEditors(viewer, table);
	}

	/**
	 * Ajout d'un item dans le tableau
	 * 
	 * @param item
	 */
	public void addItem(VOAttributeTableItem item) {
		viewer.add(item);
		viewer.getTable().select(viewer.getTable().getItemCount() - 1);
		viewer.getTable().setFocus();
	}

	/**
	 * Remove the current item from the table
	 * 
	 */
	public void removeItem() 
	{
		Table table = viewer.getTable();
		int position = table.getSelectionIndex() ;
		if ( position >= 0 ) {
			viewer.remove( viewer.getElementAt(position) );
			if ( position < table.getItemCount() ) {
				table.select(position);
				table.setFocus();
			}
			else {
				position--;
				if ( position < table.getItemCount() ) {
					table.select(position);
					table.setFocus();
				}
			}
		}
	}

	/**
	 * Insert the given item in the table before the current position
	 * 
	 * @param item
	 */
	public void insertItem(VOAttributeTableItem item) 
	{
		Table table = viewer.getTable();
		int position = table.getSelectionIndex() ;
		if ( position >= 0 ) {
			viewer.insert(item, position);
			table.select(position);
			table.setFocus();
		}
	}

	/**
	 * Renvoie un tableau du contenu du tableau
	 * 
	 * @return
	 */
	public VOAttributeTableItem[] getItems() {
		int nbItem = viewer.getTable().getItemCount();
		VOAttributeTableItem[] arrayItems = new VOAttributeTableItem[nbItem];
		for (int i = 0; i < nbItem; i++) {
			arrayItems[i] = (VOAttributeTableItem) viewer.getElementAt(i);
		}
		return arrayItems;
	}

	private void attachLabelProvider(TableViewer viewer) {
		viewer.setLabelProvider(new VOAttributeTableLabelProvider());

		viewer.setLabelProvider(new ITableLabelProvider() {
			// Names of images used to represent checkboxes
			// public static final String CHECKED_IMAGE = "checked";
			// public static final String UNCHECKED_IMAGE = "unchecked";

			// For the checkbox images
			// private ImageRegistry imageRegistry = new ImageRegistry();

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return ((VOAttributeTableItem) element).sFirstCol;
				case 1:
					return ((VOAttributeTableItem) element).sAttributeName;
				case 2:
					//return VOConst.ARRAY_OF_JAVA_TYPES[((VOAttributeTableItem) element).iType];
					return VOWizardUtil.getTypeText( ((VOAttributeTableItem) element).iType ) ;
				case 3:
					return ((VOAttributeTableItem) element).sInitialValue;
				case 4:
					return (((VOAttributeTableItem) element).bGetter) ? "X"
							: "";
				case 5:
					return (((VOAttributeTableItem) element).bSetter) ? "X"
							: "";
				default:
					return "";
				}
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener lpl) {
			}
		});

	}

	private void attachContentProvider(TableViewer viewer) {
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});
	}

	private TableViewer buildAndLayoutTable(final Table table) 
	{
		TableViewer tableViewer = new TableViewer(table);

		TableLayout layout = new TableLayout();
		// layout.addColumnData(new ColumnWeightData(10, 10, true));
		// layout.addColumnData(new ColumnWeightData(65, 65, true));
		// layout.addColumnData(new ColumnWeightData(65, 65, true));
		// layout.addColumnData(new ColumnWeightData(65, 65, true));
		// layout.addColumnData(new ColumnWeightData(30, 30, true));
		// layout.addColumnData(new ColumnWeightData(30, 30, true));

		layout.addColumnData(new ColumnPixelData(20, false));

		layout.addColumnData(new ColumnPixelData(180, true)); // Name
		layout.addColumnData(new ColumnPixelData(180, true)); // Type
		layout.addColumnData(new ColumnPixelData(120, true)); // Init value
		layout.addColumnData(new ColumnPixelData(50, false)); // Getter
		layout.addColumnData(new ColumnPixelData(50, false)); // Setter
		table.setLayout(layout);

		// --- Define columns
		TableColumn firstColumn = new TableColumn(table, SWT.LEFT);
		firstColumn.setText("¤");
		firstColumn.setResizable(false);

		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");

		TableColumn typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText("Type");

		TableColumn valueColumn = new TableColumn(table, SWT.LEFT);
		valueColumn.setText("Init Value");

		TableColumn getterColumn = new TableColumn(table, SWT.CENTER);
		getterColumn.setText("Getter");
		getterColumn.setResizable(false);

		TableColumn setterColumn = new TableColumn(table, SWT.CENTER);
		setterColumn.setText("Setter");
		setterColumn.setResizable(false);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		return tableViewer;
	}

	private void attachCellEditors(final TableViewer viewer, Composite parent) 
	{
		viewer.setCellModifier(new ICellModifier() {

			public boolean canModify(Object element, String property) {
				return true;
			}

			public Object getValue(Object element, String property) {
				if (ATTRIBUTE_PROPERTY.equals(property))
					return ((VOAttributeTableItem) element).sAttributeName;
				if (TYPE_PROPERTY.equals(property))
					return new Integer(((VOAttributeTableItem) element).iType);
				if (INITIAL_VALUE_PROPERTY.equals(property))
					return ((VOAttributeTableItem) element).sInitialValue;
				if (GETTER_PROPERTY.equals(property))
					return new Boolean(((VOAttributeTableItem) element).bGetter);
				if (SETTER_PROPERTY.equals(property))
					return new Boolean(((VOAttributeTableItem) element).bSetter);

				return null;
			}

			public void modify(Object element, String property, Object value) {
				TableItem tableItem = (TableItem) element;

				VOAttributeTableItem data = (VOAttributeTableItem) tableItem
						.getData();

				if (ATTRIBUTE_PROPERTY.equals(property))
					data.sAttributeName = (String) value;
				if (TYPE_PROPERTY.equals(property))
					data.iType = ((Integer) value).intValue();
				if (INITIAL_VALUE_PROPERTY.equals(property))
					data.sInitialValue = (String) value;
				if (GETTER_PROPERTY.equals(property))
					data.bGetter = ((Boolean) value).booleanValue();
				if (SETTER_PROPERTY.equals(property))
					data.bSetter = ((Boolean) value).booleanValue();

				PluginLogger.log("Getter : " + data.bGetter);
				PluginLogger.log("Setter : " + data.bSetter);

				viewer.refresh(data);
			}
		});

		//----- Create the cell editors and properties names
		int numberOfColumns = 6 ;
		CellEditor[] editors    = new CellEditor[numberOfColumns];
		String[]     properties = new String    [numberOfColumns];
		
		int i=0 ;
		//--- First column : No editor 
		properties[i] = FIRST_COL_PROPERTY ;
		editors[i] = null;
		i++;
		
		//--- Attribute Name : Text editor 
		properties[i] = ATTRIBUTE_PROPERTY ;
		editors[i] = new TextCellEditor(parent, SWT.NONE);
		i++;
		
		//--- Attribute Java Type : Combo Box editor 
		properties[i] = TYPE_PROPERTY ;
		//ComboBoxCellEditor cbEditor = new ComboBoxCellEditor(parent, VOConst.ARRAY_OF_JAVA_TYPES, SWT.READ_ONLY);
		ComboBoxCellEditor cbEditor = new ComboBoxCellEditor(parent, VOWizardUtil.getJavaTypeTexts(), SWT.READ_ONLY);
		Control control = cbEditor.getControl();
		if ( control instanceof CCombo )
		{
			CCombo combo = (CCombo) control ;
			combo.setVisibleItemCount(10);
		}
		editors[i] = cbEditor ;
		i++;
		
		//--- Initial value 
		properties[i] = INITIAL_VALUE_PROPERTY ;
		editors[i] = new TextCellEditor(parent, SWT.NONE);
		i++;
		
		//--- Flag / Getter
		properties[i] = GETTER_PROPERTY ;
		editors[i] = new CheckboxCellEditor(parent, SWT.CHECK);
		i++;
		
		//--- Flag / Setter
		properties[i] = SETTER_PROPERTY ;
		editors[i] = new CheckboxCellEditor(parent, SWT.CHECK);
		i++;
		
		viewer.setColumnProperties(properties);
		viewer.setCellEditors(editors);
		
//		viewer.setCellEditors(new CellEditor[] {
//				null,
//				new TextCellEditor(parent, SWT.NONE),
//				new ComboBoxCellEditor(parent, VOConst.ARRAY_TYPES,SWT.READ_ONLY), 
//				new TextCellEditor(parent, SWT.NONE),
//				new CheckboxCellEditor(parent, SWT.CHECK),
//				new CheckboxCellEditor(parent, SWT.CHECK) });
//
//		viewer.setColumnProperties(new String[] { FIRST_COL_PROPERTY,
//				ATTRIBUTE_PROPERTY, TYPE_PROPERTY, INITIAL_VALUE_PROPERTY,
//				GETTER_PROPERTY, SETTER_PROPERTY });
	}

}