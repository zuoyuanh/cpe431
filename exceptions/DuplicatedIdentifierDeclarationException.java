package exceptions;

import java.io.*;

public class DuplicatedIdentifierDeclarationException extends Exception
{
   private String idName;
   private String idType;

   public DuplicatedIdentifierDeclarationException(String idName, String idType)
   {
      this.idName = idName;
      this.idType = idType;
   }

   public String getErrorMessage()
   {
      return "duplicated declaration for " + idType + " " + idName;
   }
}