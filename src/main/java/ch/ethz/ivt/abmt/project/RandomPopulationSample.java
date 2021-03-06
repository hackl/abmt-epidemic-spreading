package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Population;

import java.util.*;

/**
 * This class selects randomly a sample of x persons out of the population.
 *
 */

public class RandomPopulationSample {
    // initialize variables
    private Population population;
    private int samples = 1;

    public RandomPopulationSample(int samples, Population population){
        // sample size
        this.samples = samples;
        // population of the scenario
        this.population = population;
    }

    public Set<Id> randomPopulation() {
        // import random process
        Random random = new Random();

        // generate a list of keys for all persons in the population
        List<Id> keys = new ArrayList<Id>(this.population.getPersons().keySet());

        // initialize an empty list
        List<Id> randomKeys = new ArrayList<Id>();
        // loop through the sample size
        for (int i = 0; i<this.samples; i++){
            // assigning randomly person ids to the list
            int index = random.nextInt(keys.size());
            randomKeys.add(keys.get(index));
            keys.remove(index);
        }
        // returns a list with random person ids
        Set<Id> randomPersonsIds = new HashSet<Id>(randomKeys);
        return randomPersonsIds;
    }

}
