package ch.heigvd.res.mailrobot.model.mail;

/**
   * Represents a person. Characterized by an email address
   * 
   * @author Romain, Dardan 
   */
public class Person {
   
   /**
   * Constructor with an email address.
   * 
   */
   public Person(String email){
      this.email = email;
   }
   
   /**
   * Returns a printable version of the class
   * 
   */
   public String toString(){
      return this.email;
   }
   
   /**
   * Returns the email
   * 
   */
   public String getEmail(){
      return this.email;
   }
   
   private String email;
}
