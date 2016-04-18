package ch.heigvd.res.mailrobot.model.prank;

import ch.heigvd.res.mailrobot.model.mail.Group;
import ch.heigvd.res.mailrobot.model.mail.Message;
import ch.heigvd.res.mailrobot.smtp.SmtpClient;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * This class represents a Prank. 
 * It starts a smtp connection, and sends the prank.
 *
 * @author Romain, Dardan
 */
public class Prank {

   /**
    *
    * Constructor with the required parameters
    */
   public Prank(String smtpServerAddress, int smtpServerPort, Vector<Group> groupsTab, Vector<Message> messagesTab) throws IOException {

      // Creates a new SmtpClient, tries to connect to it, and if successful, sends the prank
      SmtpClient sc = new SmtpClient();
      
      if (sc.connect(smtpServerAddress, smtpServerPort)) {
         // Browses the groups and sends the prank
         for (Group g : groupsTab) {
            sc.send(messagesTab, g);
         }
         
         // Disconnects after sending the emails
         sc.disconnect();
      }

   }
}
