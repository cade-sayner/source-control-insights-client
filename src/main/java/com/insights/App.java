package com.insights;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class App 
{
    public static Properties applicationProperties;
    private static AuthenticatedApiClient client;
    private static String jwt;
    public static void main( String[] args ) throws Exception
    {
        try{
            applicationProperties = loadProperties();
        }
        catch(Exception e){
            System.out.println("Something went wrong trying to load the application properties");
            System.out.println(e.toString());
            return;
        }

        Scanner s = new Scanner(System.in);
        String line = s.nextLine();
        if(line.equals("login")){
            jwt = LoginService.login();
            System.out.println("The jwt:" + jwt);
        }
        client = new AuthenticatedApiClient(jwt);
    }

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return null;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }
}
