/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplication1;

/**
 *
 * @author bdi_usr
 */
public class Genotipo {
    
    public final int[] phasesDurations;
    public final int length;
    
    private static int GenotipoCount = 0;
    
    public final int id;
    
    public double vd;
    public double co2;
    public double co;
    public double hc;
    public double pmx;
    public double nox;
    public double fitness;
    
    public Genotipo(int[] phasesDurations)
    {
        this.phasesDurations = phasesDurations;
        this.length = phasesDurations.length;
        this.id = GenotipoCount++;
    }
    
    public void setParameters(double vd, double co2, double co, double hc, 
            double pmx, double nox, double fitness)
    {
        this.vd         = vd;
        this.co2        = co2;
        this.co         = co;
        this.hc         = hc;
        this.pmx        = pmx;
        this.nox        = nox;
        this.fitness    = fitness;
    }
    
}
