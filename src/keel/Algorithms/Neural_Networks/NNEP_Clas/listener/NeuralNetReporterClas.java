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

package keel.Algorithms.Neural_Networks.NNEP_Clas.listener;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import keel.Algorithms.Neural_Networks.NNEP_Clas.algorithm.classification.CCRElitistNeuralNetAlgorithm;
import keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification.IClassifier;
import keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification.softmax.ClassificationProblemEvaluator;
import keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification.softmax.ISoftmaxClassifier;
import keel.Algorithms.Neural_Networks.NNEP_Clas.problem.errorfunctions.ClassificationAccuracyErrorFunction;
import keel.Algorithms.Neural_Networks.NNEP_Common.NeuralNetIndividual;
import keel.Algorithms.Neural_Networks.NNEP_Common.algorithm.NeuralNetAlgorithm;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.mutators.parametric.ParametricMutator;
import keel.Algorithms.Neural_Networks.NNEP_Common.mutators.parametric.ParametricSRMutator;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.INeuralNet;
import keel.Algorithms.Neural_Networks.NNEP_Common.problem.ProblemEvaluator;
import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IAlgorithmListener;
import net.sf.jclec.IEvaluator;
import net.sf.jclec.base.AbstractIndividual;


/**
 * <p>Generation reporter of neural net algorithms
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2009
 * @author Modified by Aaron Ruiz Mora (University of Cordoba) 16/7/2009
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class NeuralNetReporterClas implements IAlgorithmListener<NeuralNetAlgorithm<NeuralNetIndividual>> {
	/**
	 * <p>
	 * Generation reporter of neural net algorithms
	 * </p>
	 */

	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////

	/**
	 * Generated by Eclipse
	 */

	private static final long serialVersionUID = 873929825900558241L;

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Classification Accuracy Error function */

	private ClassificationAccuracyErrorFunction caErrorFunction = new ClassificationAccuracyErrorFunction();

	/** KEEL headers of output files */

	private String header;

	/** Train result file */

	private String trainResultFile;

	/** Test result file */

	private String testResultFile;

	/** Best model result file */

	private String bestModelResultFile;

	/** Metadata information of output attribute for generating output files */

	private IAttribute outputAttribute;

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Empty constructor
	 * </p>
	 */

	public NeuralNetReporterClas() {
		super();
	}


	/////////////////////////////////////////////////////////////////
	// -------------------- Implementing IAlgorithmListener interface
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * This event is fired when the algorithm has started its execution.
	 * 
	 * @param event An event containing a reference to the source algorithm.
	 * </p>
	 */ 

	public void algorithmStarted(AlgorithmEvent<NeuralNetAlgorithm<NeuralNetIndividual>> event) {
		// do report
		doIterationReport(event.getAlgorithm());
	}

	/**
	 * <p>
	 * This event is fired when the algorithm has finished its execution.
	 * 
	 * @param event An event containing a reference to the source algorithm.
	 * </p>
	 */ 

	@SuppressWarnings("unchecked")
	public void algorithmFinished(AlgorithmEvent<NeuralNetAlgorithm<NeuralNetIndividual>> event) {

		try 
		{

			NeuralNetAlgorithm<NeuralNetIndividual> algorithm = event.getAlgorithm();

			PrintWriter print = new PrintWriter( new FileWriter ( trainResultFile ) );
			print.write(header);

			DoubleTransposedDataSet dataset = ((ProblemEvaluator) event.getAlgorithm().getEvaluator()).getTrainData();
			double[][] observedOutputs = dataset.getAllOutputs();
			CCRElitistNeuralNetAlgorithm elitistAlgorithm = (CCRElitistNeuralNetAlgorithm) algorithm;

			ISoftmaxClassifier bestClassifier = (ISoftmaxClassifier) elitistAlgorithm.getBestCCRIndividual().getGenotype();
			byte[][] predictedOutputs = bestClassifier.classify(dataset.getAllInputs());

			// Print train results		
			for(int i=0; i<dataset.getNofobservations(); i++){
				int observedClass = 1;
				while(observedOutputs[observedClass-1][i]!=1)
					observedClass++;
				print.write(outputAttribute.show(observedClass) + " ");

				int predictedClass = 1;
				while(predictedOutputs[predictedClass-1][i]!=1)
					predictedClass++;
				print.write(outputAttribute.show(predictedClass) + "\n");
			}

			print.close();

			// Print test results		
			print = new PrintWriter( new FileWriter ( testResultFile ) );
			print.write(header);

			dataset = ((ProblemEvaluator) event.getAlgorithm().getEvaluator()).getTestData();

			observedOutputs = dataset.getAllOutputs();
			predictedOutputs = bestClassifier.classify(dataset.getAllInputs());
			for(int i=0; i<dataset.getNofobservations(); i++){
				int observedClass = 1;
				while(observedOutputs[observedClass-1][i]!=1)
					observedClass++;
				print.write(outputAttribute.show(observedClass) + " ");

				int predictedClass = 1;
				while(predictedOutputs[predictedClass-1][i]!=1)
					predictedClass++;
				print.write(outputAttribute.show(predictedClass) + "\n");
			}

			print.close();

			// Print test results		
			print = new PrintWriter( new FileWriter ( bestModelResultFile ) );   			

			// Algorithm generation
			int generation = algorithm.getGeneration();

			//ParametricMutator of NeuralNetAlgorithm
			ParametricMutator<NeuralNetIndividual> parametricMutator = null;
			if(algorithm.getMutator1() instanceof ParametricMutator)
				parametricMutator = (ParametricMutator<NeuralNetIndividual>) algorithm.getMutator1();
			else if(algorithm.getMutator2() instanceof ParametricMutator)
				parametricMutator = (ParametricMutator<NeuralNetIndividual>) algorithm.getMutator2();

			print.write(renderGeneration(generation, (AbstractIndividual) elitistAlgorithm.getBestIndividual(), 
					elitistAlgorithm.getBestCCRIndividual(), parametricMutator, algorithm.getEvaluator()));
			print.close();
		}
		catch ( IOException e )
		{
			System.err.println( "Can not open the training output file: " + e.getMessage() );
		}
	}

	/**
	 * <p>
	 * This event is fired when the algorithm has finished a generation.
	 * 
	 * @param event An event containing a reference to the source algorithm.
	 * </p>
	 */ 

	public void iterationCompleted(AlgorithmEvent<NeuralNetAlgorithm<NeuralNetIndividual>> event) {
		// do report
		doIterationReport(event.getAlgorithm());
	}

	/////////////////////////////////////////////////////////////////
	// ------------------------------- Getting and setting properties
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Returns file name where the best model obtained will be written
	 *
	 * @return String File name
	 * </p>
	 */

	public String getBestModelResultFile() {
		return bestModelResultFile;
	}

	/**
	 * <p>
	 * Sets file name where the best model obtained will be written
	 *
	 * @param bestModelResultFile File name
	 * </p>
	 */

	public void setBestModelResultFile(String bestModelResultFile) {
		this.bestModelResultFile = bestModelResultFile;
	}

	/**
	 * <p>
	 * Returns file name where the testing results of best model 
	 * obtained will be written
	 *
	 * @return String File name
	 * </p>
	 */

	public String getTestResultFile() {
		return testResultFile;
	}

	/**
	 * <p>
	 * Sets file name where the testing results of best model 
	 * obtained will be written
	 *
	 * @param testResultFile File name
	 * </p>
	 */

	public void setTestResultFile(String testResultFile) {
		this.testResultFile = testResultFile;
	}

	/**
	 * <p>
	 * Returns file name where the training results of best model 
	 * obtained will be written
	 *
	 * @return String File name
	 * </p>
	 */

	public String getTrainResultFile() {
		return trainResultFile;
	}

	/**
	 * <p>
	 * Sets file name where the testing results of best model 
	 * obtained will be written
	 *
	 * @param trainResultFile File name
	 * </p>
	 */

	public void setTrainResultFile(String trainResultFile) {
		this.trainResultFile = trainResultFile;
	}

	/**
	 * <p>
	 * Returns KEEL file header
	 *
	 * @return String KEEL file header
	 * </p>
	 */

	public String getHeader() {
		return header;
	}

	/**
	 * <p>
	 * Sets KEEL file header
	 *
	 * @param header KEEL file header
	 * </p>
	 */

	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * <p>
	 * Returns output attribute metadata
	 *
	 * @return IAttribute Output attribute metadata
	 * </p>
	 */

	public IAttribute getOutputAttribute() {
		return outputAttribute;
	}

	/**
	 * <p>
	 * Sets output attribute metadata
	 *
	 * @param outputAttribute New output attribute metadata
	 * </p>
	 */

	public void setOutputAttribute(IAttribute outputAttribute) {
		this.outputAttribute = outputAttribute;
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public Methods
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Outputs the information of an iteration to System.out
	 *
	 * @param algorithm Algorithm that is exectuting currently
	 * </p>
	 */

	@SuppressWarnings("unchecked")
	public void doIterationReport(NeuralNetAlgorithm<NeuralNetIndividual> algorithm){

		// Algorithm generation
		int generation = algorithm.getGeneration();

		//ParametricMutator of NeuralNetAlgorithm
		ParametricMutator<NeuralNetIndividual> parametricMutator = null;
		if(algorithm.getMutator1() instanceof ParametricMutator)
			parametricMutator = (ParametricMutator<NeuralNetIndividual>) algorithm.getMutator1();
		else if(algorithm.getMutator2() instanceof ParametricMutator)
			parametricMutator = (ParametricMutator<NeuralNetIndividual>) algorithm.getMutator2();

		//Output generation
		printGeneration(generation, algorithm.getBestIndividual(), ((CCRElitistNeuralNetAlgorithm) algorithm).getBestCCRIndividual(), parametricMutator, algorithm.getEvaluator());

		System.out.println("Generations without improving ==> Best: " + algorithm.getNogbest() + " ( " + algorithm.getCurrentBest() + " / " + algorithm.getPreviousBest() + " )" +
				"\n                                  Mean: " + algorithm.getNogmean() + " ( " + algorithm.getCurrentMean() + " / " + algorithm.getPreviousMean() + " )\n");		

	}

	/**
	 * <p>
	 * Outputs the information of a generation to System.out
	 *
	 * @param nofgeneration Number of generation of the algorithm
	 * @param bestNnind Best NeuralNetIndividual of the algorithm
	 * @param bestCCRNnind Best CCR NeuralNetIndividual of the algorithm (if it is a Classification problem)
	 * @param parametricMutator ParametricMutator of the algorithm
	 * @param evaluator NeuralNetEvaluator to use in individual evaluation
	 * </p>
	 */

	public void printGeneration(int nofgeneration, AbstractIndividual<INeuralNet> bestNnind, AbstractIndividual<INeuralNet> bestCCRNnind,
			ParametricMutator<NeuralNetIndividual> parametricMutator, IEvaluator evaluator)
	{
		System.out.println(renderGeneration(nofgeneration, bestNnind, bestCCRNnind, parametricMutator, evaluator));
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected Methods
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Renders a generation of the algorithm to a String
	 *
	 * @param nofgeneration Number of generation of the algorithm
	 * @param bestNnind Best NeuralNetIndividual of the algorithm
	 * @param bestCCRNnind Best CCR NeuralNetIndividual of the algorithm (if it is a Classification problem)
	 * @param parametricMutator ParametricMutator of the algorithm
	 * @param evaluator NeuralNetEvaluator to use in individual evaluation
	 * 
	 * @return String String with the render of a generation
	 * </p>
	 */

	protected String renderGeneration(int nofgeneration, AbstractIndividual<INeuralNet> bestNnind, AbstractIndividual<INeuralNet> bestCCRNnind,
			ParametricMutator<NeuralNetIndividual> parametricMutator, IEvaluator evaluator){

		String result = ("Generation " + nofgeneration +"\n");
		if(bestCCRNnind!=null){
			result+="=> Best CCR Individual:\n";
			result+=renderNeuralNetIndividual(bestCCRNnind, evaluator);
		}
		else{
			result+="=> Best Fitness Individual:\n";
			result+=renderNeuralNetIndividual(bestNnind, evaluator);
		}
		if(parametricMutator!=null)
			result+=("AlphaInput " + parametricMutator.getAlphaInput() + " AlphaOutput " + parametricMutator.getAlphaOutput());
		if(parametricMutator instanceof ParametricSRMutator)
			result+=(" Success Ratio " + ((ParametricSRMutator<NeuralNetIndividual>) parametricMutator).getSuccessRatio());
		return result;
	}

	/**
	 * <p>
	 * Renders a NeuralNetIndividual to a String
	 *
	 * @param nnind IGenotype(INeuralNet) to render
	 * @param evaluator NeuralNetEvaluator to use in individual evaluation
	 * 
	 * @return String String with the render of a NeuralNetIndividual
	 * </p>
	 */

	@SuppressWarnings("unchecked")
	protected String renderNeuralNetIndividual(AbstractIndividual<INeuralNet> nnind, IEvaluator evaluator){

		String result = nnind.toString();
		result+="\n";

		result+=("Number of hidden neurons: " + nnind.getGenotype().getNofhneurons());
		result+=(" Number of effective links: "+ nnind.getGenotype().getNoflinks() + "\n");

		IClassifier classifier = (IClassifier) nnind.getGenotype();
		result+=("Train CCR: " + ((ClassificationProblemEvaluator)evaluator).getTrainClassificationError(classifier, caErrorFunction) + "\n");
		result+=("Test  CCR: " + ((ClassificationProblemEvaluator)evaluator).getTestClassificationError(classifier, caErrorFunction) + "\n");

		return result;
	}
}

