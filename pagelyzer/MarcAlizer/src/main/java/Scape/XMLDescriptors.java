package Scape;


import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class XMLDescriptors {

	static Document documentDelta;
	static Document documentViXML1;
	static Document documentViXML2;
	public static boolean split = true; 
	// to split the urls like http://archive.org/200924351234/http://anydomaincrawled
	// after split only the second part of the url with the date is used
	public static double SVM_VALUE=1;
	// this value means that if there is no items in blocks like links etc. two blocks are considered as similar
	/*public static boolean run(String fichierXml1, String fichierXml2, String fichierDelta, ArrayList<Double> desc) {
		
		Element rootViXML1;
		Element rootViXML2;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			// IT was used for the files created already on the disk by using VIPS
			
			//	documentViXML1 = builder.parse(new File(fichierXml1)); // for test
			//	documentViXML2 = builder.parse(new File(fichierXml2));
			
			documentViXML1 = builder.parse(new InputSource(new StringReader(fichierXml1)));
			documentViXML2 = builder.parse(new InputSource(new StringReader(fichierXml2)));
			
			//Le parsing est terminé ;)
		}
		catch(Exception e){
			e.printStackTrace();
		}

		
		rootViXML1 = documentViXML1.getDocumentElement();
		rootViXML2 = documentViXML2.getDocumentElement();
		
		NodeList nodeLstsource = rootViXML1.getElementsByTagName("Block");
	    NodeList nodeLstversion = rootViXML2.getElementsByTagName("Block");
	   
	    if(nodeLstsource.getLength() == nodeLstversion.getLength() && nodeLstsource.getLength()!=0) // discard pages with no block
	    {	
			//desc.add(BlockBasedContent(0,nodeLstsource, nodeLstversion,"Adr"));// links
			//desc.add(BlockBasedContent(0,nodeLstsource, nodeLstversion,"Name"));// links
			//desc.add(BlockBasedContent(1,nodeLstsource, nodeLstversion,"Src"));// images
			//desc.add(BlockBasedContent(1,nodeLstsource, nodeLstversion,"Name"));// images
	    	desc.add(NewBlockBasedContent(2,nodeLstsource, nodeLstversion,"Txt"));// Text
			
			return true;
	    }
	    
	    return false;
		
	}*/
//Swetha commented above method and created the below run(), to get the text comparison right
public static boolean run(String fichierXml1, String fichierXml2/*, String fichierDelta*/, ArrayList<Double> desc) {
		
		Element rootViXML1;
		Element rootViXML2;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();			
			documentViXML1 = builder.parse(new InputSource(new StringReader(fichierXml1)));
			documentViXML2 = builder.parse(new InputSource(new StringReader(fichierXml2)));
		}
		catch(Exception e){
			e.printStackTrace();
		}		
		rootViXML1 = documentViXML1.getDocumentElement();
		rootViXML2 = documentViXML2.getDocumentElement();		
		NodeList nodeLstsource = rootViXML1.getElementsByTagName("Block");
	    NodeList nodeLstversion = rootViXML2.getElementsByTagName("Block");
	    Double results = 0.0;
	    //Check the no.of blocks are same
	    if(nodeLstsource.getLength() == nodeLstversion.getLength() && nodeLstsource.getLength()!=0) // discard pages with no block
	    {	
	    	//If the no.of blocks are same, check its content			
	    	Double resultsForAddrLink = BlockBasedContent(0,nodeLstsource, nodeLstversion,"Adr");// links		
	    	//If there is a mismatch in the Link Address, return the results as -1.0
	    	if(resultsForAddrLink < 1.0){
	    		System.out.println("Change in the link address");
	    		return false;	    		
	    	}	    		
	    	//If there is a mismatch in the Link Name, return the results as -1.0
			Double resultsForNameLink = BlockBasedContent(0,nodeLstsource, nodeLstversion,"Name");// links
			if(resultsForNameLink < 1.0){
				System.out.println("Change in the link name");
				return false;	
			}
	    		    	
			//If there is a mismatch in the Text content, return the results as -1.0
			Double resultsForTxt = BlockBasedContent(2,nodeLstsource, nodeLstversion,"Txt");// Text						
			if(resultsForTxt < 1.0){
				System.out.println("Change in the Text content");
				return false;	
			}
			return true;
			
	    }	    
	    System.out.println("Change in the no.of Blocks OR no Blocks");
	    return false;
		
	}
	
	private static double BlockBasedContent(int type, NodeList nodeLstsource, NodeList nodeLstversion, String atr)
	{ 
	    double resultoverblocks = 0;
	    HashMap<String, Double> temptext1;
	    HashMap<String, Double> temptext2 = null;
	    for(int i = 0; i< nodeLstsource.getLength();i++ )
	    {	
	    	if(((Element)nodeLstsource.item(i)).getAttribute("ID")!="" ) // 
	    	{
	    		//Compare the text based content
		    	if(type==2) 
		    	{
		    		// get first document text		    		
		    		Element el = (Element) nodeLstsource.item(i);	
		    	
		    		NodeList txtl = el.getElementsByTagName("Txts");
		    		if(txtl!=null&&txtl.item(0)!=null)
		    			temptext1 = CosineSimilarity.getFeaturesFromString(((Element)txtl.item(0)).getAttribute(atr));		    		
		    		else return 0;
		    		
		    		// second document text 
		    		el = (Element) nodeLstversion.item(i);
		    		txtl = el.getElementsByTagName("Txts");
		    		
		    		if(txtl!=null&&txtl.item(0)!=null)
		    		{		    			
		    			temptext2 = CosineSimilarity.getFeaturesFromString(((Element)txtl.item(0)).getAttribute(atr));		    			
		    		}
		    		else resultoverblocks += SVM_VALUE;		    		
		    		System.out.println(temptext1);
		    		System.out.println(temptext2);
		    		resultoverblocks = CosineSimilarity.calculateCosineSimilarity(temptext1, temptext2);
		    		//Below condition indicates that there was a mismatch in the text
		    		if(resultoverblocks < 0.99){
		    			return -1.0;
		    		}		    		
		    	}
		    	else if(type == 0){
		    		resultoverblocks = CosineIndexLinks((Element)nodeLstsource.item(i), (Element)nodeLstversion.item(i), split,atr);
		    		//Below condition indicates that there was a mismatch in either link address/link name
		    		if(resultoverblocks < 0.99){
		    			return -1.0;
		    		}	
		    	}
		    	else
		    		resultoverblocks += CosineIndexImages((Element)nodeLstsource.item(i), (Element)nodeLstversion.item(i), split,atr);
		    }
	    }	 	   
		
		return resultoverblocks;
	}
	
	
	/*private static double BlockBasedContent(int type, NodeList nodeLstsource, NodeList nodeLstversion, String atr)
	{ 
	    double resultoverblocks = 0;
	    
	    int count = 0;
	    HashMap<String, Double> temptext1;
	    HashMap<String, Double> temptext2 = null;
	    for(int i = 0; i< nodeLstsource.getLength();i++ )
	    {	
	    	if(((Element)nodeLstsource.item(i)).getAttribute("ID")!="" ) // 
	    	{
		    	if(type==2) // Txt
		    	{
		    		// get first document text		    		
		    		Element el = (Element) nodeLstsource.item(i);	
		    	
		    		NodeList txtl = el.getElementsByTagName("Txts");
		    		//Swetha need to chck this section for text comparison. Currently it is empty
		    		if(txtl!=null&&txtl.item(0)!=null)
		    			//The below method will replace any non-letter characters to nothing
		    			temptext1 = CosineSimilarity.getFeaturesFromString(((Element)txtl.item(0)).getAttribute(atr));
		    		//System.out.println;
		    		else return 0;
		    		
		    		// second document text 
		    		el = (Element) nodeLstversion.item(i);
		    		txtl = el.getElementsByTagName("Txts");
		    		
		    		if(txtl!=null&&txtl.item(0)!=null)
		    		{		    			
		    			temptext2 = CosineSimilarity.getFeaturesFromString(((Element)txtl.item(0)).getAttribute(atr));		    			
		    		}
		    		else resultoverblocks += SVM_VALUE;
		    		//System.out.println(CosineSimilarity.calculateCosineSimilarity(temptext1, temptext2));
		    		//if(temptext1.size()!=0 &&temptext2.size()!=0)
		    		resultoverblocks += CosineSimilarity.calculateCosineSimilarity(temptext1, temptext2);
		    		if(resultoverblocks < 0.0){
		    			count = -1;
		    			break;
		    		}
		    	}
		    	else if(type == 0){
		    		resultoverblocks += CosineIndexLinks((Element)nodeLstsource.item(i), (Element)nodeLstversion.item(i), split,atr);
		    		if(resultoverblocks < 0.0){
		    			count = -1;
		    			break;
		    		}
		    	}
		    	else
		    		resultoverblocks += CosineIndexImages((Element)nodeLstsource.item(i), (Element)nodeLstversion.item(i), split,atr);
		    }
	    }
	 // no block detected return 0.5 for training and also for tests
	    if(count == 0)
	    	return 0.5;
	    else if(count == -1){
	    	return -1.0;
	    }else
	    	count = nodeLstsource.getLength() ;
	   
		System.out.println(resultoverblocks/(double)count); //+ " -- " + count + " -- " + nodeLstsource.getLength());
		return resultoverblocks/(double)count;
	}*/
	

	public static void printTable(int[] table, String s){
		System.out.print(s + " :");
		for (int i = 0 ; i < table.length ; ++i){
			System.out.print(" " + table[i]);
		}
		System.out.println();
	}

	public static void writeTable(int[] table, String s){
		FileWriter writer = null;
		String texte = "";
		for (int i = 0 ; i < table.length -1; ++i){
			texte += table[i] + " ";
		}
		texte += table[table.length-1] + "\n";
		try{
		     writer = new FileWriter(s, true);
		     writer.write(texte,0,texte.length());
		}catch(IOException ex){
		    ex.printStackTrace();
		}finally{
		  if(writer != null){
		     try {
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
		  }
		}
	}
	
	public static void printTable(double[] table, String s){
		System.out.print(s + " :");
		for (int i = 0 ; i < table.length ; ++i){
			System.out.print(" " + table[i]);
		}
		System.out.println();
	}

	public static void writeTable(double[] table, String s){
		FileWriter writer = null;
		String texte = "";
		for (int i = 0 ; i < table.length -1; ++i){
			texte += table[i] + " ";
		}
		texte += table[table.length-1] + "\n";
		try{
		     writer = new FileWriter(s, true);
		     writer.write(texte,0,texte.length());
		}catch(IOException ex){
		    ex.printStackTrace();
		}finally{
		  if(writer != null){
		     try {
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
		  }
		}
	}
	
	public static int nbBlocksUpperBounded(Element e) {
		return nbBlocksUpperBounded(e, 10);
	}

	public static int nbBlocksUpperBounded(Element e, int nbBlockMax) {
		int a = nbBlocks(e);
		return (a>=nbBlockMax)?nbBlockMax:a;
	}

	public static boolean sameStructure(Element e1, Element e2){
		NodeList l1 = e1.getElementsByTagName("Block");
		NodeList l2 = e2.getElementsByTagName("Block");
		final int length = l1.getLength();
		if (length != l2.getLength()) {
			return false;
		}
		for (int i = 0 ; i < length ; ++i){
			if (!((Element)l1.item(i)).getAttribute("Ref").equals(((Element)l2.item(i)).getAttribute("Ref"))) {
				return false;
			}
		}
		return true;
	}

	public static int nbBlockString(Element e, String s) {
		NodeList l = e.getChildNodes();
		final int length = l.getLength();
		int result = 0;
		for (int i = 0 ; i < length ; ++i) {
			if (s.equals(l.item(i).getNodeName()))
				++result;
		}
		return result;
	}

	public static int nbNodeBlocks(Element eXML) {
		NodeList nl = eXML.getElementsByTagName("Block");
		final int length = nl.getLength();
		int result = 0;
		for (int i = 0 ; i < length ; ++i) {
			NodeList l = nl.item(i).getChildNodes();
			final int length2 = l.getLength();
			boolean addbool = true;
			for (int j = 0 ; j < length2 ; ++j) {
				if ("Block".equals(l.item(j).getNodeName())) {
					addbool = false;
					break;
				}
			}
			if (addbool)
				++result;
		}
		return result;
	}

	public static int nbString(Element e, String s) {
		return e.getElementsByTagName(s).getLength();
	}

	// ok
	public static int nbBlocks(Element e){
		return nbString(e, "Block");
	}

	// ok
	public static int nbInsertAllTree(Element e){
		return nbString(e, "Insert");
	}

	// ok
	public static int nbDeleteAllTree(Element e){
		return nbString(e, "Delete");
	}

	// ok
	public static int nbUpdateAllTree(Element e){
		return nbString(e, "Update");
	}

	// ok
	public static int nbDelete(Element e) {
		return nbBlockString(e, "Delete");
	}

	// ok
	public static int nbInsert(Element e) {
		return nbBlockString(e, "Insert");
	}

	// ok
	public static int nbUpdate(Element e) {
		return nbBlockString(e, "Update");
	}	

	// ok
	public static double ratioString(Element eDelta, Element eXML, String s) {
		return ((double) nbBlockString(eDelta, s))/nbBlocks(eXML);
	}


	// ok : ratio de blocks mis a jour
	public static double ratioUpdate(Element eDelta, Element eXML) {
		return ratioString(eDelta, eXML, "Update");
	}

	public static double ratioInsert(Element eDelta, Element eXML) {
		return ratioString(eDelta, eXML, "Insert");
	}

	public static double ratioDelete(Element eDelta, Element eXML) {
		return ratioString(eDelta, eXML, "Delete");
	}

	// ok
	public static double ratioNodeString(Element eDelta, Element eXML, String s) {
		return ((double) nbBlockString(eDelta, s))/nbNodeBlocks(eXML);
	}
	// ok : ratio de blocks mis a jour
	public static double ratioNodeUpdate(Element eDelta, Element eXML) {
		return ratioNodeString(eDelta, eXML, "Update");
	}

	public static double ratioNodeInsert(Element eDelta, Element eXML) {
		return ratioNodeString(eDelta, eXML, "Insert");
	}

	public static double ratioNodeDelete(Element eDelta, Element eXML) {
		return ratioNodeString(eDelta, eXML, "Delete");
	}


	public static double ratioNodeString(Element eDelta, Element eXML1, Element eXML2, String s, boolean min) {
		if (min)
			return Math.min(ratioNodeString(eDelta, eXML1, s), ratioString(eDelta, eXML2, s));
		return Math.max(ratioNodeString(eDelta, eXML1, s), ratioString(eDelta, eXML2, s));
	}

	public static double minRatioNodeUpdate(Element eDelta, Element eXML1, Element eXML2) {
		return ratioNodeString(eDelta, eXML1, eXML2, "Update", true);
	}

	public static double maxRatioNodeUpdate(Element eDelta, Element eXML1, Element eXML2) {
		return ratioNodeString(eDelta, eXML1, eXML2, "Update", false);
	}

	public static double minRatioNodeInsert(Element eDelta, Element eXML1, Element eXML2) {
		return ratioNodeString(eDelta, eXML1, eXML2, "Insert", true);
	}

	public static double maxRatioNodeInsert(Element eDelta, Element eXML1, Element eXML2) {
		return ratioNodeString(eDelta, eXML1, eXML2, "Insert", false);
	}

	public static double minRatioNodeDelete(Element eDelta, Element eXML1, Element eXML2) {
		return ratioNodeString(eDelta, eXML1, eXML2, "Delete", true);
	}

	public static double maxRatioNodeDelete(Element eDelta, Element eXML1, Element eXML2) {
		return ratioNodeString(eDelta, eXML1, eXML2, "Delete", false);
	}


	public static double ratioString(Element eDelta, Element eXML1, Element eXML2, String s, boolean min) {
		if (min)
			return Math.min(ratioString(eDelta, eXML1, s), ratioString(eDelta, eXML2, s));
		return Math.max(ratioString(eDelta, eXML1, s), ratioString(eDelta, eXML2, s));
	}

	public static double minRatioUpdate(Element eDelta, Element eXML1, Element eXML2) {
		return ratioString(eDelta, eXML1, eXML2, "Update", true);
	}

	public static double maxRatioUpdate(Element eDelta, Element eXML1, Element eXML2) {
		return ratioString(eDelta, eXML1, eXML2, "Update", false);
	}

	public static double minRatioInsert(Element eDelta, Element eXML1, Element eXML2) {
		return ratioString(eDelta, eXML1, eXML2, "Insert", true);
	}

	public static double maxRatioInsert(Element eDelta, Element eXML1, Element eXML2) {
		return ratioString(eDelta, eXML1, eXML2, "Insert", false);
	}

	public static double minRatioDelete(Element eDelta, Element eXML1, Element eXML2) {
		return ratioString(eDelta, eXML1, eXML2, "Delete", true);
	}

	public static double maxRatioDelete(Element eDelta, Element eXML1, Element eXML2) {
		return ratioString(eDelta, eXML1, eXML2, "Delete", false);
	}

	public static int containsStringAdvertisement(Element e, String s, String s2, String s3) {
		final NodeList nl = e.getElementsByTagName(s);
		final int length1 = nl.getLength();
		Element eAux = null;
		NamedNodeMap nnm = null;
		int result = 0;
		for (int i = 0 ; i < length1 ; ++i) {
			final NodeList nl2 = nl.item(i).getChildNodes();
			final int length2 = nl2.getLength();
			for (int j = 0 ; j < length2 ; ++j) {
				if (s2.equals((eAux = (Element) nl2.item(j)).getNodeName())) {
					final NodeList nl3 = eAux.getChildNodes();
					final int length3 = nl3.getLength();
					for (int k = 0 ; k < length3 ; ++k) {
						if (s3.equals((nl3.item(k).getNodeName()))) {
							boolean pub = false;
							nnm = nl3.item(k).getAttributes();
							final int length4 = nnm.getLength();
							for (int l = 0 ; l < length4 ; ++l){
								if ((nnm.item(l).toString().contains("publicit")) || (nnm.item(l).toString().contains("advertise"))){
									pub = true;
									break;
								}
							}
							if (!pub) {
								++result;							
							}
						}
					}
				}
			}
		}
		return result;
	}


	public static boolean containsString(Element e, String s) {
		return (e.getElementsByTagName(s).getLength() != 0);
	}

	public static boolean containsDelete(Element e) {
		return containsString(e,"Delete");
	}

	public static boolean containsInsert(Element e) {
		return containsString(e,"Insert");
	}

	public static boolean containsUpdate(Element e) {
		return containsString(e,"Update");
	}

	public static boolean noInsertionNorDeletion(Element e) {
		return !containsInsert(e) && !containsDelete(e);
	}

	public static boolean containsLinks(Element e){
		return containsString(e, "link");
	}

	public static boolean containsImages(Element e){
		return containsString(e, "img");
	}

	public static Hashtable<String, Integer> getLinksOrImage(Element e, boolean split, String tag, String attribute){
		Hashtable<String, Integer> result = new Hashtable<String, Integer>();
		NodeList l = e.getElementsByTagName(tag);
		final int length = l.getLength();
		for (int i = 0 ; i < length ; ++i){
			String address = ((Element)l.item(i)).getAttribute(attribute);
			if (split) {
				String[] s = address.split("://");
				address = s[s.length-1];
			}
			result.put(address, 0);
		}
		return result;
	}
	
	public static HashMap<String, Double> getLinksOrImageMap(Element e, boolean split, String tag, String attribute){
		HashMap<String, Double> result = new HashMap<String, Double>();
		NodeList l = e.getElementsByTagName(tag);
		final int length = l.getLength();
		
		for (int i = 0 ; i < length ; ++i){
			String address = ((Element)l.item(i)).getAttribute(attribute);
			if (split) { // It depends if in the collection links are redirected to links in the archive
				
			
				String[] s = address.split("http://");
				if(s.length >1) // redirected 
				{
					String temp = s[s.length-2];
					address = s[s.length-1];
					String[] sredirect = temp.split("/");
					address =sredirect[sredirect.length-1] +"/" + address  ;
				}
				else
					address = s[s.length-1];
			}
			
			if(result.containsKey(address))
				result.put(address, result.get(address)+1);
			else 
				result.put(address, (double) 1);
			
			
		}
		return result;
	}

	public static Hashtable<String, Integer> getLinks(Element e, boolean split){
		return getLinksOrImage(e, split, "links", "Adr");
	}

	public static Hashtable<String, Integer> getImages(Element e, boolean split){
		return getLinksOrImage(e, split, "img", "Src");
	}

	public static double JaccardIndex(Element e1, Element e2, boolean split, String tag, String attribute){
		Hashtable<String, Integer> hash1 = getLinksOrImage(e1, split, tag, attribute);
		Hashtable<String, Integer> hash2 = getLinksOrImage(e2, split, tag, attribute);
		int nbCommon = 0; 
		for (String s : hash1.keySet()) {
			if (hash2.containsKey(s)) {
				++nbCommon;
			}
		}
		int a = (hash1.size()+hash2.size()-nbCommon);
		if (a == 0) {
			return 0;
		}
		return ((double)nbCommon)/a;
	}
	
	public static double CosineIndex(Element e1, Element e2, boolean split, String tag, String attribute){
		HashMap<String, Double> hash1 = getLinksOrImageMap(e1, split, tag, attribute);
		HashMap<String, Double> hash2 = getLinksOrImageMap(e2, split, tag, attribute);
		if(hash1.isEmpty()&&hash2.isEmpty())
			return SVM_VALUE;
		
		if(hash1.isEmpty()||hash2.isEmpty())
			return 0;//dissimilar
		
		return CosineSimilarity.calculateCosineSimilarity(hash1, hash2);
		
	}

	public static double CosineIndexLinks(Element e1, Element e2, boolean split,String attr){
		return CosineIndex(e1, e2, split, "link", attr);
	}
	
	public static double CosineIndexImages(Element e1, Element e2, boolean split,String attr){
		return CosineIndex(e1, e2, split, "img", attr);
	}
	
	public static double JaccardIndexLinks(Element e1, Element e2, boolean split,String attr){
		return JaccardIndex(e1, e2, split, "link", attr);
	}
	
	public static double JaccardIndexImages(Element e1, Element e2, boolean split,String attr){
		return JaccardIndex(e1, e2, split, "img", attr);
	}

	public static double JaccardIndexIDLinks(Element e1, Element e2, boolean split){
		return JaccardIndex(e1, e2, split, "link", "ID");
	}

	public static double JaccardIndexIDImages(Element e1, Element e2, boolean split){
		return JaccardIndex(e1, e2, split, "img", "ID");
	}

	public static int nbChooseAdvertisement(Element eXML, String s, boolean advertisement){
		int result = 0;
		NodeList nl = eXML.getElementsByTagName(s);
		final int length = nl.getLength();
		if (advertisement) {
			return length;
		}
		for (int i = 0 ; i < length ; ++i) {
			boolean pub = false;
			NamedNodeMap nnm = nl.item(i).getAttributes();
			final int length2 = nnm.getLength();
			for (int l = 0 ; l < length2 ; ++l){
				if ((nnm.item(l).toString().contains("publicit")) || (nnm.item(l).toString().contains("advertise"))){
					pub = true;
					break;
				}
			}
			if (!pub){
				++result;
			}
		}
		return result;
	}

}

/***** OLD FUNCTION THAT COMPARES WITHOUT BLOCKS************/
/*private static double WithoutBlock(int type, Element rootViXML1, Element rootViXML2, String atr)
{ 
double resultoverblocks = 0;

int count = 0;
HashMap<String, Double> temptext1 = null;
HashMap<String, Double> temptext2 = null;
double toadd = 0;

    	if(type==2) // Txt
    	{
    

    		String text1 = "";
    		String text2 = "";
    		NodeList txtl = rootViXML1.getElementsByTagName("Txts");
    		if(txtl!=null&&txtl.item(0)!=null)
    		{
    			for(int k=0;k<txtl.getLength();k++)
    				text1+=((Element)txtl.item(k)).getAttribute(atr);
    			
    			temptext1 = CosineSimilarity.getFeaturesFromString(text1);
    		}
    		
    		
    		// second document text 
    		
    		txtl = rootViXML2.getElementsByTagName("Txts");
    		if(txtl!=null&&txtl.item(0)!=null)
    		{
    			for(int k=0;k<txtl.getLength();k++)
    				text2+=((Element)txtl.item(k)).getAttribute(atr);
    			
    			temptext2 = CosineSimilarity.getFeaturesFromString(text2);
    		}
    		
    		
    		if(temptext1!=null && temptext2!=null && temptext1.size()!=0 &&temptext2.size()!=0)
    			toadd = CosineSimilarity.calculateCosineSimilarity(temptext1, temptext2);
    		if(toadd != -1000)
    			resultoverblocks = toadd;
    		else 
    			{//no image;
    			 resultoverblocks = SVM_VALUE;
    			 }
    		
    		
    	}
    	else if(type==0)
    	{

    		toadd = //JaccardIndexLinks(rootViXML1, rootViXML2, false, atr);//
    				CosineIndexLinks(rootViXML1, rootViXML2, true,atr);
    		if(toadd != -1000)
    			resultoverblocks = toadd;
    		else 
			{//no link;
			 resultoverblocks = SVM_VALUE;}
    		
    	}
    	else
    	{
    		toadd = CosineIndexImages(rootViXML1, rootViXML2, true,atr);
    		if(toadd != -1000)// most of the  time happens because of segmentation error
    			resultoverblocks = toadd;
    	
    		else 
			{
			 resultoverblocks = SVM_VALUE;}
    	}
    //resultoverblocks = toadd;
	System.out.println(resultoverblocks);
	
return resultoverblocks;


}*/