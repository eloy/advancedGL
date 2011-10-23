 /******* BEGIN LICENSE BLOCK *****
 * Versión: GPL 2.0/CDDL 1.0/EPL 1.0
 *
 * Los contenidos de este fichero están sujetos a la Licencia
 * Pública General de GNU versión 2.0 (la "Licencia"); no podrá
 * usar este fichero, excepto bajo las condiciones que otorga dicha 
 * Licencia y siempre de acuerdo con el contenido de la presente. 
 * Una copia completa de las condiciones de de dicha licencia,
 * traducida en castellano, deberá estar incluida con el presente
 * programa.
 * 
 * Adicionalmente, puede obtener una copia de la licencia en
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Este fichero es parte del programa opensiXen.
 *
 * OpensiXen es software libre: se puede usar, redistribuir, o
 * modificar; pero siempre bajo los términos de la Licencia 
 * Pública General de GNU, tal y como es publicada por la Free 
 * Software Foundation en su versión 2.0, o a su elección, en 
 * cualquier versión posterior.
 *
 * Este programa se distribuye con la esperanza de que sea útil,
 * pero SIN GARANTÍA ALGUNA; ni siquiera la garantía implícita 
 * MERCANTIL o de APTITUD PARA UN PROPÓSITO DETERMINADO. Consulte 
 * los detalles de la Licencia Pública General GNU para obtener una
 * información más detallada. 
 *
 * TODO EL CÓDIGO PUBLICADO JUNTO CON ESTE FICHERO FORMA PARTE DEL 
 * PROYECTO OPENSIXEN, PUDIENDO O NO ESTAR GOBERNADO POR ESTE MISMO
 * TIPO DE LICENCIA O UNA VARIANTE DE LA MISMA.
 *
 * El desarrollador/es inicial/es del código es
 *  FUNDESLE (Fundación para el desarrollo del Software Libre Empresarial).
 *  Indeos Consultoria S.L. - http://www.indeos.es
 *
 * Contribuyente(s):
 *  Eloy Gómez García <eloy@opensixen.org> 
 *
 * Alternativamente, y a elección del usuario, los contenidos de este
 * fichero podrán ser usados bajo los términos de la Licencia Común del
 * Desarrollo y la Distribución (CDDL) versión 1.0 o posterior; o bajo
 * los términos de la Licencia Pública Eclipse (EPL) versión 1.0. Una 
 * copia completa de las condiciones de dichas licencias, traducida en 
 * castellano, deberán de estar incluidas con el presente programa.
 * Adicionalmente, es posible obtener una copia original de dichas 
 * licencias en su versión original en
 *  http://www.opensource.org/licenses/cddl1.php  y en  
 *  http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * Si el usuario desea el uso de SU versión modificada de este fichero 
 * sólo bajo los términos de una o más de las licencias, y no bajo los 
 * de las otra/s, puede indicar su decisión borrando las menciones a la/s
 * licencia/s sobrantes o no utilizadas por SU versión modificada.
 *
 * Si la presente licencia triple se mantiene íntegra, cualquier usuario 
 * puede utilizar este fichero bajo cualquiera de las tres licencias que 
 * lo gobiernan,  GPL 2.0/CDDL 1.0/EPL 1.0.
 *
 * ***** END LICENSE BLOCK ***** */
package es.indeos.osx.finreports.model;

import java.math.BigDecimal;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.compiere.util.Env;
import org.compiere.util.Formater;

/**
 * AccountTreeTableModel 
 *
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 */
public class AccountTreeTableModel implements TreeTableModel {

	protected EventListenerList listenerList = new EventListenerList();
	
	AccountsTree<Account> tree;
	
	
	public AccountTreeTableModel(AccountsTree<Account> tree)	{
		this.tree = tree;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		 listenerList.add(TreeModelListener.class, l);		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		   listenerList.remove(TreeModelListener.class, l);		
	}



	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	@Override
	public Object getChild(Object parent, int index) {		
		return ((AccountsTree<Account>) parent).getChild(index);
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
	public int getChildCount(Object parent) {
		return ((AccountsTree<Account>) parent).getChildCount();	
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((AccountsTree<Account>) parent).getIndexOfChild(parent, child);		
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
	public Object getRoot() {
		return tree;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
	public boolean isLeaf(Object node) {
		AccountsTree<Account> tree = (AccountsTree<Account>) node;
		if (tree.getData() == null)	{
			return false;
		}
		if (tree.getChildCount() > 0) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
		
	}

	
	String[] columns = {"Cuenta", "Inicial", "Debe", "Haber", "Saldo"};
	
	/* (non-Javadoc)
	 * @see es.indeos.osx.finreports.model.TreeTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columns.length;
	}

	/* (non-Javadoc)
	 * @see es.indeos.osx.finreports.model.TreeTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columns[column];		
	}

	/* (non-Javadoc)
	 * @see es.indeos.osx.finreports.model.TreeTableModel#getColumnClass(int)
	 */
	@Override
	public Class getColumnClass(int column) {
		if (column == 0)	{
			return TreeTableModel.class;
		}
		else {
			return BigDecimal.class;
		}
	}

	/* (non-Javadoc)
	 * @see es.indeos.osx.finreports.model.TreeTableModel#getValueAt(java.lang.Object, int)
	 */
	@Override
	public Object getValueAt(Object node, int column) {
		Account acct = ((AccountsTree<Account>) node).getData();
		if (acct == null)	{
			return "";
		}
		switch (column) {
		case 0:
			return acct.getName();
		case 1: {
			return Env.ONE.negate();
		}
		case 2: {
			return Env.ONE.negate();
		}
		case 3: {
			return Env.ONE.negate();
		}
		case 4: {
			if (((AccountsTree<Account>) node).getChildCount() == 0)	{
				return Formater.formatAmt(acct.getBalance());
			}
			else {
				return Formater.formatAmt(acct.getChildsBalance());
			}
		}
			
		default:
			throw new UnsupportedOperationException("Invalid column count");
		}
	}

	/* (non-Javadoc)
	 * @see es.indeos.osx.finreports.model.TreeTableModel#isCellEditable(java.lang.Object, int)
	 */
	@Override
	public boolean isCellEditable(Object node, int column) {
		  return getColumnClass(column) == TreeTableModel.class; 
	}

	/* (non-Javadoc)
	 * @see es.indeos.osx.finreports.model.TreeTableModel#setValueAt(java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public void setValueAt(Object aValue, Object node, int column) {
		// TODO Auto-generated method stub
		
	}

}
