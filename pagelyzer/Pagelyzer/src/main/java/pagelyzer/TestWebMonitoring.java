package pagelyzer;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;

import pagelyzer.Capture;
import pagelyzer.JPagelyzer;



public class TestWebMonitoring {

	/**
	 * @param args
	 * @throws IOException 
	 * args[0]  is a file that contains list of urls pairs to test url1 \t url2 
	 * args[1] = config file the same used with pagelyzer 
	 * args[2] = path file to save the results 
	 * To run this test you shoud use hybrid settings as a default type to optimize other tests (not to use capture for each type of tests)
	 */
	public static void main(String[] args) throws IOException {				
		
		TestONLINEfromUrls();
	}
	
	public static void TestONLINEfromUrls() throws IOException 
	{
		//Provide web page URL which has to be monitored
	   String websiteURL1 = "https://www.csudh.edu/alert/";
	   String websiteURL2 = "https://www.csudh.edu/alert/";
	   //Provide the config_content.xml path
	   String[] pagelyzerargs = {"-config","D:/EducationVerticle/WebMonitoring/pagelyzer/Example_configFiles/config_content.xml","-url1",websiteURL1 ,"-url2",websiteURL2};
	   //Download the chromedriver and place it in a path
	   System.setProperty("webdriver.chrome.driver","D:/Softwares/ChromeDriver/chromedriver.exe");	        

	        JPagelyzer pagelyzer = new JPagelyzer(pagelyzerargs,false);
	        Capture capture1;
	        Capture capture2;
	        StringBuffer sb = new StringBuffer();
	        
	        double score = 0;     
	            capture1 = pagelyzer.GetCapture(websiteURL1, pagelyzer.browser1);
	            capture2 = pagelyzer.GetCapture(websiteURL2, pagelyzer.browser2);	            

	            if(capture1!=null && capture2!=null){
	            	//Compare the web pages 
	            	score = pagelyzer.CallMarcalizerResult(capture1, capture2);
	            }
              try {
					capture1.cleanup();
					capture2.cleanup();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
			e.printStackTrace();
		}
      sb.append(System.currentTimeMillis()+" " +"Web Page Score is "+score  + "\n");
      
      //If score is -1.0, then the web pages are dissimilar
      System.out.println("Web Monitoring Score is "+score);
      
      //Place the web page score in a text file as given below
      BufferedWriter writer = new BufferedWriter(
              new FileWriter("D:/EducationVerticle/results.txt", true)  //Set true for append mode
          );  
		writer.newLine();   //Add new line
		writer.write(sb.toString());
		writer.close();
	}
	
}
