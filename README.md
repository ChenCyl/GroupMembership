# Distributed Group Membership
## Package Dependencies

- Java 1.8.0
- Maven 3.6.0 (just used in the second way mentioned below)

## Instructions

> Choose only one way between the first way and the second way.

### The First Way - download directly

#### Step 1: download the .jar package

[Click here to download directly](https://github.com/ChenCyl/GroupMembership/releases/download/v1.0.1/GroupMembership-1.0-SNAPSHOT.jar)

#### Step 2: run

`cd` into the directory of .jar

- If you are the introducer
  - `java -jar GroupMembership-1.0.2.jar -p <port> `
  - eg: `java -jar GroupMembership-1.0.2.jar -p 9001`

- If you are a normal node
  - `java -jar GroupMembership-1.0.2.jar -p <port> -i <introducer_ip_address> <introducer_port> `
  - eg: `java -jar GroupMembership-1.0.2.jar -p 9002 -i 192.168.1.114 9001`

### The Second Way - compile the code

#### Step 1: compile the code

1. `cd` into the root directory of project
2. `mvn package`

#### Step 2: run

`cd target`

- If you are the introducer
  - `java -jar GroupMembership-1.0.2.jar -p <port> `
  - eg: `java -jar GroupMembership-1.0.2.jar -p 9001`

- If you are a normal node
  - `java -jar GroupMembership-1.0.2.jar -p <port> -i <introducer_ip_address> <introducer_port> `
  - eg: `java -jar GroupMembership-1.0.2.jar -p 9002 -i 192.168.1.114 9001`

## Tips!

- If you are a Linux user, please `vim /etc/hosts` and comment out the configuration about localhost.
  - eg: `#127.0.0.1	localhost`
- If you are a windows user, please remember to disable virtual network interface cards.

If you hava any questions about running the project among different operating systems, please email me @hitoka@163.com

