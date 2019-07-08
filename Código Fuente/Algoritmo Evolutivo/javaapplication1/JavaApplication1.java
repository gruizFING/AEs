/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplication1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.maths.random.SecureRandomSeedGenerator;
import org.uncommons.maths.random.SeedException;
import org.uncommons.watchmaker.examples.EvolutionLogger;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.SigmaScaling;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

/**
 *
 * @author Germán_Ruiz
 */
public class JavaApplication1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Problem Loading
        // Primer argumento es directorio del problema sin / al final.
        String problemPath = args[0];
        Problem problem = Problem.getProblem();
        int phasesCount = problem.Populate(problemPath);
               
        // Genotipo Factory
        int genotipoLenght = phasesCount + problem.getN_tl_logic();
        CandidateFactory<Genotipo> factory = new GenotipoFactory(genotipoLenght);
       
        // Selección Sigma que delega a Selección de Torneo donde la probabilidad de elegir al 
        // candidato mas 'fittest' es de 1
        //SelectionStrategy selection;
        //SelectionStrategy sigmaScaling;
        
        //FitnessEvaluator<Genotipo> evaluator;
        
        // Random Number Generator
        
        SecureRandomSeedGenerator srsg = new SecureRandomSeedGenerator();
        Random rng = null;
        
        try {
            rng = new MersenneTwisterRNG(srsg.generateSeed(16));
            //Random rng2 = new Random(seed);
        } catch (SeedException ex) {
            Logger.getLogger(JavaApplication1.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Primer Random " + rng.nextInt());
        
        //Random rng = new MersenneTwisterRNG();
        
        List<Genotipo> fittestList = new ArrayList<Genotipo>();
        List<Long> executionTimes = new ArrayList<Long>();
        
        int numeroEjecuciones = 10;

        List<Double> mutationProbs = new ArrayList<Double>();
        List<Double> crossoverProbs = new ArrayList<Double>();
        List<Integer> populationSizes = new ArrayList<Integer>();
        mutationProbs.add(0.05d); // mutationProbs.add(0.075d); mutationProbs.add(0.1d);
        // crossoverProbs.add(0.7d); // 
        crossoverProbs.add(0.9d); // crossoverProbs.add(0.9d); 
        populationSizes.add(50); //populationSizes.add(75); populationSizes.add(100);
        
       
            
        for(Double mp: mutationProbs)
        for(Double cp: crossoverProbs)
        for(Integer ps: populationSizes){
           System.out.println("---EJECUCION mp: " + mp  + "; cp: " + cp + "; ps: " + ps + ";---");
            for(int i = 0; i < numeroEjecuciones; i++){

                System.out.println("Ejecucion " + i + ":");

                // Operadores
                List<EvolutionaryOperator<Genotipo>> operators = new ArrayList<EvolutionaryOperator<Genotipo>>(2);       
                operators.add(new GenotipoMutation(new Probability(mp)));     
                operators.add(new GenotipoCrossover(3,new Probability(cp)));       
                EvolutionaryOperator<Genotipo> pipeline = new EvolutionPipeline<Genotipo>(operators);

                // Selection
                SelectionStrategy selection = new TournamentSelection(new ConstantGenerator<Probability>(new Probability(0.5d)));
                SelectionStrategy sigmaScaling = new SigmaScaling(selection);

                // Fitness Evaluator
                FitnessEvaluator<Genotipo> evaluator = new CachingFitnessEvaluator<Genotipo>(new GenotipoEvaluator());

                long startTime = System.nanoTime();
                EvolutionEngine<Genotipo> engine = new GenerationalEvolutionEngine<Genotipo>(factory, 
                                                                                     pipeline,
                                                                                     evaluator,
                                                                                     sigmaScaling, 
                                                                                     rng);
                GenEvolLogger logger = new GenEvolLogger();
                engine.addEvolutionObserver(logger);

                Genotipo fittestGenotype = engine.evolve(
                        ps, // individuals in the population.
                        5, // 5% elitism.
                        new GenerationCount(200));

                long endTime = System.nanoTime();
                executionTimes.add(TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS));

                fittestList.add(fittestGenotype);

                logger.printData();

                System.out.println(); System.out.println();
            }
        }
        
        
        
        // Impresion de Resultados Acumulados
        int ejec = 0;
        System.out.println("Mejores fitness para las " + numeroEjecuciones + " ejecuciones:"); 
        for(Genotipo genotipo: fittestList)
        {
            System.out.println(genotipo.fitness);
            
            //Genero los mejores TL Logics de cada ejecucion
            String tl_logic_name = "tl-logic" + "-" + ejec++ + "-" + "best.add.xml";
            Problem.getProblem().CreateTLFile(genotipo,"tl_logics/" + tl_logic_name);
        }
        
        System.out.println("Tiempos de ejecucion en seg.:");
        for(Long execTime: executionTimes)
        {
            System.out.println(execTime);
        }
        
    }
    
}
