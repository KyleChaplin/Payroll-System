# Payroll-System

## Setting up database
Download Database
This payroll system uses Oracle Database 21c running on localhost.
A remote connection is also an option.

Download - https://www.oracle.com/database/technologies/oracle-database-software-downloads.html

Download JDBC
I'm using Intellij as my IDE and used their built-in database connection and chose the option to download missing drivers.
It should be possible to download and use these any way you want.

Change the database information

How to view database
I'm using Oracle's SQL Developer app to view my database, you can also use SQL Plus, which comes as part of the database.

Download - https://www.oracle.com/database/sqldeveloper/

Database information has been moved to a .env file. This file is created if it does not exist already. The format is as follows -

DB_URL=your_db_url
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

## Setting up email server
I am using the Google SMPT server. The following is required to send emails.

These will need to be put in the database before any emails can be sent.

Google email
Google app password

https://support.google.com/accounts/answer/185833?hl=en