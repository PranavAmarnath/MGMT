# MGMT

A Java Swing application for logging into and out of a system to track hours spent by users.

## Download
<a href="https://github.com/PranavAmarnath/MGMT/releases/download/v1.0/MGMT-1.0-SNAPSHOT.jar" onclick="window.open('https://github.com/PranavAmarnath/MGMT/releases/download/v1.0/data.csv'); return true;">
    <img src="https://img.shields.io/badge/MGMT-1.0-blue" alt="Download MGMT 1.0" />
</a>
<p>
Run with java -jar MGMT-1.0-SNAPSHOT.jar (or double-click it). Requires Java 17 or newer.
Make sure to have the `data.csv` file in the same folder as the JAR file.

## Insight
The purpose of this application is to manage the hours of people who log in and log out of the system (the users) and use this statistic to measure commitment.
It saves the sign-in timestamp so that a user can still sign out to obtain their hours even if the app closes.<p>
  
The application utilizes worker threads to read and write CSV data to an external file. This system uses the same model architecture as [SecresCSV](https://github.com/PranavAmarnath/SecresCSV).

[MGMT](https://github.com/PranavAmarnath/MGMT) supports reading and writing to a CSV file to track hours of users. It also syncs to the system theme.

Libraries:
* OpenCSV - reading CSV data
* FlatLaf - modern look and feel for Swing components
* SwingX - `JXHyperlink`
* FlatLaf-SwingX - modern LAF for SwingX components
* jSystemThemeDetector - detect the current appearance of the OS

How the application looks:
<p align="left">
      <img src="https://github.com/PranavAmarnath/MGMT/assets/64337291/780ab8a0-268b-462e-81a7-83ccd44f6e89" width="370" />
      <img src="https://github.com/PranavAmarnath/MGMT/assets/64337291/71aa033d-cdea-44cd-8740-4385c3e03bf1" width="370" /> 
      <img src="https://github.com/PranavAmarnath/MGMT/assets/64337291/9a63ed23-0269-4ff2-a94a-ceaebcb92e90" width="370" /> 
</p>
