/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ao1;

import java.util.Random;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 *
 * @author bdi_usr
 */
public class GenotipoFactory extends AbstractCandidateFactory<Genotipo>
{
    private final int phasesCount;
    
    public GenotipoFactory(int phasesCount)
    {
        if (phasesCount <= 0)
        {
            throw new IllegalArgumentException("El numero de fases debe ser positivo.");
        }
        this.phasesCount = phasesCount;
    }
    
    @Override
    public Genotipo generateRandomCandidate(Random rng)
    {
        int[] phasesDurations = new int[phasesCount];
        for (int i = 0; i < phasesCount; i++)
        {
            // se asignan duraciones en el intervalo [5,60] segundos
            phasesDurations[i] = 5 + rng.nextInt(56);
        }
        return new Genotipo(phasesDurations);
    }
    
    
}
