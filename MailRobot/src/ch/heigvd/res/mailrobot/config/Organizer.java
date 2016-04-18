package ch.heigvd.res.mailrobot.config;

import ch.heigvd.res.mailrobot.model.mail.Group;
import ch.heigvd.res.mailrobot.model.mail.Message;
import ch.heigvd.res.mailrobot.model.mail.Person;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Gathers all the informations used to create an SMTP connection.
 *
 * @author Romain, Dardan
 */
public class Organizer {
   
   /**
    * Constructor with the required arguments. Parses the message and email file, and
    * creates the wanted groups
    *
    */
   public Organizer(String emails, int numberOfGroups, String messages) throws FileNotFoundException, IOException {

      messagesTab = readMessages(messages);
      emailsTab = readEmails(emails);
      groupsTab = generateGroups(emailsTab, numberOfGroups);

   }

   /**
    * Parses the messages file. Returns a Vector containing all the messages.
    *
    */
   public Vector<Message> readMessages(String messages) throws FileNotFoundException, IOException {
      Vector<Message> messagesTab = new Vector<>(0);

      File file = new File(messages);

      try {

         BufferedReader br = new BufferedReader(new FileReader(file));
         String line;

         String msg = "";
         while ((line = br.readLine()) != null) {
            while (!line.equals("====")) {
               msg += line + "\n";
               line = br.readLine();
            }

            // Deletes the last \n
            msg = msg.substring(0, msg.length() - 1);

            // Adds the message to messagesTab
            messagesTab.add(new Message(msg));

            // Resets the String for further use
            msg = "";
         }

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      return messagesTab;
   }

   /**
    * Parses the emails file. Returns a Vector containing all the emails.
    *
    */
   public Vector<Person> readEmails(String emails) throws FileNotFoundException, IOException {
      Vector<Person> emailsTab = new Vector<>(0);

      File file = new File(emails);

      try {

         BufferedReader br = new BufferedReader(new FileReader(file));
         String line;

         String email = "";

         // Simply ready the full line and adds it the the emailsTab Vector
         while ((line = br.readLine()) != null) {
            emailsTab.add(new Person(line));
         }

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      return emailsTab;
   }

   /**
    * Generates the wanted number of groups given in parameters. Returns a Vector
    * containing all the groups.
    *
    */
   public Vector<Group> generateGroups(Vector<Person> emailsTab, int numberOfGroups) {
      Vector<Group> groupsTab = new Vector<>(0);

      System.out.println("[Groups: " + numberOfGroups + ", emails: " + emailsTab.size() + ", number of person in each group: " + emailsTab.size() / numberOfGroups + "]\n");

      // A group must consist of 1 sender and 2+ recipients.
      if (emailsTab.size() / numberOfGroups < 3) {
         System.out.println("Nombre de personnes insuffisantes par rapport au nombre de groupes demandés");
      } else {

         isValid = true;

         // Number of emails in each group. The last group can have less person than the others.
         int groupSize = (int) (((double) emailsTab.size() / numberOfGroups) + 0.9);
         int membersNb = 0;

         // Creates the wanted group number and their respective persons.
         for (int i = 0; i < numberOfGroups; ++i) {
            int indexDébut = i * groupSize;
            int indexFin = indexDébut + groupSize;

            // If the last group must have less members
            if (indexFin > emailsTab.size()) {
               indexFin = emailsTab.size();
            }

            // Adds the freshly created group to the groupsTab
            groupsTab.add(new Group(emailsTab.subList(indexDébut, indexFin)));
         }
      }

      return groupsTab;
   }

   /**
    * Displays the Organizer information.
    *
    */
   public void display() {
      System.out.println("ORGANIZER");

      System.out.println("MESSAGES");
      for (int i = 0; i < messagesTab.size(); ++i) {
         System.out.println(messagesTab.elementAt(i));
      }

      System.out.println("GROUPES");
      for (int i = 0; i < groupsTab.size(); ++i) {
         System.out.println(groupsTab.elementAt(i));
      }

   }

   /**
    * Returns messagesTab.
    *
    */
   public Vector<Message> getMessagesTab() {
      return messagesTab;
   }

   /**
    * Returns groupsTab.
    *
    */
   public Vector<Group> getGroupsTab() {
      return groupsTab;
   }

   /**
    * Returns isValid. Checks if the groups could successfully be created
    *
    */
   public boolean isValid() {
      return isValid;
   }

   private Vector<Message> messagesTab = new Vector<>(0);
   private Vector<Person> emailsTab = new Vector<>(0);
   private Vector<Group> groupsTab = new Vector<>(0);
   private boolean isValid = false;
}
