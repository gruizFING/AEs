/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ao1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * @author Germán_Ruiz
 */

/* saves each tl_logic with ID, Nº OF PHASES, and STATES of PHASES*/
class Logic
{
    public String id;
    public int n_phases;
    public List<String> phases;
    public List<Integer> phases_durations;
}

// Singleton Class
public class Problem 
{ 
    private static Problem problem = null;
    
    
    protected Problem() 
    {
      this.n_tl_logic = 0;
      this.tl_logic = new ArrayList<Logic>();
   }
    
   public static Problem getProblem() {
      if(problem == null) {
         problem = new Problem();
      }
      return problem;
   }
    
    private int dimension;
    private int n_tl_logic;
    private int phasesCount;
    private List<Logic> tl_logic;
    private int simulation_time;      
    private String path;
    private String instance;
    private int n_vehicles;        
    private double best_cost; 

    /*
    public Problem(int dimension, int n_tl_logic, List<Logic> tl_logic, int simulation_time, String path, String instance, int n_vehicles, double best_cost) {
        this.dimension = dimension;
        this.n_tl_logic = n_tl_logic;
        this.tl_logic = tl_logic;
        this.simulation_time = simulation_time;
        this.path = path;
        this.instance = instance;
        this.n_vehicles = n_vehicles;
        this.best_cost = best_cost;
    }
    */
    
    
    public double getBest_cost() {
        return best_cost;
    }

    public int getDimension() {
        return dimension;
    }

    public int getPhasesCount() {
        return phasesCount;
    }
    
    public String getInstance() {
        return instance;
    }

    public int getN_vehicles() {
        return n_vehicles;
    }

    public int getN_tl_logic() {
        return n_tl_logic;
    }

    public int getSimulationTime() {
        return simulation_time;
    }

    public List<Logic> getTl_logic() {
        return tl_logic;
    }

    public String getPath() {
        return path;
    }
    
    public String getLoopPath(){
        return path + "loop.sh";
    }
    
    public String getCfgPath(){
        return path + "red.sumo.cfg";
    }

    public int Populate(String problemPath)
    {  
        if(!problemPath.endsWith("/"))
            problemPath += "/";
        path = problemPath;
        
        int phasesCount = 0;
        String tl_logic_path = path + "tl-logic.add.xml";
        try
        {
            BufferedReader tl_file = new BufferedReader(new FileReader(tl_logic_path));
            String line;
            while ((line = tl_file.readLine()) != null)
            {
                if (line.contains("<tlLogic")) 
                {
                    Logic tl = new Logic();
                    tl.phases = new ArrayList<String>();
                    tl.phases_durations = new ArrayList<Integer>();
                    String[] components = line.split("\\s+"); //Splitting by spaces
                    for (String component : components) {
                        component = component.replace("\"", "");
                        if (component.startsWith("id=")) {
                            tl.id = component.split("=")[1];
                            break;
                        }
                    }

                    line = tl_file.readLine();
                    while ((line.contains("<phase"))) {
                        String[] components_aux = line.split("\\s+"); //Splitting by spaces                            
                        for (String component : components_aux) {
                            component = component.replace("\"", "");
                            if (component.startsWith("duration=")) {
                                String duration = component.split("=")[1];
                                Integer dur = Integer.parseInt(duration);
                                tl.phases_durations.add(dur);
                            }
                            if (component.startsWith("state=")) {
                                String state = component.split("=")[1];
                                state = state.substring(0, state.length()-2);
                                tl.phases.add(state);
                                
                                if (!state.contains("y")) //Para amarillas duracion de 5 seg
                                {
                                    phasesCount++;
                                    
                                }
                                else
                                {
                                    tl.phases_durations.set(tl.phases_durations.size()-1, 5); //Por las dudas
                                }
                                break;
                            }
                        }
                        line = tl_file.readLine();
                    }
                    tl.n_phases = tl.phases.size();
                    //Adding the tl logic
                    this.tl_logic.add(tl);
                }    
            }
            tl_file.close();
        }
        catch(IOException e)
        {
            System.out.println(e.toString());
            e.printStackTrace(); 
        }
        
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            //Here we do the actual parsing
            Document doc = builder.parse(new File(this.getCfgPath()));
            
            this.simulation_time = Integer.parseInt(((Element)doc.getElementsByTagName("end").item(0)).getAttribute("value"));
            System.out.println("Simulation TIme: " + this.simulation_time);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            e.printStackTrace(); 
        }
        
        
        this.n_tl_logic = this.tl_logic.size();
        this.dimension = this.tl_logic.get(0).n_phases;
        this.phasesCount = phasesCount;
        return phasesCount;
    }
    
    public void CreateTLFile(Genotipo genotipo, String tl_logic_path)
    {
        try
        {
            int cont = 0;
            PrintWriter tl_file = new PrintWriter(new BufferedWriter(new FileWriter(path + tl_logic_path)));
            
            tl_file.println("<additional>");
            for (int i=0; i < n_tl_logic; i++)
            {
                tl_file.println("    <tlLogic id=\"" + tl_logic.get(i).id + "\" type=\"static\" programID=\"1\" offset=\"" + (genotipo.phasesDurations[cont]) + "\">");
                cont++;
                for (int j=0; j < tl_logic.get(i).n_phases; j++)
                {
                    String states = tl_logic.get(i).phases.get(j);
                    if (states.contains("y")) //Para amarillas duracion de 5 seg
                    {
                        tl_file.println("        <phase duration=\"5\" state=\"" + states + "\"/>");
                    }
                    else
                    {
                        tl_file.println("        <phase duration=\"" + genotipo.phasesDurations[cont] + "\" state=\"" + states + "\"/>");
                        cont++;
                    }

                }
                tl_file.println("    </tlLogic>");
            }
            tl_file.println("</additional>");
            tl_file.close();
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
    }

}






