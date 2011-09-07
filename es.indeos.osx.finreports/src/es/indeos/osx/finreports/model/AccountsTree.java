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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ElementsTree 
 *
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 */
public class AccountsTree <T extends Account> implements Visitable<T> {
	
	// NB: LinkedHashSet preserves insertion order
    private final Set<AccountsTree<T>> children = new LinkedHashSet<AccountsTree<T>>();
    private final T data;

    /**
     * Return a sorted accounts tree
     */
	public static void getElementTree()	{
		AccountsTree<Account> forest = new AccountsTree<Account>(null);		
								
	    for (Account element : Account.getAccounts()) {
	    	forest.child(element);
	    }
	    forest.accept(new PrintIndentedVisitor(0));
	}
	    
	/**
	 * Simple constructor
	 * @param data
	 */
	public AccountsTree(T data) {
        this.data = data;
    }

	/**
	 * Accept a visitor
	 */
    public void accept(Visitor<T> visitor) {
        visitor.visitData(this, data);

        for (AccountsTree<T> child : children) {
            Visitor<T> childVisitor = visitor.visitTree(child);
            child.accept(childVisitor);
        }
    }
    
    /**
     * Add a new child into the forest 
     * @param data
     * @return
     */
    public AccountsTree<T> child(T data) {      
    	
    	// Follow all childrens of this element
    	// if start with like the name of the account, 
    	// then deep inside
    	for (AccountsTree<T> child: children ) {
            if (data.getName().startsWith(child.data.getName())) {
                return child.child(data);
            }
        }
    	// If no childres starts, is because the new 
    	// accoutn must be set inside
    	return  child(new AccountsTree<T>(data));    	       
    }

    /**
     * Add a tree into the forest
     * @param child
     * @return
     */
    public AccountsTree<T> child(AccountsTree<T> child) {
        children.add(child);
        return child;
    }
}

/**
 * Test visitor
 * PrintIndentedVisitor 
 *
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 */
class PrintIndentedVisitor implements Visitor<Account> {

    private final int indent;

    PrintIndentedVisitor(int indent) {
        this.indent = indent;
    }
      
    /* (non-Javadoc)
	 * @see es.indeos.osx.finreports.model.Visitor#visitData(es.indeos.osx.finreports.model.ElementsTree)
	 */
    public void visitData(AccountsTree<Account> parent, Account data) {
        if (data == null )	{
        	return;
        }
    	for (int i = 0; i < indent; i++) { // TODO: naive implementation
            System.out.print(" ");
        }
       
       System.out.println(data.getName());
              
    }

	/* (non-Javadoc)
	 * @see es.indeos.osx.finreports.model.Visitor#visitTree(es.indeos.osx.finreports.model.ElementsTree)
	 */
	@Override
	public Visitor<Account> visitTree(AccountsTree<Account> tree) {
		 return new PrintIndentedVisitor(indent + 2);
	}
}

