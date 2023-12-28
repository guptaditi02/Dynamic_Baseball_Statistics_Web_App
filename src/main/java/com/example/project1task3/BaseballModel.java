package com.example.project1task3;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/*
Explanation:
* Author: Aditi Gupta - argupta
* Last Modified: September 22, 2023
BaseballModel is a class that encapsulates the business logic for the web application.
It provides methods to search for baseball player image URLs, scrape player data, and search for baseball team flag image URLs.
The doSearch method searches for a baseball player's image URL by screen scraping a website.
The scrapePlayerData method scrapes additional player data using the player's first and last name.
The doFlagSearch method searches for a baseball team's flag image URL by screen scraping another website.
The fetch method is a private utility method used to make HTTP GET requests and retrieve the response as a string.
The class uses external libraries such as Jsoup for HTML parsing and Gson for JSON parsing.
Data is extracted from web pages using string manipulation and HTML structure analysis.
The results are stored in a Map called playerDataMap, which can be accessed by the servlet for further processing.
 */

public class BaseballModel {

    Map<String, String> playerDataMap = new HashMap<>();

    // This method performs a search for a baseball player's image URL
    public String doSearch(String firstName, String lastName) throws UnsupportedEncodingException  {
        // URL encode the searchTag, e.g., to encode spaces as %20
        firstName = URLEncoder.encode(firstName, "UTF-8");
        lastName = URLEncoder.encode(lastName, "UTF-8");

        String response = "";
        String baseballReferenceURL = "https://www.baseball-reference.com/players/";

        // Create a URL for the page to be screen scraped
        if(firstName != null && lastName != null) {
            if(lastName.length() >= 5) {
                baseballReferenceURL = baseballReferenceURL + lastName.substring(0, 1).toLowerCase() + "/" + lastName.substring(0, 5).toLowerCase() + firstName.substring(0, 2).toLowerCase() + "01.shtml";
            } else {
                baseballReferenceURL = baseballReferenceURL + lastName.substring(0, 1).toLowerCase() + "/" + lastName.toLowerCase() + firstName.substring(0, 2).toLowerCase() + "01.shtml";
            }
        } else {
            baseballReferenceURL = null;
        }

        // Fetch the page
        response = fetch(baseballReferenceURL);

        // Search the page to find the picture URL
        int cutLeft = response.indexOf("players open");
        cutLeft = response.indexOf("media-item multiple", cutLeft);
        String s = " src=";
        cutLeft = response.indexOf(s, cutLeft) + s.length() + 1;

        // If not found, then no such photo is available.
        if (cutLeft == -1) {
            System.out.println("pictureURL= null");
            return null;
        }

        // Look for the jpg extension
        int cutRight = response.indexOf("jpg", cutLeft) + 3;

        // Now snip out the part from positions cutLeft to cutRight
        // and prepend the protocol (i.e., https).
        String pictureURL;
        if (cutRight > cutLeft) {
            pictureURL = response.substring(cutLeft, cutRight);
        } else {
            pictureURL = null;
        }
        return pictureURL;
    }

    // This method scrapes player data using the first and last name
    public Map<String, String> scrapePlayerData(String firstName, String lastName) throws UnsupportedEncodingException {
        firstName = URLEncoder.encode(firstName, "UTF-8");
        lastName = URLEncoder.encode(lastName, "UTF-8");
        String playerName = firstName + " " + lastName;
        String response = "";

        // Send an HTTP GET request to the website
        String html = fetch("https://razzball.com/mlbamids/");
        // Fetch the page

        // Search the page to find the picture URL
        int cutLeft = html.indexOf(firstName+" "+lastName);
        cutLeft = html.indexOf("</a>", cutLeft);
        cutLeft = html.indexOf("</td>", cutLeft);
        String s = "<td>";
        cutLeft = html.indexOf(s, cutLeft) + s.length();

        // If not found, then no such photo is available.
        String result;
        if (cutLeft == -1) {
            System.out.println("Element not found");
            return null;}

        // Look for the </td>
        int cutRight = html.indexOf("</td>", cutLeft);


        // Now snip out the part from positions cutLeft to cutRight
        if (cutRight > cutLeft) {
            result = html.substring(cutLeft, cutRight);
        } else {
            result = null;
        }

        if (result != null) {
            // Extract player data using an API
            try {
                // Define the URL of the API
                String apiUrl = "https://lookup-service-prod.mlb.com/json/named.player_info.bam?sport_code=%27mlb%27&player_id=%27" + result + "%27";
                System.out.println(apiUrl);
                String api = "";
                api = fetch(apiUrl);

                // Parse the JSON response using Gson
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonData = jsonParser.parse(api.toString()).getAsJsonObject();
                JsonObject playerInfo = jsonData.getAsJsonObject("player_info")
                        .getAsJsonObject("queryResults")
                        .getAsJsonObject("row");

                // Extract specific fields
                String fullName = playerInfo.get("name_display_first_last").getAsString();
                String teamName = playerInfo.get("team_name").getAsString();
                String primaryPosition = playerInfo.get("primary_position_txt").getAsString();
                String jerseyNumber = playerInfo.get("jersey_number").getAsString();
                String noOfThrows = playerInfo.get("throws").getAsString();
                String playerId = playerInfo.get("player_id").getAsString();
                String activeStatus = playerInfo.get("active_sw").getAsString();
                String birthCountry = playerInfo.get("birth_country").getAsString();

                playerDataMap.put("Full Name", fullName);
                playerDataMap.put("Team Name", teamName);
                playerDataMap.put("Primary Position", primaryPosition);
                playerDataMap.put("Jersey Number", jerseyNumber);
                playerDataMap.put("Throws", noOfThrows);
                playerDataMap.put("Player Id", playerId);
                playerDataMap.put("Active Status", activeStatus);
                playerDataMap.put("Birth Country", birthCountry);

                return playerDataMap;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Player not found or element not found");
        }
        return playerDataMap;
    }

    // This method performs a search for a baseball team's flag image URL
    public String doFlagSearch(String firstName, String lastName) throws UnsupportedEncodingException  {
        firstName = URLEncoder.encode(firstName, "UTF-8");
        lastName = URLEncoder.encode(lastName, "UTF-8");
        Map<String, String> map1 = scrapePlayerData(firstName, lastName);
        String teamName = map1.get("Team Name");
        String response = "";
        String baseballFlagURL = "https://www.flags.com/";

        // Create a URL for the page to be screen scraped
        if(teamName != null){
            baseballFlagURL = baseballFlagURL + teamName.toLowerCase().replace(" ", "-") + "-flag/";
        } else {
            baseballFlagURL = null;
        }

        // Fetch the page
        response = fetch(baseballFlagURL);

        // Search the page to find the picture URL
        int cutLeft = response.indexOf("productView-imageCarousel-main-item slick-current");
        String s = " href=";
        cutLeft = response.indexOf(s, cutLeft) + s.length() + 1;

        // If not found, then no such photo is available.
        if (cutLeft == -1) {
            System.out.println("pictureURL= null");
            return null;
        }

        // Look for the jpg extension
        int cutRight = response.indexOf("jpg", cutLeft) + 3;

        // Now snip out the part from positions cutLeft to cutRight
        // and prepend the protocol (i.e., https).
        String imageURL;
        if (cutRight > cutLeft) {
            imageURL = response.substring(cutLeft, cutRight);
        } else {
            imageURL = null;
        }
        return imageURL;
    }

    // Make an HTTP request to a given URL
    private String fetch(String urlString) {
        String response = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;

            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text; readLine() strips newline characters
                response += str;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
            // Handle the exception here
        }
        return response;
    }
}
