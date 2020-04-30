package Scape;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CosineSimilarity {

/**
* calculate the cosine similarity between feature vectors of two clusters
*
* The feature vector is represented as HashMap<String, Double>.
*
* @param firstFeatures The feature vector of the first cluster
* @param secondFeatures The feature vector of the second cluster
* @return the similarity measure
*/
	public static Double calculateCosineSimilarity(HashMap<String, Double> firstFeatures, HashMap<String, Double> secondFeatures) {
		
		Double fnorm = 0.0;	// the first part of the denominator of the cosine similarity
		Double snorm = 0.0;	// the second part of the denominator of the cosine similarity
		Set<String> fkeys = firstFeatures.keySet();
		Iterator<String> fit = fkeys.iterator();
		boolean keysNotMatch = false;
		boolean dissimilar = false;
		StringBuffer buf = new StringBuffer();
		while (fit.hasNext()) 
		{
			String featurename = fit.next();
			boolean containKey = secondFeatures.containsKey(featurename);		
			//Swetha added below code to identify the missing keywords 
			if (!containKey) 
			{
				//Check if any of the keys are missing 
				keysNotMatch = true;
				//List all the missing keywords for our reference
				buf.append(featurename+" ");	
			}		
		}
		
		//If Keys are matching, validate if the occurrences of keys are matching
		if(!keysNotMatch){
			fnorm = calculateNorm(firstFeatures);	
			snorm = calculateNorm(secondFeatures);
			//If occurrences of the text doesnt match, return as dissimilar
			if(Double.compare(fnorm, snorm) != 0){
				System.out.println("fnorm - "+fnorm+" "+"snorm - "+snorm);
				System.out.println("Values are not matching");
				dissimilar = true;
			}
		}else{
			dissimilar = true;
			System.out.println("Keys that are not matching are - "+buf);
		}
		
		if(dissimilar){
			return -1.0;
		}else
			return 1.0;				
	}

/**
* calculate the norm of one feature vector
*
* @param feature of one cluster
* @return
*/
	public static Double calculateNorm(HashMap<String, Double> feature) {
		Double norm = 0.0;
		Set<String> keys = feature.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
		String featurename = it.next();
		norm = norm + Math.pow(feature.get(featurename), 2);
		//System.out.println("featurename - "+featurename+" norm - "+norm+" "+Math.pow(feature.get(featurename), 2));
		}
		return Math.sqrt(norm);
	}
	
	
	public static HashMap<String, Double> getFeaturesFromString(String text)
	{
		HashMap<String, Double> results = new HashMap<String, Double>();
		//text = text.replaceAll("\\P{L}+\\s", "");// replace any non-letter characters with nothing.
		String[] tokens = text.split(" ");
		for(int i=0;i<tokens.length;i++)
		{
			if(results.containsKey(tokens[i]))
				results.put(tokens[i], results.get(tokens[i])+1);
			else 
				results.put(tokens[i], (double) 1);
			
			
		}
		return results;
		
	}
	
	public static void main(String[] args) {
		 /*double d1 = 26.5329983228432;
	      double d2 = 26.60826939130014;*/
	      double d1 = 26.12;
	      double d2 = 26.00;
	      int retval = Double.compare(d1, d2);
	      System.out.println(retval);
	      if(retval > 0) {
	         System.out.println("d1 is greater than d2");
	      } else if(retval < 0) {
	        System.out.println("d1 is less than d2");
	      } else {
	         System.out.println("d1 is equal to d2");
	      }
	}
	
}

