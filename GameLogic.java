import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.io.*;

public class GameLogic{
      
   //Name of file containing all the possible "secret words"
   private static final String SECRET_WORDS_FNAME = "secret_words.txt";   
   
   //Name of the dictinoary file (containing all valid guesses)
   private static final String VALID_GUESSES_FNAME = "dictionary.txt";   
   
   
   //Dimensions of the game grid in the game window
   public static final int MAX_ROWS = 5;
   public static final int MAX_COLS = 6;
   
   //Character codes for the enter and backspace key press
   public static final char KEY_ENTER = KeyEvent.VK_ENTER; //(numeric value is: 10)
   public static final char KEY_BACKSPACE = KeyEvent.VK_BACK_SPACE; //(numeric value is: 8)
   
   //The null character value (used to represent an "empty" value for a spot on the game board)
   public static final char NULL_CHAR = 0;
   
   //Various Color Values
   private static final Color CLR_GREEN = new Color(53, 209, 42); //(Green... right letter right place)
   private static final Color CLR_YELLOW = new Color(235, 216, 52); //(Yellow... right letter wrong place)
   private static final Color CLR_DGRAY = Color.DARK_GRAY; //(Dark Gray)
   private static final Color CLR_BLACK = Color.BLACK; //(Black) default cell color
   
   //ints representing different potential arrow directions (drawn at the end of a row on the game board)
   public static final int ARROW_LEFT = -1;
   public static final int ARROW_RIGHT = 1;
   public static final int ARROW_BLANK = 0;
   
   //A preset, hard-coded secret word to be use when the resepective debug is enabled
   private static final char[] HARDCODED_SECRET = {'R', 'A', 'I', 'D', 'E', 'R'};      
   
   //Array storing all valid guesses read out of the respective file
   private static String[] valids;
   
   //The active row/col where the user left off typing
   private static int userRow, userCol;
      
   //The indices out of the dictionary where the current "leftmost" and 
   //"rightmost" (ie alphabetically earliest and latest, respetively) occur
   private static int leftWordIdx, rightWordIdx;
   
   //*******************************************************************
   
   //
   //If either of the valid guess or secret words files cannot be read, or are
   //missing the word count in the first line) this program terminates with System.exit(1)
   public static char[] initGame(){
      getValids();
      if (BetwordleLauncher.DEBUG_USE_HARDCODED_SECRET)
        return HARDCODED_SECRET;
      else 
        return getSecretWord();
   }
   
   //This function gets called once after the graphics window has been
   //initialized and init has been called.
   public static void warmup(){
 /*    GameGUI.setBoardChar(0, 0, 'C');
     GameGUI.setBoardChar(1, 3, 'O');
     GameGUI.setBoardChar(3, 5, 'S');
     GameGUI.setBoardChar(4, 5, 'C');

     GameGUI.setBoardColor(0, 0, CLR_YELLOW);
     GameGUI.setBoardColor(3, 5, CLR_GREEN);

     GameGUI.setKBColor('C', CLR_GREEN);
     GameGUI.setKBColor('U', CLR_DGRAY);

     GameGUI.setArrow(1, ARROW_LEFT);
     GameGUI.setArrow(2, ARROW_RIGHT);  */
     //All of your warmup code will go in here except for the
     //"wiggle" task (3.1.1 step 3)... where will that go?
   }   
   
   
   //This function gets called everytime the user types a valid key on the
   //keyboard (alphabetic character, enter, or backspace) or clicks one of the
   //keys on the graphical keyboard interface.
   //
   //The key pressed is passed in as a char value.
   public static void reactToKeyPress(char key){
     
      //placeholder debug print message
      //if (key == ('P'))
      //  GameGUI.wiggleRow(3);
      System.out.println("key pressed: '" + key + "' (int value: " + (int)key + ")");

      if (key == KEY_BACKSPACE){
        backspace();
      }
      else if (key != KEY_ENTER){
        letterTyped(key);
      }
      else if (key == KEY_ENTER){
        enterTyped();
      }
      return;
    }

     // The following deletes the current letter and goes back to the previous letter and checks edgecase
    public static void backspace(){
      if (userCol > 0){
        userCol--;
        GameGUI.setBoardChar(userRow, userCol, NULL_CHAR);
        return;
      }
      else {
        return;
      }
    }

    // The following accounts for edgecase and puts key typed onto game board.
    public static void letterTyped(char key){
      if (userCol == 6){
        return;
      }
      else {
        GameGUI.setBoardChar(userRow, userCol, key);
        userCol++;
        return;
      }
    }

    // The following wiggles row if not enough letters and enter is pressed, and checks the input when word is complete
    public static void enterTyped(){
      if (userCol != 6){
        GameGUI.wiggleRow(userRow);
      }
      String typedWord = typedToString();
      if (beforeOrAfter()){
        return;
      }
      else if (!checkReal(typedWord)){
        GameGUI.wiggleRow(userRow);
        return;
      }
      else {
        checkInput(typedWord);
      }
      return;
    }
    public static String typedToString(){
      String typedAsString = "";
      for (int ch = 0; ch < 6; ch++){
        typedAsString += GameGUI.getBoardChar(userRow, ch);
      }
      return typedAsString;

    }

    // The following checks the word for correctness
    public static void checkInput(String typedWord){
      userCol = 0;
      char[] secret_word = GameGUI.getSecretAsArr();
      char[] copy_secret_word = Arrays.copyOf(secret_word, 6);
      copy_secret_word = turnGreen(copy_secret_word);
      turnYellow(copy_secret_word);
      turnGray(secret_word);
      arrowDisplay(secret_word);
      userRow++;
      if (typedWord.equals(GameGUI.getSecretAsStr())){
        GameGUI.triggerGameOver(true);
      }
      else if (userRow == 5){
        GameGUI.triggerGameOver(false);
      }
    }



