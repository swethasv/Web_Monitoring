package pagelyzerSchedular;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

import pagelyzer.TestWebMonitoring;



public class TimerExample extends TimerTask{
	
	private String name ;
	public TimerExample(String n){
	  this.name=n;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		 System.out.println(Thread.currentThread().getName()+" "+name+" the task has executed successfully "+ new Date());
		    if("Task1".equalsIgnoreCase(name)){
		    	TestWebMonitoring test = new TestWebMonitoring();
		    	
		    	try {
		    		test.TestONLINEfromUrls();
				      Thread.sleep(10000);
				      } catch (InterruptedException e) {
				        // TODO Auto-generated catch block
				        e.printStackTrace();
				      } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    }
	}   

}
