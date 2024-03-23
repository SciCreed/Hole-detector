# Hole Detector

Hole-detector is an Android application that uses the device's location and shake detection to mark points on a map and alert the user when they are near a marked point. The application is built using Java and uses OpenStreetMap for the map view. 

## Project Structure

The main code for the application is located in the `app/src/main/java/com/example/holedetector` directory.

[MenuActivity.java](app/src/main/java/com/example/holedetector/MenuActivity.java) is the entry point of the application. From this activity, the user can navigate to other activities.

[MapActivity.java](app/src/main/java/com/example/holedetector/MapActivity.java) is the activity that displays the map view and allows the user to track their location and mark points on the map.

[SettingsActivity.java](app/src/main/java/com/example/holedetector/SettingsActivity.java) is the activity that allows the user to configure the sensitivity of the shake detection.