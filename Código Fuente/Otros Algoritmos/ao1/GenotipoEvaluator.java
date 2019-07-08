/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ao1;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 *
 * @author bdi_usr
 */
public class GenotipoEvaluator  implements FitnessEvaluator<Genotipo>{
    
    private final Problem problem;
    private final String problem_path, loop_path, sumo_cfg_path;
    private final Integer simulation_time, n_vehicles;
    
    
    public GenotipoEvaluator(){
        problem = Problem.getProblem();
        problem_path = problem.getPath();
        loop_path = problem.getLoopPath();
        sumo_cfg_path = problem.getCfgPath();
        simulation_time = problem.getSimulationTime();
        n_vehicles = problem.getN_vehicles();
    }
    
    
    @Override
    public double getFitness(Genotipo candidate,
                             List<? extends Genotipo> population)
    {
        double fitness = 0.0;
        // Cargar candidato en archivo de SUMO
        try
        {
            String current_time = Long.toString(System.currentTimeMillis());
            String tl_logic_name = "tl-logic" + candidate.id + current_time + ".add.xml";
            
            problem.CreateTLFile(candidate, "tl_logics/" + tl_logic_name);
            
            String tl_logic_path = problem_path + "tl_logics/" + tl_logic_name;
            
            
            // Ejecutar simulación de SUMO
        
            /* CALL THE SUMO SIMULATOR AND SET A NEW TL_LOGIC SOLUTION (PHASE'S DURATION)*/	
            //sprintf(order,"tl2/./loop.sh %s.%d.sumo.cfg tl2/%s/%d/ tl-logic.add.xml output-tripinfos.xml tl2/ %d %d",pbm().instance(),pbm().n_vehicles(),pbm().path(),pbm().n_tl_logic(),pbm().n_vehicles(),pbm().simulation_time());
            //Runtime rt = Runtime.getRuntime();
            //Process pr = rt.exec("cmd /c dir");
      
            
            String id_exec = candidate.id + current_time;
            String output_path = problem_path + "output/";
            String output_trips_path = output_path + "output-tripinfos" + id_exec + ".xml";
            String output_emission_path = problem_path + "edges_data/output/output-emissions" + id_exec + ".xml";
            String output_trips_res = output_path + "resTrip" + id_exec + ".txt";
            String output_grVre_res = output_path + "GvR" + id_exec + ".txt";
            String output_emiss_res = output_path + "resEmission" + id_exec + ".txt";
            String output_numVeh_res = output_path + "numVeh" + id_exec + ".txt";
            
            //Creo el EdgeData????.add.xml file
            String edge_data= problem_path + "edges_data/edgeData" + id_exec + ".add.xml";           
            PrintWriter edge_data_file = new PrintWriter(new BufferedWriter(new FileWriter(edge_data)));
            edge_data_file.println("<additional>");
            edge_data_file.println("<edgeData id=\"dump_" + simulation_time + "\" type=\"emissions\" freq=\"" + simulation_time + "\" file=\"output/output-emissions" + id_exec + ".xml\"/>");
            edge_data_file.println("</additional>");
            edge_data_file.close();
            
            ProcessBuilder builder = new ProcessBuilder(
                    loop_path,                    
                    sumo_cfg_path,
                    problem_path,
                    tl_logic_path,
                    edge_data,
                    output_trips_path,
                    output_emission_path,
                    output_grVre_res,
                    output_trips_res,
                    output_numVeh_res,
                    output_emiss_res,
                    Integer.toString(n_vehicles),
                    Integer.toString(simulation_time)
            );  
            Process pr = builder.start();
            
            int exitVal = pr.waitFor();
            
            if (exitVal == 0) //No errors
            {               
                //Leer los datos y calcular fitness desde resEmis..txt
                BufferedReader fitness_file = new BufferedReader(new FileReader(output_emiss_res));
                double co, co2, hc, nox, pmx, fuel, cantVeh; 
                String line = fitness_file.readLine();
                String[] values = line.split(" ");
                co   = Double.parseDouble(values[0]);
                co2  = Double.parseDouble(values[1]);
                hc   = Double.parseDouble(values[2]);
                nox  = Double.parseDouble(values[3]);
                pmx  = Double.parseDouble(values[4]);
                fuel = Double.parseDouble(values[5]);
                cantVeh = Double.parseDouble(values[6]);
                
                fitness_file.close();
                
                // CALCULO DE LA FUNCION DE FITNESS //
                
                fitness = (co + co2/100 + hc*10 + nox*100)/(cantVeh*2);
                
                //fitness = (co*100 + co2 + hc*1000 + nox*10000) + 100000000/(cantVeh);
                
                
                // ****** //
                
                File fit_file = new File(output_emiss_res);
                if (!fit_file.delete())
                {
                    throw new Exception("Error borrando el archivo de fitness");
                }
                
                // Cargo los valores en candidate
                candidate.setParameters(cantVeh, co2, co, hc, pmx, nox, fitness);
            }
            else
            {
                InputStream is = pr.getErrorStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line2;
                while ((line2 = br.readLine()) != null) {
                    System.out.println(line2);
                }
            }
            
            //Borrar los archivos generados para no ocupar espacio innecesario
            
        }
        /*
        catch(java.io.IOException e) 
        { 
            System.out.println(e.toString());
            e.printStackTrace(); 
        }
        
        catch(InterruptedException | NumberFormatException e)
        {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        */
        catch(Exception e){
            System.out.println(e.toString());
            e.printStackTrace(); 
        }
        
        
        
        return fitness;
    }
    
    /**
    * <p>Specifies whether this evaluator generates <i>natural</i> fitness
     * scores or not.</p>
     * <p>Natural fitness scores are those in which the fittest
     * individual in a population has the highest fitness value.  In this
     * case the algorithm is attempting to maximise fitness scores.
     * There need not be a specified maximum possible value.</p>
     * <p>In contrast, <i>non-natural</i> fitness evaluation results in fitter
     * individuals being assigned lower scores than weaker individuals.
     * In the case of non-natural fitness, the algorithm is attempting to
     * minimise fitness scores.</p>
     */
    @Override
    public boolean isNatural()
    {
        return false;
    }
    
}
