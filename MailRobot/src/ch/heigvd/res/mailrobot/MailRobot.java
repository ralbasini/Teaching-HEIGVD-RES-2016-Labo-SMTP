package ch.heigvd.res.mailrobot;

import ch.heigvd.res.mailrobot.config.Organizer;
import ch.heigvd.res.mailrobot.model.prank.Prank;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * This is the main class of our programm. Its role is to get the properties from the
 * 'appconfig.properties' file, and to generate a Prank
 *
 * @author Romain, Dardan
 */
public class MailRobot {

   public static void main(String[] args) throws IOException {

      try {

         // Variable declaration
         String smtpServerAddress = "";
         int smtpServerPort = 0;
         int numberOfGroups = 0;
         String emails = ""; // Path to the emails.txt file
         String messages = ""; // Path to the messages.txt file
         Properties prop = new Properties();

         try {

            // Gets the info contained in the .properties file
            String filename = "appconfig.properties";

            BufferedReader br = new BufferedReader(new FileReader(filename));
            prop.load(br);

            smtpServerAddress = prop.getProperty("smtpServerAddress");
            smtpServerPort = Integer.parseInt(prop.getProperty("smtpServerPort"));
            numberOfGroups = Integer.parseInt(prop.getProperty("numberOfGroups"));
            emails = prop.getProperty("emails");
            messages = prop.getProperty("messages");

            // Debug info
            /*System.out.println("Infos obtenues");
             System.out.println("smtpServerAddress : " + smtpServerAddress);
             System.out.println("smtpServerPort : " + smtpServerPort);
             System.out.println("numberOfGroups : " + numberOfGroups);
             System.out.println("emails : " + emails);
             System.out.println("messages : " + messages);
             System.out.println("");*/
            
         } catch (IOException ex) {
            ex.printStackTrace();
         }

         // Creates an Organizer with the given parameters
         Organizer o = new Organizer(emails, numberOfGroups, messages);

         // Debug info
         //o.display();
         
         // Creates a new Prank if the organizer is valid
         if (o.isValid()) {
            
            // Debug info
            System.out.println("Organizer valid !");
            Prank p = new Prank(smtpServerAddress, smtpServerPort, o.getGroupsTab(), o.getMessagesTab());
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
