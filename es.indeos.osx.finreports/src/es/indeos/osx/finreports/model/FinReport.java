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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.ValueNamePair;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;



/**
 * FinReport 
 *
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 */
public class FinReport{
	private CLogger log = CLogger.getCLogger(getClass());
	
	private File report;
	private AccountsTree<Account>[] trees;	
	private Set<FinReportLine> reportLines = new  LinkedHashSet<FinReportLine>();
	
	public final static String END_OF_REPORT_STRING = "**END**";
	
	public FinReport(File report, AccountsTree<Account>[] trees)	{
		this.report = report;
		this.trees = trees;		
	}
	
	/**
	 * Load pages from XML report definition
	 * @throws IOException 
	 * @throws  
	 */
	public void loadReportDefinition() throws IOException 	{
		// Load the file.		
		final Sheet sheet = SpreadSheet.createFromFile(report).getSheet(0);
		for (int i=0; i < sheet.getRowCount(); i++)	{			
			String name = sheet.getValueAt(0, i).toString();
			String source = sheet.getValueAt(1, i).toString();						
			// If end of string reached
			if (END_OF_REPORT_STRING.equals(name))	{
				return;
			}
			FinReportLine line = new FinReportLine(name, source);						
			reportLines.add(line);
		}
	}
		
	public FinReportLine[] getLines()	{
		// First make calculations
		for(FinReportLine line:reportLines)	{
			if (!line.isCalculation())	{
				line.calculate(trees);
			}
		}
		
		FinReportLine[] lines = reportLines.toArray(new FinReportLine[reportLines.size()]); 
		makeCalculations(lines);
		
		return lines;
	}
	
	/**
	 * Make calculations in lines
	 * TODO R E F A C T O R!!!!!!!!!!!
	 * @param lines
	 */
	private void makeCalculations(FinReportLine[] lines)	{
		boolean done = true;
		int maxLoop = 10;
		int loopCounter = 0;
		do {
			done = true;
			for(FinReportLine line:reportLines)	{
				if (line.isCalculation()&& !line.isCalculated())	{
					try {
						FinReportColumn[] columns = new FinReportColumn[trees.length];				
						for(int i=0; i < trees.length; i++)	{
							columns[i] = new FinReportColumn();
							// Extract formula and aply  for each column
							BigDecimal value = Env.ZERO;
							String pattern = "([+|-]?[\\d\\w:]*)";
							// Create a Pattern object
							Pattern r = Pattern.compile(pattern);
							Matcher m = r.matcher(line.getSource());
							while (m.find()) {
								String a = m.group();
								if (a.length() == 0)	{
									continue;
								}
								BigDecimal calc = Env.ZERO;
								if (a.contains(":"))	{
									calc = calculateRange(a,i, lines);
								}
								else {
									int index = new Integer(a.replaceAll("\\D", "")) -1;
									if (index > lines.length || index < 0)	{
										throw new CalculationException("Invalid index in " + line.getSource());
									}
									if (lines[index].isCalculation() && !lines[index].isCalculated())	{
										throw new CalculationException("Waiting for " + lines[index].getName());
									}
									if (!lines[index].isIgnored())	{
										calc = lines[index].getColumns()[i].getBalance();
									}
								}
								if (a.startsWith("-"))	{
									calc = calc.negate();
								}
								value = value.add(calc);
							}
							columns[i].setBalance(value);
						}
						line.setColumns(columns);
						line.setCalculated(true);
					}
					catch (CalculationException e) {
						done = false;
					}
				}			
			}
			loopCounter++;
		} while (done == false || loopCounter < maxLoop);
	}
	
	/**
	 * Make calculation in ranges
	 * 
	 * @param formula
	 * @param column
	 * @param lines
	 * @return
	 * @throws CalculationException
	 */
	private BigDecimal calculateRange(String formula, int column, FinReportLine[] lines) throws CalculationException	{
		String[] range = formula.split(":");
		int from = new Integer(range[0].replaceAll("\\D", "")) - 1;
		int to = new Integer(range[1].replaceAll("\\D", "")) - 1;
		BigDecimal value = Env.ZERO;
		for (int i = from; i <= to; i++)	{
			if (lines[i].isCalculation() && !lines[i].isCalculated())	{
				throw new CalculationException("Waiting for " + lines[i].getName());
			}
			if (!lines[i].isIgnored())	{				
				value = value.add(lines[i].getColumns()[column].getBalance());
			}
		}
		return value;
	}

	/**
	 * Return all reports installed on the system
	 * TODO Rewrite this stuff
	 * @return
	 */
	public static ValueNamePair[] getavailableReports()	{
		ValueNamePair[] reports = {
				new ValueNamePair("reports/PGC2008_C_Resultados_Abreviado.ods", "Cuenta de resultados (abreviado)"),
				new ValueNamePair("reports/PGC2008_C_Resultados_Normal.ods", "Cuenta de resultados (normal)"),
				new ValueNamePair("reports/PGC2008_C_Resultados_PYME.ods", "Cuenta de resultados (PYMES)"),
				new ValueNamePair("reports/PGC2008_Ingresos_Y_Gastos_Abreviado.ods", "Ingresos y gastos (abreviado)"),
				new ValueNamePair("reports/PGC2008_Ingresos_Y_Gastos_Normal.ods", "Ingresos y gastos (normal)"),
				new ValueNamePair("reports/PGC2008_Situacion_Abreviado.ods", "Situacion (abreviado)"),
				new ValueNamePair("reports/PGC2008_Situacion_Normal.ods", "Situacion (normal)"),
				new ValueNamePair("reports/PGC2008_Situacion_PYME.ods", "Situacion (PYME)")				
				};				       
		return reports;
	}
	
}

class CalculationException extends RuntimeException{

	public CalculationException(String message) {
		super(message);
	}
	
}