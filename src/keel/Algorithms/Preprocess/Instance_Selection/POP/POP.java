//
//  POP.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 28-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.POP;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;

public class POP extends Metodo {

  public POP (String ficheroScript) {
    super (ficheroScript);
  }
  
  public void ejecutar () {

    int i, j, l;
    int nClases;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel;
    boolean marcas[];
    int weakness[];
    Referencia linea[];
    int minWeak;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Inicialization of structures*/
    marcas = new boolean[datosTrain.length];
    weakness = new int[datosTrain.length];
    linea = new Referencia[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      marcas[i] = true;
      weakness[i] = 0;
    }
    nSel = datosTrain.length;

    /*Body of the POP algorithm. For each attribute, do a resort and mark the instances that are into the intervals that
     create the proyected classes in this dimension. Finally, the marked instances are eliminated.*/
    for (i=0; i<datosTrain[0].length; i++) {
    	/*Proyection to a i dimension*/
    	if (Attributes.getInputAttribute(i).getType() != Attribute.NOMINAL) {
    		for (j=0; j<datosTrain.length; j++) {
    			linea[j] = new Referencia (j,realTrain[j][i]);
    		}

    		/*Quicksort*/
    		Arrays.sort(linea);

    		/*Increment the weakness of interior instances*/
    		for (j=1; j<datosTrain.length-1; j++) {
    			if (clasesTrain[linea[j-1].entero] == clasesTrain[linea[j].entero] && clasesTrain[linea[j+1].entero] == clasesTrain[linea[j].entero])
    				weakness[linea[j].entero] ++;
    		}
    	}
    }

    for (i=0; i<datosTrain[0].length; i++) {
    	/*Proyection to a i dimension*/
    	if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
    		for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) {
    			minWeak = Integer.MAX_VALUE;
    			for (l=0; l<datosTrain.length;l++) {
    				if (nominalTrain[l][i] == j) {
    					if (weakness[l] < minWeak) {
    						minWeak = weakness[l];
    					}
    				}
    			}
    			for (l=0; l<datosTrain.length;l++) {
    				if (nominalTrain[l][i] == j) {
    					if (weakness[l] > minWeak) {
    						weakness[l]++;
    					}
    				}
    			}
    		}
    	}
    }
    
    for (i=0; i<datosTrain.length; i++)
    	if (weakness[i] == datosTrain[0].length) {
    		marcas[i] = false;
    		nSel--;
    	}

    /*Building of the S set from the flags*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (marcas[i]) { //the instance will be copied to the solution
        for (j=0; j<datosTrain[0].length; j++) {
          conjS[l][j] = datosTrain[i][j];
          conjR[l][j] = realTrain[i][j];
          conjN[l][j] = nominalTrain[i][j];
          conjM[l][j] = nulosTrain[i][j];
        }
        clasesS[l] = clasesTrain[i];
        l++;
      }
    }

    System.out.println("POP "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
 }

  public void leerConfiguracion (String ficheroScript) {

    String fichero, linea, token;
    StringTokenizer lineasFichero, tokens;
    byte line[];
    int i, j;

    ficheroSalida = new String[2];

    fichero = Fichero.leeFichero (ficheroScript);
    lineasFichero = new StringTokenizer (fichero,"\n\r");

    lineasFichero.nextToken();
    linea = lineasFichero.nextToken();

    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the names of the training and test files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTraining = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTest = new String (line,i,j-i);

    /*Getting the path and base name of the results files*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the names of output files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[0] = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[1] = new String (line,i,j-i);
  }
}