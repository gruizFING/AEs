/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplication1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

/**
 *
 * @author bdi_usr
 */
public class GenotipoCrossover extends AbstractCrossover<Genotipo> {
    
    public GenotipoCrossover()
    {
        this(1);
    }
    
    
    public GenotipoCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }
    
    public GenotipoCrossover(int crossoverPoints, Probability crossoverProbability)
    {
        super(crossoverPoints, crossoverProbability);
    }
    
    public GenotipoCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }
    
    
     public GenotipoCrossover(NumberGenerator<Integer> crossoverPointsVariable,
                             NumberGenerator<Probability> crossoverProbabilityVariable)
    {
        super(crossoverPointsVariable, crossoverProbabilityVariable);
    }
     
    @Override
    protected List<Genotipo> mate(Genotipo parent1,
                               Genotipo parent2,
                               int numberOfCrossoverPoints,
                               Random rng)
    {
        if (parent1.length != parent2.length)
        {
            throw new IllegalArgumentException("No se puede realizar "
                    + "cruzamiento con cadenas de distinto largo");
        }
        int[] offspring1 = new int[parent1.length];
        System.arraycopy(parent1.phasesDurations, 0, offspring1, 0, parent1.length);
        int[] offspring2 = new int[parent2.length];
        System.arraycopy(parent2.phasesDurations, 0, offspring2, 0, parent2.length);
        // Apply as many cross-overs as required.
        int[] temp = new int[parent1.length];
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + rng.nextInt(parent1.length - 1));
            System.arraycopy(offspring1, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2, 0, offspring1, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2, 0, crossoverIndex);
        }
        List<Genotipo> result = new ArrayList<Genotipo>(2);
        result.add(new Genotipo(offspring1));
        result.add(new Genotipo(offspring2));
        return result;
    }
}
