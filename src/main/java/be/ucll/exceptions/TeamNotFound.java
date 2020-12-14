package be.ucll.exceptions;

public class TeamNotFound extends Exception{

    public TeamNotFound(String teamName) {
        super("This team: " + teamName + " has not been found!");
    }

    public TeamNotFound() {
        super("Please specify a valid id!");
    }
}
