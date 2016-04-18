package ch.heigvd.res.mailrobot.smtp;

import ch.heigvd.res.mailrobot.model.mail.Group;
import ch.heigvd.res.mailrobot.model.mail.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * SMTP Client used to exchange messages with the MockMock server.
 *
 * @author Romain, Dardan
 */
public class SmtpClient {

   // Final variables
   private static final String SERVER_OK = "250 ";
   private static final String DATA_READY = "354 ";
   private static final String QUIT_OK = "221 ";

   Socket clientSocket;
   BufferedReader in;
   PrintWriter out;
   boolean connected = false;
   String serverResponse = "";

   /**
    *
    * Connects to the given server on the given port. Returns the success of the
    * connection
    */
   public boolean connect(String serverAddress, int serverPort) {
      System.out.println("Connection to " + serverAddress + ":" + serverPort + "\n");

      try {
         clientSocket = new Socket(serverAddress, serverPort);
         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         out = new PrintWriter(clientSocket.getOutputStream());

         // Resets the serverResponse
         serverResponse = "";

         while ((serverResponse = in.readLine()) != null) {
            System.out.println("[S] " + serverResponse);

            // Sends the EHLO message
            out.println("EHLO " + serverAddress);
            System.out.println("[C] EHLO " + serverAddress);
            out.flush();

            // Waits for the server response. Consists of several messages beginning with "250"
            while ((serverResponse = in.readLine()) != null && serverResponse.substring(0, 3).equals("250")) {

               System.out.println("[S] " + serverResponse);

               // Final message should be "250 Ok"
               if (serverResponse.substring(0, 4).equals(SERVER_OK.substring(0, 4))) {
                  System.out.println("\nConnected !\n");
                  return true;
               }
            }
         }
         return false;

      } catch (IOException e) {
         //cleanup();
         return false;
      }

   }

   /**
    *
    * Sends a randomly selected message to a group. The protocol must follow this
    * pattern : MAIL FROM: XXX RCPT TO: XXX DATA From: XXX To: XXX Subject: XXX
    *
    * XXX .
    */
   public void send(Vector<Message> messagesTab, Group group) throws IOException {

      // Selects a random message from the messagesTab
      int rdm = ThreadLocalRandom.current().nextInt(0, messagesTab.size());
      Message m = messagesTab.elementAt(rdm);

      // Gets useful information
      String mailFrom = group.getSender();
      String rcptTo = group.displayRecipients();
      String subject = m.getSubject();
      String mess = m.getMessage();

      // Resets the serverResponse
      serverResponse = "";

      // Resets the stream
      out.flush();

      // Sends MAIL FROM: XXX
      out.println("MAIL FROM: " + mailFrom);
      out.flush();

      // Debug info
      System.out.println("[C] MAIL FROM: " + mailFrom);

      // Waits for the OK
      while ((serverResponse = in.readLine()).substring(0, 4) != SERVER_OK) {
         
         // Debug info
         System.out.println("[S] "+ serverResponse);
         
         // Adds the 'RCPT TO' command for all the recipients
         for (int i = 0; i < group.getRecipients().size(); ++i) {
            out.flush();
            out.println("RCPT TO: " + group.getRecipients().elementAt(i));
            out.flush();

            // Debug info
            System.out.println("[C] RCPT TO:" + group.getRecipients().elementAt(i));
            serverResponse = in.readLine();
            System.out.println("[S] " + serverResponse);
            
            // Checks the server response
            if (!serverResponse.substring(0, 4).equals(SERVER_OK)) {
               System.out.println("ERROR. QUITTING");
               return;
            }  
         }

         // Waits for the OK
         while ((serverResponse.substring(0, 4)) != SERVER_OK) {

            // Sends DATA
            out.println("DATA");
            out.flush();

            // Debug info
            System.out.println("[C] DATA");

            // Waits for the OK
            while ((serverResponse = in.readLine()).substring(0, 4) != DATA_READY) {

               // Sends the DATA information
               out.println("From: " + mailFrom);
               out.println("To: " + rcptTo);
               out.println("Subject: " + subject);
               out.println("");
               out.println(mess);
               out.println(".");
               out.flush();

               // Debug info
               System.out.println("[S] "+ serverResponse);
               System.out.println("[C] Subject: " + subject);
               System.out.println("[C] From: " + mailFrom);
               System.out.println("[C] To: " + rcptTo);
               System.out.println("[C]" + mess);
               System.out.println("[C] .");

               // Waits for the final response of the server
               while ((serverResponse = in.readLine()).substring(0, 4) != SERVER_OK) {
                  System.out.println("[S] " + serverResponse);
                  // Email sent successfully

                  // Debug info
                  System.out.println("[" + subject + "] sent successfully ! \n");

                  return;
               }
            }
         }
      }
      return;
   }

   /**
    *
    * Quits the client/server exchange. Closes the socket and the buffers
    */
   public void disconnect() throws IOException {
      out.flush();
      out.println("QUIT");
      out.flush();

      // Debug info
      System.out.println("[C] QUIT");

      if ((serverResponse = in.readLine()).equals(QUIT_OK)) {
         // Debug info
         System.out.println("[S] " + serverResponse + "\n");

         
         in.close();
         out.close();
         clientSocket.close();
         System.out.println("Socket closed successfully");
      }

   }
}
