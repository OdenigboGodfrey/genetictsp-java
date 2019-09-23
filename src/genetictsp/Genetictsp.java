/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genetictsp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Black
 */
public class Genetictsp {

    /**
     * @param args the command line arguments
     */
    static LinkedList<String> locs = new LinkedList<>();
    //read lines from the file into a list of string
    static List<String> list = new ArrayList<>();
    //creating a hashmap to hold all info on the locations
    static LinkedHashMap<Integer,LinkedList<Double>> main_locations = new LinkedHashMap<>();
    static LinkedHashMap<Integer,LinkedList<Double>> init_main_locations = new LinkedHashMap<>();
    //creating a hashmap to hold the final locations rearranged to reduce travel
    static HashMap<Integer,List<Double>> distance = new HashMap<>();
    static HashMap<Integer,List<String>> random_locations_path = new HashMap<>();
    static LinkedList<Double> random_locations_distance = new LinkedList<>();
    //holds the current generated paths 
    static LinkedList<String> path = new LinkedList<>();
    //static List<Double> random_locations_distance = new ArrayList<>();
    static int start_location = 1;
    static Double start_location_x = 0.0;
    static Double start_location_y = 0.0;
    static SecureRandom sr = new SecureRandom();
    static int genertions = 1000,generations_count = 0;
    static boolean GASwap = false;
    static double best_generation_distance = 0.0 ;
    static String best_generation = "",best_genertion_path;
    static LinkedList<String> unique = new LinkedList<>();
    static int _10P = 0;
    static int g_c = 0;
    
