package be.ucll.exceptions;


public class OrganisationNotFound extends Exception{
    public OrganisationNotFound(String organisation) {
        super("This organisation: " + organisation + " has not been found!");
    }
}