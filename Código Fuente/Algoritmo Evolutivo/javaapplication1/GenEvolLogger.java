/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplication1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;

/**
 *
 * @author bdi_usr
 */
public class GenEvolLogger implements EvolutionObserver<Genotipo> {
    
    public List<Double> bestFitness = new ArrayList<Double>();
    public List<Double> bestVD = new ArrayList<Double>();
    public List<Double> bestCO2 = new ArrayList<Double>();
    public List<Double> bestCO = new ArrayList<Double>();
    public List<Double> bestHC = new ArrayList<Double>();
    public List<Double> bestPMx = new ArrayList<Double>();
    public List<Double> bestNOx = new ArrayList<Double>();
    
    
    public GenEvolLogger() {

    }

    @Override
    public void populationUpdate(PopulationData<? extends Genotipo> data) {
        Genotipo bestCandidate = data.getBestCandidate();
        bestFitness.add(data.getBestCandidateFitness());
        System.out.printf("Generation " + data.getGenerationNumber() + ": %.10f%n", data.getBestCandidateFitness());
        // recolectar informacion de contaminantes
        bestVD.add(bestCandidate.vd);
        bestCO2.add(bestCandidate.co2);
        bestCO.add(bestCandidate.co);
        bestHC.add(bestCandidate.hc);
        bestPMx.add(bestCandidate.pmx);
        bestNOx.add(bestCandidate.nox);
        
        // ¿P?2
        //System.out.println(Arrays.toString(data.getBestCandidate().phasesDurations));
    }
    
    public void printData(){
        
        System.out.println("Evolucion de Fitness - VD - CO2 - CO - HC - NOx:");
        for(int i = 0; i < bestFitness.size(); i++)
        {
            System.out.printf("%.10f\t%.0f\t%.5f\t%.5f\t%.5f\t%.5f%n", bestFitness.get(i),bestVD.get(i),bestCO2.get(i),bestCO.get(i),bestHC.get(i),bestNOx.get(i));
        }
        System.out.println("Evolucion de Fitness:");
        for(Double fitness: bestFitness)
        {
            System.out.println(fitness);
        }
        /*
        System.out.println("Evolucion de VD:");
        for(Double vd: bestVD)
        {
            System.out.println(vd);
        }
        System.out.println("Evolucion de CO2:");
        for(Double cont: bestCO2)
        {
            System.out.println(cont);
        }
        System.out.println("Evolucion de CO:");
        for(Double cont: bestCO)
        {
            System.out.println(cont);
        }
        System.out.println("Evolucion de HC:");
        for(Double cont: bestHC)
        {
            System.out.println(cont);
        }
        System.out.println("Evolucion de NOx:");
        for(Double cont: bestNOx)
        {
            System.out.println(cont);
        }
        /*
        System.out.println("Evolucion de PMx:");
        for(Double cont: bestPMx)
        {
            System.out.println(cont);
        }
        */
    }
    
}
