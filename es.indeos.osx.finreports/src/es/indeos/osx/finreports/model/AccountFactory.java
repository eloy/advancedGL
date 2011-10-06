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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.opensixen.model.MAccount;
import org.opensixen.model.MFactAcctBalance;

/**
 * AccountFactory 
 *
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 */
public class AccountFactory {

	public static Collection<Account> get()	{
		Date start = new Date();
		AccountFactory factory = new AccountFactory();
		factory.load();
		factory.loadFacts();
		long diff = new Date().getTime() - start.getTime();
		System.out.println("Tiempo empleado: " + diff);
		
		return factory.getSorted();
	}
	
	
	private HashMap<String, Account> cache;
	private List<MAccount> m_accounts;
	
	private AccountFactory()	{			
	}
	
	private void load()	{		
		// Init caches
		cache = new HashMap<String, Account>();
		// Load accounts
		m_accounts = MAccount.getAccounts();					
		for(MAccount m_acct:m_accounts)	{
			Account acct = cache.get(m_acct.getValue());
			if (acct == null)	{
				acct = new Account();
				acct.addMAccount(m_acct);				
				cache.put(m_acct.getValue(), acct);				
			}
			else {
				acct.addMAccount(m_acct);
			}
		}
	}
	
	/**
	 * Load facts and insert into account representation
	 */
	private void loadFacts()	{
		// and facts	
		List<MFactAcctBalance> facts = MFactAcctBalance.getFacts();
		for(MFactAcctBalance m_fact:facts)	{
			String name = getAcctName(m_fact.getAccount_ID());
			Account acct = cache.get(name);
			if (acct == null)	{
				throw new UnsupportedOperationException("Account with value=" + name + " not found." );	
			}
			acct.addFact(m_fact);
		}
	}
	
	/**
	 * Return the name of the account with this id
	 * @param id
	 * @return
	 */
	private String getAcctName(int id)	{
		for (MAccount acct:m_accounts)	{
			if (acct.getC_ElementValue_ID() == id)	{
				return acct.getValue();
			}
		}
		throw new UnsupportedOperationException("Account with id=" + id + " not found." );
	}
	
	private List<Account> getSorted() {
		SortedSet<String> sortedset = new TreeSet<String>(cache.keySet());
		ArrayList<Account> accounts = new ArrayList<Account>();
		Iterator<String> it = sortedset.iterator();
		while (it.hasNext()) {
			accounts.add(cache.get(it.next()));
		}
		return accounts;

	}
}
