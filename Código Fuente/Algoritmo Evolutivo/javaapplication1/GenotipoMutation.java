/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplication1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 *
 * @author bdi_usr
 */
public class GenotipoMutation implements EvolutionaryOperator<Genotipo>
{ 
    private final NumberGenerator<Probability> mutationProbability;
    private final Double mutationPercentage = 0.05d;
    
    public GenotipoMutation(Probability mutationProbability)
    {
        this.mutationProbability = new ConstantGenerator<Probability>(mutationProbability);
    }
    
    public GenotipoMutation(NumberGenerator<Probability> mutationProbability)
    {
        this.mutationProbability = mutationProbability;
    }
    
    
    @Override
    public List<Genotipo> apply(List<Genotipo> selectedCandidates, Random rng)
    {
        List<Genotipo> mutatedPopulation = new ArrayList<Genotipo>(selectedCandidates.size());
        for(Genotipo genotipo: selectedCandidates)
        {
             mutatedPopulation.add(mutateGenotipo(genotipo, rng));
        }
        return mutatedPopulation;
    }
    
    private Genotipo mutateGenotipo(Genotipo s, Random rng)
    {
        if (mutationProbability.nextValue().nextEvent(rng))
        {
            Genotipo mutatedGenotipo = new Genotipo(s.phasesDurations.clone());
            for(int i = 0; i < s.length*mutationPercentage; i++){
                mutatedGenotipo.phasesDurations[rng.nextInt(s.length)] = 5 + rng.nextInt(56);
            }
            return mutatedGenotipo;
        }
        return s;
    }
    
}