    public static void main(String[] args) {
        try {
            File f = new File("test3tsp.txt");
            //get file path
            Path p = f.toPath();
            list = Files.readAllLines(p);
            //loop through the list of the file content
            for(String s : list){
                //split the string
                int city_id = 0;
                List<Double> temp_list = new ArrayList<>();
                String[] temp = s.split(" ");
                int temp_counter = 0;
                for(int i = 0; i < temp.length;i++)
                {
                    if(!temp[i].equals(""))
                    {
                       switch(temp_counter)
                       {
                            case 0:
                                city_id = Integer.parseInt(temp[i]);
                                break;
                            case 1:
                                temp_list.add( Double.parseDouble(temp[i]));
                                break;
                            case 2:
                                temp_list.add( Double.parseDouble(temp[i]));
                                break;
                            default:
                                break;
                       }
                    temp_counter++;
                    }
                }
                //check to avoid repeating the same location
                if(main_locations.size() > 0 && !main_locations.containsValue(temp_list))
                {   
                    main_locations.put(city_id, new LinkedList<>());
                    main_locations.get(city_id).add(temp_list.get(0));
                    main_locations.get(city_id).add(temp_list.get(1));
                }
                else if(main_locations.isEmpty())
                {
                    main_locations.put(city_id, new LinkedList<>());
                    main_locations.get(city_id).add(temp_list.get(0));
                    main_locations.get(city_id).add(temp_list.get(1));
                }
            }
            init_main_locations = new LinkedHashMap<>(main_locations);
            //start location is city 1
            start_location = 1;
            _10P = (int)(0.1 *genertions);
            Random(10,2);
        } catch (Exception ex) {
            Logger.getLogger(Genetictsp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void Random(int run_times,int parent_no)
    {
        //hold city numbers
        LinkedList<Integer> cities_no = new LinkedList<>();
        LinkedList<Integer> init_cities_no = new LinkedList<>();
        
        Double temp_distance = 0.0,total_distance = 0.0;
        
        for(Map.Entry<Integer,LinkedList<Double>> entry : main_locations.entrySet())
        {
            init_cities_no.add(entry.getKey());
        }
        cities_no.addAll(init_cities_no);
        int recheck_counter = 0,success_counter = 0;
        //clear path on each run
        path.clear();
        for(int x = 0;x < parent_no;x++)
        {
            System.out.println("=====================");
            String t_path = "";
            start_location = 1;
            temp_distance = 0.0;
            if(recheck_counter == run_times || success_counter == parent_no)
            {
                break;
            }
            
            if(cities_no.isEmpty())
            {
                cities_no.addAll(init_cities_no);
            }
            
            for(;;)
            {                
                if(cities_no.size() == 1)
                {
                    temp_distance += nn(start_location, 1);
                    System.out.println("Travelling from city "+start_location+" to city "+1);
                    t_path += start_location;
                    if(temp_distance < total_distance || total_distance == 0.0)
                    {
                        total_distance = temp_distance;
                        best_generation_distance = total_distance;
                        best_generation = "Parent";
                        best_genertion_path = t_path;
                        path.add(t_path);
                        success_counter += 1;
                    }
                    else
                    {
                        recheck_counter+=1;
                    }
                    System.out.println("Total Distance is "+temp_distance);
                    cities_no.clear();
                    break;
                }
                
                int random_city = sr.nextInt(cities_no.size());
                //make sure random city is not the current city
                if(cities_no.get(random_city) == start_location)
                {
                    for(;;)
                    {
                        random_city = sr.nextInt(cities_no.size());
                        if(cities_no.get(random_city) != start_location)
                        {
                            break;
                        }
                    }
                }
                temp_distance += nn(start_location, cities_no.get(random_city));
                t_path += start_location+",";
                System.out.println("Travelling from city "+start_location+" to city "+cities_no.get(random_city));
                int prev_start_loc = start_location;
                start_location = cities_no.get(random_city);
                cities_no.remove(cities_no.indexOf(prev_start_loc));
                
            }
            
        }
        for(String s : path)
        {
            System.out.println("Parent : "+s);
            unique.add(s);
        }
        if(path.size() != parent_no)
        {
            Random(run_times, parent_no);
        }
        else
        {
            
            for(int i = 0; i <= genertions;i++)
            {
                GA(path,"both");   
            }
            
            System.out.println("================================");
            System.out.println("Shortest Distance "+best_generation_distance+" Gotten in "+best_generation+" With a City Travel Path of "+best_genertion_path);
            System.out.println("Unique Children Created After "+ generations_count +" Generations Are "+unique.size());
            System.out.println("================================");   
        }
    }
    private static Double nn(int start_location,int key)
    {
        //calculate travel distance
        double distance_calc = Math.sqrt(Math.pow((main_locations.get(start_location).get(0) - (main_locations.get(key).get(0))), 2) + Math.pow((main_locations.get(start_location).get(1) - (main_locations.get(key).get(1))), 2));
        return distance_calc;
    }
    
    private static void GA(LinkedList<String> parents,String type)
    {   
        
        if(g_c == _10P)
        {
            System.out.println("Generation Count "+generations_count);
            g_c = 0;
        }
        g_c+=1;
        String[] parent1 = parents.get(0).split(",");
        
        String[] parent2 = parents.get(1).split(",");
        LinkedList<String> children = new LinkedList<>();
        switch(type.toLowerCase())
        {
            case "single point":
                children.addAll(single_point(parent1, parent2));
                break;
            case "two point":
                children.addAll(two_point(parent1, parent2));
                break;
            case "both":
                if(GASwap)
                {
                    children.addAll(single_point(parent1, parent2));
                    GASwap = false;
                }
                else
                {
                    children.addAll(two_point(parent1, parent2));
                    GASwap = true;
                }
                break;
        }
        
        for(String s : children)
        {
            String[] splitted_child = s.split(",");
            double temp_distance = 0.0,total_distance = 0.0;
            
            for(int i = 0; i < splitted_child.length;i++)
            {
                
                if(i == (splitted_child.length - 1))
                {
                    temp_distance += nn(Integer.parseInt(splitted_child[i]), Integer.parseInt(splitted_child[0]));
                    total_distance = temp_distance;
                    if(total_distance <= best_generation_distance || best_generation_distance == 0)
                    {
                        best_generation_distance = total_distance;
                        best_generation = "Generation "+generations_count;
                        best_genertion_path = s;
                        System.out.println("Shortest Distance So far is "+best_generation_distance+" Gotten in Child "+ children.indexOf(s) +" of "+best_generation+" with a City Travel Path of "+best_genertion_path);
                    }
                }
                else
                {
                    temp_distance += nn(Integer.parseInt(splitted_child[i]), Integer.parseInt(splitted_child[i+1]));
                }
            }
        }
        generations_count+=1;
        path.clear();
        path.addAll(children);
    }
    
    private static LinkedList<String> single_point(String[] parent1,String[] parent2)
    {
        LinkedList<String> children_path = new LinkedList<>();
   
        //get center of the parent for single point gene crossing
        int center = sr.nextInt((parent1.length-2) + 1) + 1;
        
        
        //create child from parents
        //child 1
        //for crossover method 4th parameter can be blank as the method only checks for two point
        String child1 = crossover(center, parent1, parent2, "");
        child1 = child1.substring(0, child1.length()-1);
        
        //child 2
        String child2 = crossover(center, parent2, parent1, "");
        child2 = child2.substring(0, child2.length()-1);
        
        //mutate child genes to prevent travelling to same city twice
        child1 = mutation(child1, parent1.length);
        child2 = mutation(child2, parent1.length);
        
        children_path.add(child1);
        children_path.add(child2);
        return children_path;
    }
    
    private static LinkedList<String> two_point(String[] parent1,String[] parent2)
    {
        LinkedList<String> children_path = new LinkedList<>();
   
        //get center of the parent for single point gene crossing
        int center = parent1.length / 3;
        
        //create child from parents
        //child 1
        //for crossover method 4th parameter can be blank as the method only checks for two point
        String child1 = crossover(center, parent1, parent2, "two point");
        child1 = child1.substring(0, child1.length()-1);
        
        
        //child 2
        String child2 = crossover(center, parent2, parent1, "two point");
        child2 = child2.substring(0, child2.length()-1);
        
        child1 = mutation(child1, parent1.length);
        child2 = mutation(child2, parent1.length);
        
        children_path.add(child1);
        children_path.add(child2);
        
        return children_path;
    }
    private static String mutation(String child_path,int parent_length)
    {
        String result = "nill";
        int fail_counter = 0;
        
        for(;;)
        {
            //create a list to hold city_no to be used to prevent travelling to the same city twice
            LinkedList<Integer> cities = new LinkedList<>();
            LinkedList<Integer> init_cities = new LinkedList<>();
            LinkedList<Integer> cities_index_to_be_mutated = new LinkedList<>();
            int cities_no_to_be_mutated_count = 0;
            for(int i = 1; i <= parent_length;i++)
            {
                init_cities.add(i);
            }
            cities.addAll(init_cities);
            //converted mutation to method, so adjustments had to be made
            LinkedList<Integer> children = new LinkedList<>();
            for(String s : child_path.split(","))
            {
                children.add(Integer.parseInt(s));
            }
            //mutation phase
            //phase 1: get duplicates
            for(int i = 0; i < children.size();i++)
            {
                if(cities.contains(children.get(i)))
                {
                    cities.remove(cities.indexOf(children.get(i)));
                }
                else
                {
                    cities_index_to_be_mutated.add(i);
                    cities_no_to_be_mutated_count+=1;
                }
            }

            //phase 2: replace duplicates
            for(int i = 0; i < cities_no_to_be_mutated_count;i++)
            {
                int replace_city = sr.nextInt(cities.size());
                //default remove parameter is object, cast to use overloaded int parameter of the remove method
                children.remove((int)(cities_index_to_be_mutated.get(i)));
                children.add(cities_index_to_be_mutated.get(i), cities.get(replace_city));
                cities.remove(cities.indexOf(cities.get(replace_city)));

            }
            
            //phase 3: Swapping
            //swap 4 indexes in the children randomly in 2s
            LinkedList<Integer> swapped_indexes = new LinkedList<>();
            for(int i = 0; i < 2; i++)
            {
                int swap_index_1 = sr.nextInt(parent_length - 2 + 1) + 1;
                int swap_index_2 = sr.nextInt(parent_length - 4 + 1) + 1;
                //keep on generating indexes until 2 different indexes that arent already swapped are created.
                for(;;)
                {
                    if((swap_index_1 != swap_index_2 && (!swapped_indexes.contains(swap_index_1) || !swapped_indexes.contains(swap_index_2))))
                    {
                        break;
                    }
                    swap_index_1 = sr.nextInt(parent_length - 2 + 1) + 1;
                    swap_index_2 = sr.nextInt(parent_length - 4 + 1) + 1;
                }
                int prev_value_index1 = children.get(swap_index_1);
                int prev_value_index2 = children.get(swap_index_2);
                
                children.remove((int)swap_index_1);
                children.add(swap_index_1, prev_value_index2);
                //second random index
                children.remove((int)swap_index_2);
                children.add(swap_index_2, prev_value_index1);
                
                swapped_indexes.add(swap_index_1);
                swapped_indexes.add(swap_index_2);
            }
            
            result = "";
            for(Integer i : children)
            {
                result += i+",";
            }
            result = result.substring(0, result.length() - 1);
            fail_counter += 1;
            
            if(!unique.contains(result) || fail_counter == _10P)
            {
                if(fail_counter != _10P)
                {
                    unique.add(result);
                }
                
                break;
            }
        }
        
        
        return result;
    }
    private static String crossover(int center,String[] parent1,String[] parent2,String type)
    {
        String child = "";
        
        for(int i = 0; i < center;i++)
        {
            child += parent1[i]+",";
        }
        //create genes for two point cross over
        if(type.equalsIgnoreCase("two point"))
        {
            for(int i = center; i < (center * 2);i++)
            {
                child += parent2[i]+",";
            }
            
            for(int i = (center * 2);i < parent1.length;i++)
            {
                child += parent1[i]+",";
            }
        }
        else
        {
            //genes for single point
            for(int i = center;i < parent1.length;i++)
            {
                child += parent2[i]+",";
            }
        }
        return child;
    }
}