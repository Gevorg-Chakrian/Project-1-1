package UpperBound.GA;

public class HeapSort 
{
    /**
     * Sorting of the array by higher fitness
     * @param ind array of individual to sort
     */
    public static void sort(Individual[] ind)
    {
        int length=ind.length;
        
        for(int i=length/2;i>0;i--) 
            downheap(ind,i,length);

        do 
        {
            length=length-1;

            Individual temp=ind[0];
            ind[0]=ind[length-1];
            ind[length-1]=temp;
            downheap(ind,1,length);
        } 
        while(length>1);
    }
    
    /**
     * Downheap of the sorting
     * @param ind array of individual to sort
     * @param i stage of the heap sort
     * @param length actual length considered
     */
    private static void downheap(Individual[] ind, int i, int length)
    {
        Individual temp=ind[i-1];
        
        while(i<=length/2) 
        {
            int j=2*i;
            if((j<length) && (ind[j-1].getFitness()>ind[j].getFitness())) 
                j++;
            
            if(temp.getFitness() <= ind[j-1].getFitness()) 
                break;
            else 
            {
                ind[i-1]=ind[j-1];
                i=j;
            }
        }
        ind[i-1]=temp;
    }
}