    // The following cycles through compares all the letters of both words to check for any overlap, if none is found, they are turned gray on gameboard and keyboard
    public static void turnGray(char[] secret_word_array){
      for (int ch = 0; ch != 6; ch++){
        for (int i = 0; i != 6; i++){
          if (GameGUI.getBoardChar(userRow, ch) != (secret_word_array[i])){
            if ((GameGUI.getBoardColor(userRow, ch) != CLR_GREEN) && (GameGUI.getBoardColor(userRow, ch) != CLR_YELLOW))
              GameGUI.setBoardColor(userRow, ch, CLR_DGRAY);
            if ((GameGUI.getKBColor(GameGUI.getBoardChar(userRow, ch)) != CLR_GREEN) && (GameGUI.getKBColor(GameGUI.getBoardChar(userRow, ch)) != CLR_YELLOW)){
              GameGUI.setKBColor(GameGUI.getBoardChar(userRow, ch), CLR_DGRAY);
            }
          }
        }
      }
    }
    // The following cycles through compares all the letters of both words to check for any overlap, if any is found, they are turned yellow on gameboard and keyboard
    public static void turnYellow(char[] secret_word){
      for (int ch = 0; ch != 6; ch++){
        for (int i = 0; i != 6; i++){
          if (GameGUI.getBoardChar(userRow, ch) == (secret_word[i]) && GameGUI.getBoardColor(userRow, ch) != CLR_GREEN){
            secret_word[i] = ' ';
            GameGUI.setBoardColor(userRow, ch, CLR_YELLOW);
            if ((GameGUI.getKBColor(GameGUI.getBoardChar(userRow, ch)) != CLR_GREEN)){
              GameGUI.setKBColor(GameGUI.getBoardChar(userRow, ch), CLR_YELLOW);
            }
            break;
          }
        }
      }
    }

      // The following cycles through compares the letters of both words to check if they match perfectly, if they do, the letters that match are turned green on gameboard and keyboard,
      // The total correct number of characters are tracked to end game if the word is guessed before 5 guesses.
    public static char[] turnGreen(char[] secret_word){
      char currentChar;
      for (int ch = 0; ch != 6; ch++){
        currentChar = GameGUI.getBoardChar(userRow, ch);
        if (currentChar == (secret_word[ch])){
          secret_word[ch] = ' ';
          GameGUI.setBoardColor(userRow, ch, CLR_GREEN);
          GameGUI.setKBColor(currentChar, CLR_GREEN);
        }
      }
      return secret_word;
    }
    // The following checks if the first character is greater than or less than the second and displays a corresponding arrow, if they are equal, it continues cycling through each letter.
    public static void arrowDisplay(char[] secret_word){
      char currentChar;
      for (int ch = 0; ch != 6; ch++){
        currentChar = GameGUI.getBoardChar(userRow, ch);
        if (currentChar > (secret_word[ch])){
          GameGUI.setArrow(userRow, ARROW_LEFT);
          return;
        }
        else if (currentChar < (secret_word[ch])){
          GameGUI.setArrow(userRow, ARROW_RIGHT);
          return;
        }
      }
    }

    public static boolean beforeOrAfter(){
      if (userRow == 0){
          return false;
      }
      char currentChar;
      char previousChar;
      int currentArrow;
      for (int ch = 0; ch < 6; ch++){
        currentChar = GameGUI.getBoardChar(userRow, ch);
        previousChar = GameGUI.getBoardChar(userRow - 1, ch);
        currentArrow = GameGUI.getArrowDirection(userRow - 1);
        if (currentChar == previousChar){
          continue;
        }
        else if (currentArrow == ARROW_RIGHT && currentChar > previousChar){
          return false;
        }
        else if (currentArrow == ARROW_LEFT && currentChar < previousChar){
          return false;
        }
        else {
          GameGUI.wiggleRow(userRow);
          return true;
        }
      }
      return false;
    }

    public static void getValids(){
      valids = new String[15056];
      File dictFile = new File("dictionary.txt");
      try {
        Scanner dict = new Scanner(dictFile);
        int i = 0;
        while (dict.hasNextLine()) {
            String word = dict.nextLine();
            valids[i] = word;
            i++;
            }
        dict.close();
      }
      catch (FileNotFoundException e) {
        System.out.println(e.toString());
        System.exit(1);
      }

    }

    public static boolean checkReal(String typedWord){
      File dictFile = new File("dictionary.txt");
      for (String word : valids){
        if (word.equals(typedWord.toLowerCase())){
          return true;
        }
      }
      return false;
    }

    public static char[] getSecretWord(){
      int randomNum = getRandomNum();
      File secretFile = new File("secret_words.txt");
      String secretWord;
      try {
        Scanner secret = new Scanner(secretFile);
        int i = 2;
        while (secret.hasNextLine()) {
            secretWord = secret.nextLine();
            i++;
            if (i == randomNum){
              secret.close();
              return secretWordAsChar(secretWord.toUpperCase());
            }
        }
        secret.close();
      }
      catch (FileNotFoundException e) {
        System.out.println(e.toString());
        System.exit(1);
      }
      return null;
    }

    public static char[] secretWordAsChar(String secretWord){
      char[] secretWordAsChar = new char[6];
      for (int i = 0; i < secretWord.length(); i++){
        secretWordAsChar[i] = secretWord.charAt(i);
      }
      return secretWordAsChar;
    }

    public static int getRandomNum(){
      Random random = new Random();
      int randomNum = random.nextInt(845) + 2;
      return randomNum;
    }
}
