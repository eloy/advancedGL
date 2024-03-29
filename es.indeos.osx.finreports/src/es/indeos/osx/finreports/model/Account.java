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
import java.util.ArrayList;
import java.util.List;

import org.compiere.model.MElementValue;
import org.compiere.util.Env;
import org.opensixen.model.MAccount;
import org.opensixen.model.MFactAcctBalance;

/**
 * Account 
 *
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 */
public class Account {
	
	private String name;
	
	private String title;
	
	private boolean isFolder;
	
	private List<MFactAcctBalance> facts;

	private List<MElementValue> accounts;
	
	private BigDecimal childsBalance = Env.ZERO;

	private String accountType;

	private String accountSign;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}



	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the isFolder
	 */
	public boolean isFolder() {
		return isFolder;
	}

	/**
	 * @param isFolder the isFolder to set
	 */
	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	
	
	/**
	 * @return the accountType
	 */
	public String getAccountType() {
		return accountType;
	}



	/**
	 * @return the accountSign
	 */
	public String getAccountSign() {
		return accountSign;
	}



	/**
	 * Calculate balance based in account type
	 * @param fact
	 * @return
	 */
	private BigDecimal calculateBalance(MFactAcctBalance fact)	{
		if (accountSign.equals("N"))	{
			if (accountType.equals("E") || accountType.equals("N") )	{
				accountSign = "D";
			}
			else {
				accountSign = "C";
			}
		}
		// Credit account
		if (accountSign.equals("C"))	{
				return fact.getAmtAcctCr().subtract(fact.getAmtAcctDr());
		}
		// Debit account
		else {
			return fact.getAmtAcctDr().subtract(fact.getAmtAcctCr());
		}
		
	}
	
	
	public BigDecimal getBalance()	{
		if (facts == null)	{
			return Env.ZERO;
		}
		BigDecimal balance = Env.ZERO;
		
		for (MFactAcctBalance fact:facts)	{
			balance = balance.add(calculateBalance(fact));
		}
		
		return balance;
	}
	
	

	public void addMAccount(MAccount account)	{
		if (accounts == null)	{
			accounts = new ArrayList<MElementValue>();
			name = account.getValue();
			title = account.getName();
			isFolder = account.isSummary();
			accountType = account.getAccountType();
			accountSign = account.getAccountSign();
		}
		accounts.add(account);
	}
	
	public List<MElementValue> getMAccounts()	{
		return accounts;
	}
	
	public void addFact(MFactAcctBalance fact)	{
		if (isFolder())	{
			throw new UnsupportedOperationException("Can't add facts to folder account: " + toString());
		}
		if (facts == null)	{
			facts = new ArrayList<MFactAcctBalance>();
		}
		facts.add(fact);
	}


	/**
	 * @return the childsBalance
	 */
	public BigDecimal getChildsBalance() {
		return childsBalance;
	}


	/**
	 * @param childsBalance the childsBalance to set
	 */
	public void setChildsBalance(BigDecimal childsBalance) {
		this.childsBalance = childsBalance;
	}
	
	/**
	 * Return Account string representation
	 * @return value + " " + name
	 */
	@Override
	public String toString()	{
		return getName() + " " + getTitle();
	}
		
}
