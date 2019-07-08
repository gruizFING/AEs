/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ao1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;

/**
 *
 * @author bdi_usr
 */
public class AO1Engine extends GenerationalEvolutionEngine {
    
    private final Random rng;
    private final CandidateFactory<Genotipo> candidateFactory;
    private final FitnessEvaluator<? super Genotipo> fitnessEvaluator;
    
    AO1Engine(CandidateFactory<Genotipo> candidateFactory,
                                      FitnessEvaluator<? super Genotipo> fitnessEvaluator,
                                      Random rng)
    {
        super(candidateFactory, null, fitnessEvaluator, null, rng);
        this.candidateFactory = candidateFactory;
        this.fitnessEvaluator = fitnessEvaluator;
        this.rng = rng;
    }
    
    // Algoritmo Otro Numero 1
    public Genotipo execute1(){
        Genotipo gen = null;
       
        // Tomar los valores de fase del archivo de logicas
        // Generar una poblacion de 200 Genotipos, asignando un valor
        // aleatorio en el rango [x-10,x,x+10] a cada fase(roja o verde), donde x es el valor en el archivo.
        // Evaluar toda la poblacion con el GenotipoEvaluator y obtener el mejor.
        // Retornarlo.
        
        List<Genotipo> population = new ArrayList<Genotipo>();
        Problem problem = Problem.getProblem();
        int phasesCount = problem.getPhasesCount();
        int n_tl_logics = problem.getN_tl_logic();
        
        for (int i = 0; i < 200; i++) 
        {
            int[] phasesDurations = new int[phasesCount + n_tl_logics];
            int pos = 0;
            for (Logic tlogic : problem.getTl_logic()) 
            {
                phasesDurations[pos++] = 0; //Offset en 0
                for (int j = 0; j < tlogic.n_phases; j++)
                {
                    if (!tlogic.phases.get(j).contains("y")) //Solo agrego duracion si no es amarilla
                    {
                        int duration = tlogic.phases_durations.get(j);
                        int random = this.rng.nextInt(21); //0 a 20
                        
                        phasesDurations[pos++] = duration - 10 + random;
                    }
                }
            }
                    
            Genotipo genotipo = new Genotipo(phasesDurations);
            population.add(genotipo);
        }
        
        List<EvaluatedCandidate<Genotipo>> evaluated = evaluatePopulation(population);
        Collections.sort(evaluated);

        //Agarro el mejor genotipo, el primero en la lista tiene el mejor fitness
        gen = evaluated.get(0).getCandidate();
        
        return gen;
    }
    
    // Algoritmo Otro Numero 2
    public Genotipo execute2(Genotipo gen){
        
        // Toma el resultado de la execute1 e intenta mejorarlo asignando 
        // a una única verde un valor de 3 min = 120 segundos de forma de descongestionar
        // Prueba con todas las verdes, las evalua y se queda con la mejor.
        // compara tambien contra el genotipo devuelto por execute1 ya que 
        // tal vez no es necesario descongestionar ninguna interseccion.
        
        List<Genotipo> population = new ArrayList<Genotipo>();
        int lenght = gen.length;
        int[] phases_durations = gen.phasesDurations;
        
        for (int i = 1; i < lenght; i++)
        {
            if (phases_durations[i] != 0) //offset, se saltea
            {
                int value = phases_durations[i];
                phases_durations[i] = 180;
                Genotipo genotipo = new Genotipo(phases_durations);
                phases_durations[i] = value;
                
                population.add(genotipo);
            }
        }
        
        List<EvaluatedCandidate<Genotipo>> evaluated = evaluatePopulation(population);
        Collections.sort(evaluated);

        //Agarro el mejor genotipo, el primero en la lista tiene el mejor fitness
        gen = evaluated.get(0).getCandidate();
        
        return gen;
    }
    
    public Genotipo simulatedAnnealing()
    {
        GenotipoEvaluator evaluator = new GenotipoEvaluator();       
        Genotipo gen = this.candidateFactory.generateRandomCandidate(rng);
        double bestFitness = evaluator.getFitness(gen, null);
      
        for (int T = 200; T > 0; T--)
        {
            int[] phases_durations = Neighbor(gen.phasesDurations.clone());
            
            Genotipo genotipo = new Genotipo(phases_durations);
            double fitness = evaluator.getFitness(genotipo, null);
            
            if (acceptanceProbability(bestFitness, fitness, T) > rng.nextDouble())
            {
                bestFitness = fitness;
                gen = genotipo;
            }      
        }
    
        return gen;
    }
    
    private int[] Neighbor(int[] phases_dur)
    {
        int rand = rng.nextInt(phases_dur.length);            
        int rand2 = rng.nextInt(2);
        
        if (rand2 == 0) //Resto 5, si queda < 5 entones le sumo
            phases_dur[rand] = phases_dur[rand] < 10 ? phases_dur[rand] + 5 : phases_dur[rand] - 5;
        else            //Sumo 5, si queda > 60 entones le resto
            phases_dur[rand] = phases_dur[rand] > 55 ? phases_dur[rand] - 5 : phases_dur[rand] + 5;
             
        return phases_dur;
    }

    private double acceptanceProbability(double bestFitness, double fitness, int T) {
        // If the new solution is better, accept it
        if (fitness < bestFitness)
            return 1.0;
        
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((bestFitness - fitness) / T);
    }
    
    
}
