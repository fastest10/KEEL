/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/*
 * ExcelToKeel.java
 */
package keel.Algorithms.Preprocess.Converter;

import keel.Dataset.*;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jxl.*;

/**
 * <p>
 * <b> ExcelToKeel </b>
 * </p>
 * This class extends from the Importer class. It is used to read 
 * data with Excel format and transform them to the KEEL format.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class ExcelToKeel extends Importer {

    /** ExcelToKeel class Constructor.
     * Initializes the variables that stores the symbols used to identify null 
     * values and separator between data.
     *
     * @param  nullValueUser. Null value symbols.
     * 
     */
    public ExcelToKeel(String nullValueUser) {
        nullValue = nullValueUser;
    }

    /**
     * Method used to transform the data from the excel file given as parameter to 
     * KEEL format file which will be stored in the second file given.
     *
     * @param pathnameInput Excel file path.
     * @param pathnameOutput KEEL file path.
     *
     * @throws Exception if the files can not be read or written.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {
        Pattern p;
        Matcher m;
        String element = new String();
        int i = 0;
        int j = 0;
        int type = -1;
        int actualValueInt;
        int numColumns = 0;
        int numRows = 0;
        double actualValue;
        double min;
        double max;


        File fileInput = new File(pathnameInput);

        try {
            Workbook workbook = Workbook.getWorkbook(new File(pathnameInput));

            Sheet sheet = workbook.getSheet(0);

            numColumns = sheet.getColumns();
            numRows = sheet.getRows();



            /* Leemos la primera linea con los nombres
            de los atributos para obtener el numero de atributos  */

            numAttributes = numColumns;

//Reservamos memoria para almacenar la definiciÃ³n de los atributos y de los datos
            String[][] values = new String[numRows][numColumns];
            attribute = new Attribute[numAttributes];
            data = new Vector[numAttributes];
            types = new Vector[numAttributes];

            for (i = 0; i < numAttributes; i++) {
                attribute[i] = new Attribute();
                data[i] = new Vector();
                types[i] = new Vector();

            }

            for (i = 0; i < numRows; i++) {
                for (j = 0; j < numColumns; j++) {
                    Cell cell = sheet.getCell(j, i);
                    String stringCell = cell.getContents();
                    CellType typeCell = cell.getType();

                    if (typeCell.equals(CellType.NUMBER_FORMULA)){
                        NumberFormulaCell formula = (NumberFormulaCell) cell;
                        stringCell = String.valueOf(formula.getValue());
                    }
                    if (typeCell.equals(CellType.NUMBER)) {
                        stringCell = stringCell.replace(",", ".");
                    }
                    values[i][j] = stringCell;

                }
            }

            workbook.close();

        int initialI = 1;
        if(processHeader){
            // Almacenamos el nombre de los atributos
            for (i = 0; i < numAttributes; i++) {
                element = values[0][i];
                element = element.replace("'", "");
                element = element.replace("\"", "");
                element = element.replace("\n", " ");
                element = element.replace("\r", " ");

                p = Pattern.compile("\\s+");
                m = p.matcher(element);
                element = m.replaceAll(" ");

                // Elimino los espacios en blanco de los nombres de los atributos y pongo en manÃºscula
                // la primera letra de cada palabra.
                if (element.contains(" ")) {
                    StringTokenizer tokenUcfirts = new StringTokenizer(element, " ");
                    String lineUcfirts = "";
                    if (tokenUcfirts.hasMoreTokens()) {
                        lineUcfirts = tokenUcfirts.nextToken();
                    }
                    while (tokenUcfirts.hasMoreTokens()) {
                        lineUcfirts = lineUcfirts.concat(UcFirst(tokenUcfirts.nextToken()));
                    }

                    element = lineUcfirts;

                }


                if (element.equals("") || element.equals("?") || element.equals("<null>") || element.equals(nullValue)) {
                    element = "ATTRIBUTE_" + (i + 1) + "";
                }
                attribute[i].setName(element);
                System.out.println("Attribute " + i + "->" + element);
            }
        }
        else{
            for (i = 0; i < numAttributes; i++) {
                    attribute[i].setName("a" + i);
            }
            initialI = 0;
        }



            for (i = initialI; i < values.length; i++) {
                for (j = 0; j < numAttributes; j++) {
                    element = values[i][j];

                    p = Pattern.compile("^\\s+");
                    m = p.matcher(element);
                    element = m.replaceAll("");

                    p = Pattern.compile("\\s+$");
                    m = p.matcher(element);
                    element = m.replaceAll("");

                    element = element.replace("\"", "");
                    element = element.replace("\n", " ");
                    element = element.replace("\r", " ");

                    if (element.equals("") || element.equals(nullValue) || element.equals("<null>")) {
                        element = "?";
                    }
                    data[j].addElement(element);
                }
            }


