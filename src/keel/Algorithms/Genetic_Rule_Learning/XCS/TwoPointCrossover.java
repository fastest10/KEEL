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



package keel.Algorithms.Genetic_Rule_Learning.XCS;
import  keel.Algorithms.Genetic_Rule_Learning.XCS.KeelParser.Config;
import java.lang.*;
import java.io.*;
import java.util.*;

/**
 * <p> Implements the two point crossover.
 * @author Written by Albert Orriols (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */
public class TwoPointCrossover implements Crossover{

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Creates an object of the class. It does not have to initialize anything.
 * </p>
 */
  public  TwoPointCrossover() {        
  } // end TwoPointCrossover        

/**
 * <p>
 * Applies crossover. It generates two children.
 * </p>
 * @param parent1 is the first parent.
 * @param parent2 is the second parent
 * @param child1 is the first child
 * @param child2 is the second child.
 */
  public void makeCrossover(Classifier parent1, Classifier parent2, Classifier child1, Classifier child2) {        
    int i=0;

   // cross1 is a number between [0.. clLength-1]
    int cross1 = (int) (Config.rand() * (double)Config.clLength);
    // cross2 is a number between [1..clLenght]. So, it's forced that the second cross point
    // cannot be in the first allele of classifier
    int cross2 = (int) (Config.rand() * (double)Config.clLength) + 1;


    if (cross1 > cross2){
            int aux = cross2;
            cross2 = cross1;
            cross1 = aux;
            //In the else-if condition is not necessary to check if (cross2<clLength) 
            // to increment the point, because cross1 [0..length-1]
    }else if(cross1 == cross2 ) cross2++;


    // All the intervals (real representation) or genes (ternary representation) that 
    // are not in the cross point are crossed.
    if (!Config.ternaryRep && Config.permitWithinCrossover){
        for (i=cross1+1; i<cross2-1; i++){
                child2.setAllele(i,parent1);
                child1.setAllele(i,parent2);	    	
        }

        //Now we have to cross the border allele
        child1.crossAllele(cross1,parent1,parent2);   
        child1.crossAllele(cross2-1,parent2,parent1); 
        child2.crossAllele(cross1,parent2,parent1);   
        child2.crossAllele(cross2-1,parent1,parent2); 
    }
    else{
        for (i=cross1; i<cross2-1; i++){
            child2.setAllele(i,parent1);
            child1.setAllele(i,parent2);	        	
        }
    }	        	        
  } // end makeCrossover        

} // end 2PointCrossover




