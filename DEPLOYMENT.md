Deploying Verification Emailer API
==================================

This is just a guide for deploy deploying Verification Emailer API (vea) using
Java, Maven, and Systemd.

Building and Running
--------------------

To build the JAR file:

```
mvn package spring-boot:repackage
```

and to build the JAR file without running the tests:

```
mvn package spring-boot:repackage -DskipTests
```

To run the jar file use this command, substituting the actual version:

```
java -jar path/to/email-<VERSION>.jar --spring.profiles.active=prod,api
```

For example, if the version is 1.5.0:

```
java -jar path/to/email-1.5.0.jar --spring.profiles.active=prod,api
```

To learn more about the active profiles used, see the
[active profiles section](./README.md#active-profiles) in the README.md file.

Use of Systemd
--------------

Systemd can be used to have a more robust deployment method.
With systemd, this application can be made into a service that will
automatically start up when the underlying machine starts up.

### Creating a Script to Run the Application

First, we will need a Bash script for systemd to run that will start up the
application. The file path can be `/usr/sbin/vea`.

```bash
#!/bin/bash

echo "Starting VEA..."

version="1.5.0"

cd "/path/to/"

java -jar email-$version.jar --spring.profiles.active=prod

echo "VEA stopped"
```

### Creating a Service File

Next, we need to create a service file for systemd that will call the above
Bash script. The file path can be `/etc/systemd/system/vea.service`.

```
[Unit]
Description=Verification Emailer API
After=network.target

[Service]
Type=simple
WorkingDirectory=/path/to
ExecStart=/usr/sbin/vea
Restart=always

[Install]
WantedBy=multi-user.target
```

### Interacting with Systemd

Once this is all set up, we can use commands to interact with the service.
The commands below assume the service is named "vea" (stands for Verification
Emailer API).

| Command                        | Explanation                                                       |
|--------------------------------|-------------------------------------------------------------------|
| `sudo systemctl start vea`     | Starts the service vea.                                           |
| `sudo systemctl stop vea`      | Stops the service vea.                                            |
| `sudo systemctl restart vea`   | Restarts the service vea.                                         |
| `sudo systemctl status vea`    | Checks the status of the service vea.                             |
| `sudo systemctl enable vea`    | Makes vea start up whenever the underlying machine starts up.     |
| `sudo systemctl disable vea`   | Makes vea not start up whenever the underlying machine starts up. |
| `sudo systemctl daemon-reload` | Restarts systemd.                                                 |
| `journalctl -n 10`             | View the last 10 systemd logs (can be any number).                |

Unzipping Old Log Files
-----------------------

Log files from the previous day will get zipped as a `gz` file.
To unzip them, use:

```
gzip -dk filename.gz
```
