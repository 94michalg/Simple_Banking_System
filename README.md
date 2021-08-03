# Simple_Banking_System
Project has beed made in 10/2020, during my Jetbrains Academy course. It simulates simple banking system, where all accounts have auto-generated card number (using luhn algorithm) and pin code. Accounts are stored in SQL database.
## Technologies
* Gradle 7.1.1
* SQLite 3.8.11.2
## Setup
Database filename must be provided as a command line argument e.g. 
```
java Main -fileName card.s3db
```
## Features
When the user is not logged in he can create new account or log in to existing one. After logging in he can check the balance, add some income, make a transfer, close the account or log out.
