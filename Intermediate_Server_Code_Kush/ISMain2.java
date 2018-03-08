import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import xmlParser.Action;
import xmlParser.Parser;

public class ISMain2 {
	private static long sleepDuration = 5000;
	private static String nfsShareLocation = "/mnt/nfs-share/HealthCare/";

	private static String jarPath;
	private static String className;
	private static Integer id;
	private static String location = "INTERMEDIATE SERVER";
	private static String xmlLocation = nfsShareLocation + "app.xml";
	private static List<Action> actionData;
	
	public static void parseXML() {
		// get the jarnames and classnames from xml
		// run the method only if the location in xml is GATEWAY
		//Parser parser = new Parser("/mnt/nfs-share/HealthCare/app.xml");
		Parser parser = new Parser(xmlLocation);
		actionData = parser.getActionData();
		// jarPath = actionData.get(0).getActionJarLocation();
		// className = actionData.get(0).getClassDescription();
	}
	
	public static Object loadClass() {		
		JarFile jarFile;
		Object o = null;
		Class<?> c = null;
		
		try {
			jarFile = new JarFile(jarPath);
		
	        Enumeration<JarEntry> e = jarFile.entries();
	        URL[] urls = { new URL("jar:file:" + jarPath+"!/") };
	        URLClassLoader cl = URLClassLoader.newInstance(urls);
	        
			
	        while (e.hasMoreElements()) {
	            JarEntry je = e.nextElement();	         
	            
	            String jarClassName = je.getName().substring(0,je.getName().length()-6);
	            jarClassName = jarClassName.replace('/', '.');
	            System.out.println("jarClassName: " + jarClassName);
	            //System.out.println("jarClassName is " + className);
	            
	            if(jarClassName.equals(className)) {	            	
		            c = cl.loadClass(jarClassName);
		            o = c.newInstance();
		            return o;
	            }
	        }	        	       
		
		} catch (IOException e1) {
			System.out.println("IOException");
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return o;
	}
	
	public static void main(String[] args) throws Exception {
		parseXML();
		
	//	Object classObject = loadClass();
	//	System.out.println("classObject is: " + classObject);
		
		Properties prop = new Properties();
		InputStream input = null;
		String configPath = "/mnt/nfs-share/IAS/ip_config.properties";
		String mqServerPath = null;
		try {
			input = new FileInputStream(configPath);
			prop.load(input);
			
			System.out.println(prop.getProperty("MQSERVER"));
			mqServerPath = prop.getProperty("MQSERVER");
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("received val = " + mqServerPath);
		
		
	
		String QUEUE_NAME = "ISQueue" + mqServerPath;
		String tempIpPort = mqServerPath;
		
		 ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost(mqServerPath.split(":")[0]);
		    
		    factory.setPort(Integer.parseInt(mqServerPath.split(":")[1]));
		    factory.setUsername("admin");
		    factory.setPassword("admin"); 
		    Connection connection = factory.newConnection();
		    Channel channel = connection.createChannel();

		    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		    Consumer consumer = new DefaultConsumer(channel) {
		      @Override
		      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
		          throws IOException {
		        String message = new String(body, "UTF-8");
		        System.out.println(" [x] Received '" + message + "'");
		        processMessage(message);
		        addToCentralServerQueue(message, tempIpPort);
		      }
		    };
		    channel.basicConsume(QUEUE_NAME, true, consumer);
		
		
		    
		
	} // main
	
	
	
	public static void processMessage(String message)
	{
		
		String[] strs = message.split("#");
		//strs[1] contains "bp1" "hb1" etc search xml based on these values
		
		
		for(Action action : actionData)
		{
			if(action.getSensorActionId().equals(strs[1]) && action.getLocation().equals(location))
			{
				jarPath = action.getActionJarLocation();
				className = action.getClassDescription();
				id = Integer.parseInt(action.getEventId());
				
				Object classObject = loadClass();
				
				
				Class[] partypes = new Class[2];
				partypes[0] = Integer.class; //Check if string or integer
				partypes[1] = List.class;
				
				List<Object> listArgs = new ArrayList<>();
				listArgs.add(strs[0]);
				
				Object[] argToRun = new Object[2];
				argToRun[0] = id;
				argToRun[1] = listArgs;
				
				try {
					Method method = classObject.getClass().getMethod("run", partypes);
					method.invoke(classObject, argToRun);											
					
					
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	public static void addToCentralServerQueue(String message , String ip_port)
	{
		System.out.println("adding to central queue");
		
		String dataToBeSent = message;
	
		final String QUEUE_NAME = "CSQueue" + ip_port;

		ConnectionFactory factory = new ConnectionFactory();
	    	    
	    factory.setHost(ip_port.split(":")[0]);
	    factory.setPort(Integer.parseInt(ip_port.split(":")[1]));
	    factory.setUsername("admin");
	    factory.setPassword("admin");
	    
	    Connection connection;
		try {
			connection = factory.newConnection();
		
		    Channel channel = connection.createChannel();
	
		    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		    
		    channel.basicPublish("", QUEUE_NAME, null, dataToBeSent.getBytes("UTF-8"));
		    System.out.println(" [x] Sent '" + dataToBeSent + "'");
	
		    channel.close();
		    connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}	// end of class
		
		
	
			