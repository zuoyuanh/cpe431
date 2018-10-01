package exceptions;

import java.io.*;

public class IdentifierNotFoundException extends Exception
{
   private String idName;
   private String idType;

   public IdentifierNotFoundException(String idName, String idType)
   {
      this.idName = idName;
      this.idType = idType;
   }

   public String getErrorMessage()
   {
      return idType + " " + idName + " is not declared";
   }
}