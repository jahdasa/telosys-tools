package org.telosys.tools.eclipse.plugin.wizards.dataset;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/*
 * Classe implémentant le tableau éditable de critère statiques.
 * Les éléments de ce tableau sont des DatasetStaticCritTableItem.
 */
public class DatasetStaticCritTable extends Composite {

  private static final String ID_PROPERTY = "id";

  private static final String TYPE_PROPERTY = "type";  
  
  private TableViewer viewer;

  public DatasetStaticCritTable(Composite parent) {
    super(parent, SWT.BORDER);
    buildControls();
  }

  /**
   * Construit la table et le TableViewer correspondant
   * Le TableViewer permet l'édition des cases du tableau
   */
  protected void buildControls() {
    FillLayout compositeLayout = new FillLayout();
    setLayout(compositeLayout);

    final Table table = new Table(this, SWT.FULL_SELECTION);
    viewer = buildAndLayoutTable(table);

    attachContentProvider(viewer);
    attachLabelProvider(viewer);
    attachCellEditors(viewer, table);
  }

  /**
   * Ajout d'un item du tableau
   * @param item
   */
  public void addInput(DatasetStaticCritTableItem item) {
	  viewer.add(item);
  }
  
  /**
   * Suppression d'un item du tableau
   * @param item
   */
  public void removeInput(DatasetStaticCritTableItem item) {
	  viewer.remove(item);
  }
  
  /**
   * Supprime in item à la position i
   * @param item
   */
  public void removeInput(int i) {
	  viewer.remove(viewer.getElementAt(i));
  }
  
  /**
   * Renvoie un tableau du contenu du tableau
   * @return
   */
  public DatasetStaticCritTableItem[] getItems() {
	  int nbItem = viewer.getTable().getItemCount();
	  DatasetStaticCritTableItem[] arrayItems = new DatasetStaticCritTableItem[nbItem];
	  for (int i = 0; i < nbItem; i++) {
		  arrayItems[i] = (DatasetStaticCritTableItem)viewer.getElementAt(i);
	  }
	  return arrayItems;
  }
  
  private void attachLabelProvider(TableViewer viewer) {
	  
    viewer.setLabelProvider(new ITableLabelProvider() {
      public Image getColumnImage(Object element, int columnIndex) {
        return null;
      }

      public String getColumnText(Object element, int columnIndex) {
        switch (columnIndex) {
        case 0:
          return ""+((DatasetStaticCritTableItem) element).iId;
        case 1:
          return DatasetConst.ARRAY_TYPES[((DatasetStaticCritTableItem) element).iType];
        default:
          return "Invalid column: " + columnIndex;
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

  private TableViewer buildAndLayoutTable(final Table table) {
    TableViewer tableViewer = new TableViewer(table);
    
    TableLayout layout = new TableLayout();
    layout.addColumnData(new ColumnWeightData(20, 50, true));
    layout.addColumnData(new ColumnWeightData(65, 65, true));
    table.setLayout(layout);
    
    TableColumn idColumn = new TableColumn(table, SWT.CENTER);
    idColumn.setText("Parameter");
    //TableColumn typeColumn = new TableColumn(table, SWT.CENTER);
    TableColumn typeColumn = new TableColumn(table, SWT.LEFT);
    typeColumn.setText("Type");
    
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    
    return tableViewer;
  }

  private void attachCellEditors(final TableViewer viewer, Composite parent) {
    viewer.setCellModifier(new ICellModifier() {
      public boolean canModify(Object element, String property) {
        return true;
      }

      public Object getValue(Object element, String property) {
        if(ID_PROPERTY.equals(property)) return new Integer(((DatasetStaticCritTableItem) element).iId);
        if(TYPE_PROPERTY.equals(property)) return new Integer(((DatasetStaticCritTableItem) element).iType);
        return null;
      }

      public void modify(Object element, String property, Object value) {
        TableItem tableItem = (TableItem) element;
        DatasetStaticCritTableItem data = (DatasetStaticCritTableItem)tableItem.getData();
        if(ID_PROPERTY.equals(property)) data.iId = ((Integer)value).intValue();
        if(TYPE_PROPERTY.equals(property)) data.iType = ((Integer)value).intValue();
      
        viewer.refresh(data);
      }
    });

    viewer.setCellEditors(new CellEditor[] {null, 
    		new ComboBoxCellEditor(parent, DatasetConst.ARRAY_TYPES, SWT.READ_ONLY) });

    viewer.setColumnProperties(new String[] { ID_PROPERTY, TYPE_PROPERTY });
  }

}