// Asignamos el tipo de los atributos
            for (i = 0; i < data[0].size(); i++) {
                for (j = 0; j < numAttributes; j++) {
                    element = (String) data[j].elementAt(i);
                    types[j].addElement(DataType(element));
                }
            }


            for (i = 0; i < numAttributes; i++) {
                if (types[i].contains(NOMINAL)) {
                    attribute[i].setType(NOMINAL);
                } else {
                    if (types[i].contains(REAL)) {
                        attribute[i].setType(REAL);
                    } else {
                        if (types[i].contains(INTEGER)) {
                            attribute[i].setType(INTEGER);
                        } else {
                            attribute[i].setType(-1);
                        }
                    }
                }

            }


            for (i = 0; i < data[0].size(); i++) {

                for (j = 0; j < numAttributes; j++) {

                    element = (String) data[j].elementAt(i);

                    type = attribute[j].getType();


                    if (type == NOMINAL) {
                        p = Pattern.compile("[^A-ZÃa-zÃ±0-9_-]+");
                        m = p.matcher(element);

                        /**
                         * Cambio hecho para que los nominales con espacios en blanco se dejen
                         * con subrayado bajo "_" y sin comillas simples. Se aÃ±ade la siguiente linea
                         */
                        element = element.replace(" ", "_");

                        if (m.find() && !element.startsWith("'") && !element.endsWith("'") && !element.equals("?")) {
                            /**
                             * Cambio hecho para que los nominales con espacios en blanco se dejen
                             * con subrayado bajo "_" y sin comillas simples. Se comenta la siguiente linea
                             */
                            /*
                            //element="'"+element+"'";
                             */
                            data[j].set(i, element);
                        }

                        if (!(attribute[j].isNominalValue(element)) && !element.equals("?")) {
                            attribute[j].addNominalValue(element);
                        }
                        System.out.println("addNominalValue->" + element);

                    }


                    if (type == INTEGER) {
                        if (!element.equals("?")) {
                            actualValueInt = Integer.valueOf(element);
                            data[j].set(i, actualValueInt);

                            if ((attribute[j].getFixedBounds()) == false) {
                                attribute[j].setBounds(actualValueInt, actualValueInt);
                            } else {
                                min = attribute[j].getMinAttribute();
                                max = attribute[j].getMaxAttribute();
                                if (actualValueInt < min) {
                                    attribute[j].setBounds(actualValueInt, max);
                                }
                                if (actualValueInt > max) {
                                    attribute[j].setBounds(min, actualValueInt);
                                }
                            }
                        }

                    }

                    if (type == REAL) {
                        if (!element.equals("?")) {
                            //element=element.replace(".","");
                            actualValue = Double.valueOf(element);
                            data[j].set(i, actualValue);

                            if ((attribute[j].getFixedBounds()) == false) {
                                attribute[j].setBounds(actualValue, actualValue);
                            } else {
                                min = attribute[j].getMinAttribute();
                                max = attribute[j].getMaxAttribute();
                                if (actualValue < min) {
                                    attribute[j].setBounds(actualValue, max);
                                }
                                if (actualValue > max) {
                                    attribute[j].setBounds(min, actualValue);
                                }
                            }
                        }
                    }

                }//end while

            }//end while



            /* Insertamos el nombre de la relaciÃ³n que serÃ¡ el mismo que el del
             * fichero pasado, pero sin extensiÃ³n*/

            nameRelation = fileInput.getName();
            p = Pattern.compile("\\.[A-Za-z]+");
            m = p.matcher(nameRelation);
            nameRelation = m.replaceAll("");

            p = Pattern.compile("\\s+");
            m = p.matcher(nameRelation);
            nameRelation = m.replaceAll("");

// Llamamos a save para que me transforme los datos almacenamos a formato keel
            super.Save(pathnameOutput);


        } catch (Exception e) {

            System.out.println(e);
            System.exit(1);
        }
    }
}//end ExcelToKeel()

