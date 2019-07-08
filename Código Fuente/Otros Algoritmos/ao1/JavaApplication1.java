/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ao1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.SecureRandomSeedGenerator;
import org.uncommons.maths.random.SeedException;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

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
        
        String algorithm_name = "CPH";
        //String algorithm_name = "CPHD";
        //String algorithm_name = "SA";

        
        System.out.println("ALGORITMO " + algorithm_name);
        System.out.println(); System.out.println();
        
        int numeroEjecuciones = 30;
        for(int i = 0; i < numeroEjecuciones; i++){

            System.out.println("Ejecucion " + i + ":");

            // Fitness Evaluator
            FitnessEvaluator<Genotipo> evaluator = new GenotipoEvaluator();

            // START
            long startTime = System.nanoTime();
            
            AO1Engine engine = new AO1Engine(factory, evaluator, rng);
            
            // execute1          
            Genotipo fittestGenotype = engine.execute1();
            
            // execute2              
            //Genotipo fittestGenotype = engine.execute2(engine.execute1());
            
            // simulatedAnnealing
            //Genotipo fittestGenotype = engine.simulatedAnnealing();

            long endTime = System.nanoTime();
            // END
            
            executionTimes.add(TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS));

            fittestList.add(fittestGenotype);

            GenEvolLogger logger = new GenEvolLogger();
            logger.printData(fittestGenotype);

            System.out.println(); System.out.println();
        }
        
        // Impresion de Resultados Acumulados
        // Cambiar algorithm_number segun sea execute1 o execute2
        int ejec = 0;
        System.out.println(algorithm_name + " + Mejores fitness para las " + numeroEjecuciones + " ejecuciones:"); 
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
