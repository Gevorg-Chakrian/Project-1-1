package UpperBound.GA;

import java.util.Arrays;

public class Individual {
	
	int[] chromosome;
	int fitness;
	
	/**
     * Constructor of an individual
     * @param chromosome the list of genes of the individual
     */
	public Individual(int[] chromosome) {
		this.chromosome = chromosome;
		this.fitness = -1;
	}

	/**
     * Getter of a chromosome
     * @return chromosome the list of genes of the individual
     */
	public int[] getChromosome() {
		return chromosome;
	}

	/**
     * Setter of a chromosome
     * @param chromosome the list of genes of the individual
     */
	public void setChromosome(int[] chromosome) {
		this.chromosome = chromosome;
	}

	/**
     * Getter of the fitness
     * @return fitness of the individual
     */
	public int getFitness() {
		return fitness;
	}

	/**
     * Setter of the fitness
     * @param fitness fitness of the individual
     */
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
}
