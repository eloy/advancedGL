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

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.compiere.util.Env;


/**
 * FinReport 
 *
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 */
public class FinReport{
	int index = 0;
	private FinreportType report;
	private PageType[] pages;
	private ArrayList<LineType> lines;
	private AccountsTree<Account>[] trees;
	
	private Set<FinReportLine> reportLines = new  LinkedHashSet<FinReportLine>();
	
	public FinReport(AccountsTree<Account>[] trees)	{
		this.trees = trees;
		loadPages();
	}
	
	/**
	 * Load pages from XML report definition
	 */
	private void loadPages()	{
		lines = new ArrayList<LineType>();
		
		report = loadReport();
		pages = report.getPageArray();
		
		for (PageType page:pages)	{
			for (LineType line:page.getLineArray())	{
				lines.add(line);				
			}
		}
	}
	

	
	private FinreportType loadReport()	{
		try {
			// URL xmlreport = BundleProxyClassLoader.getSystemResource("xml/balance.xml");
			String path = "/home/harlock/git/advancedGL/es.indeos.osx.finreports/xml/balance.xml";
			File f = new File(path);
			
			FinreportDocument doc = FinreportDocument.Factory.parse(f);
			return doc.getFinreport();
			
		}catch (Exception e)	{
			e.printStackTrace();
			return null;
		}
	}
	
	
	public FinReportLine[] getLines()	{
		for (PageType page:pages)	{
			for (LineType line:page.getLineArray())	{
				FinReportLine rLine = new FinReportLine();
				rLine.setName(line.getText());
				
				// Calculate lines for each tree
				FinReportColumn[] cols = new FinReportColumn[trees.length];
				for (int i=0; i < trees.length; i++)	{
					FinReportColumn col = new FinReportColumn();
					col.setSources(getSources(line, trees[i]));
					cols[i] = col;
				}
				rLine.setColumns(cols);
				reportLines.add(rLine);
			}
		}
		
		return reportLines.toArray(new FinReportLine[reportLines.size()]);
	}
	
	
	/**
	 * Get sources Accounts for this line
	 * 
	 * @param line
	 * @param tree
	 * @return
	 */
	public List<Account> getSources(LineType line, AccountsTree<Account> tree)	{
		List<Account> sources = new ArrayList<Account>(); 
		for (String a:line.getAccountArray())	{			
			String acct=a.replaceAll("\\D", "");
			AccountsTree<Account> child = tree.getChild(acct);
			if (child != null)	{
				sources.add(child.getData());
			}
		}		
		return sources;
	}
}
