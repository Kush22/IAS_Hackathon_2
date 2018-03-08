

import com.rabbitmq.client.Channel;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.*;
import java.util.*;
import java.sql.*;
public class Recv {


	public static void main(String[] argv) throws Exception {
		Properties prop = new Properties();
		InputStream input = null;
		String configPath = "ip_config.properties";
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
		final  String QUEUE_NAME = "CSQueue" + mqServerPath;
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(mqServerPath.split(":")[0]);
    
    factory.setPort(Integer.parseInt(mqServerPath.split(":")[1]));
    factory.setUsername("admin");
    factory.setPassword("admin"); 
    com.rabbitmq.client.Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");


     // db connection config


	
			try {
				
				System.out.println(prop.getProperty("MYSQL"));
				String ipPort = prop.getProperty("MYSQL");
				System.out.println(" got db ip port " + ipPort);
			}
			catch(Exception e) {
				System.out.println(e.getMessage());
			}

    	
    	
		 System.out.println(" connecting to database at :" + prop.getProperty("MYSQL"));
		  String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		  String DB_URL = "jdbc:mysql://"+prop.getProperty("MYSQL")+"/healthcare";
		  String USER = "gaurav";
		  String PASS = "gaurav";
			 Class.forName("com.mysql.jdbc.Driver");
 			System.out.println("Connecting to a selected database...");
		      final Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");

		  
		      
		





	//




    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
          throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + message + "'");

	addMessageToDatabase(message, conn);


      }
    };
    channel.basicConsume(QUEUE_NAME, true, consumer);
  }


  public static void addMessageToDatabase(String message, Connection conn)
  {


			   
		   
		   String sensorData= message;
		   String data = sensorData.split("#")[0];
		   String id = sensorData.split("#")[1];
		   
		   
		Statement stmt = null;

		try{
		      
		      //STEP 4: Execute a query
		      System.out.println("Inserting records into the table...");
		      stmt = conn.createStatement();
		      
		      String sql = "INSERT INTO sensorData " +
		                   "VALUES (" + "'" + id + "'" + ","+  "'" + data + "'" + ")";
		      stmt.executeUpdate(sql);
		      
		      System.out.println("Inserted records into the table...");

		   }
		  catch(Exception e)
		 {

		 }
		   System.out.println("Goodbye!");


 }


}
