public class Main {

    public static void main(String[] args) {
        FileLoader fileLoader = new FileLoader();

        boolean result = fileLoader.loadIDsFromFile("TV_Show_Series.csv");

        if (result) {
            System.out.println("Successfully loaded ids from file!");
        } else {
            System.out.println("Failed to load ids from file!");
        }
    }
}
