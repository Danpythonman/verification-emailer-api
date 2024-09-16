Verification Emailer API
========================

Verification Emailer API is a small webserver that sends verification codes as
email.

I made this project because I had trouble finding a service that simply sent
transactional email.
Most email services are tailored to marketing and business operations, but all
I needed was a simple way to send verification codes to users that are making
an account with a web service.
This application is targeted to developers who need a quick, easy, automated
way to verify a user's email address for their projects.

Verification Emailer API is made with Java and Spring Boot. It handles basic
web app functionality, like authentication, JWT authorization, and database
connectivity.
Additionally, it has endpoints to send an email, and verify the code from an
email.

Requirements
------------

To run this application, Java version 17 is needed, along with Maven for
dependency management.

Before running, secret values need to be provided.
There are two files in the directory `src/main/resources/`
that should hold these values (and are ignored by Git for security):

- `secrets.properties` - Secret keys and account information (for example,
  JWT secret key, Mailtrap account credentials).
- `secrets-prod.properties` - Connection information for the production
 database.

Both of these files have example files in the same directory to show their
formats.

Running Locally
---------------

To run the project locally:

```
mvn spring-boot:run "-Dspring-boot.run.profiles=dev,no-op"
```

Or run with your IDE (just make sure to set the active profiles).

The active profiles can be changed depending on dev/prod environment and
emailer type. See the [Active Profiles section](#active-profiles) for more information.

Active Profiles
---------------

This application uses Spring active profiles for configuring the dev/prod
environment and the emailer type.

For the dev/prod environment, the two options are:

- `dev` - An in-memory H2 database will be used, as per the definition in
  `src/main/resources/application-dev.properties`, and logs will be sent only
  to the console.
- `prod` - The database connection will be retrieved from a secrets file
  `src/main/resources/secrets-prod.properties`, and logs will be sent to a file
  called `spring.log`, located where the application is run from.

For the emailer type, the three options are:

- `api` - Actual emails will be sent.
- `mailtrap` - Emails will be sent to Mailtrap for inspection and testing.
- `no-op` - No emails will be sent. Instead, the email contents will be logged
  to for easy testing.

Contact
-------

Daniel Di Giovanni - <dannyjdigio@gmail.com>
