//Launches the Betwordle game
public class BetwordleLauncher{
   
   
   //Controls if the secret word is displayed in the game window (true) or not (false)
   public static final boolean DEBUG_DISPLAY_SECRET = false;
   
   //Controls if the hard coded word is used as the secret word (true) or a random
   //word read from the dictionary file (false)
   public static final boolean DEBUG_USE_HARDCODED_SECRET = false;
   
   
   
   public static void main(String[] args){
      
      GameGUI.launchGame();
      
   }
   
   
}
