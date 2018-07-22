# transfer-service
Simple money transfers service

## build project
The project use Maven tool for build target application. 
Copy and paste the following lines for build project:

    mvn package    

and run integration and unit tests

    mvn verify

## run application
    mvn exec:java

default http properties values is:

      http {
        host = "localhost"
        port = 8080
      }

and can be changed in a application configuration file _application.conf_
      
## testing application REST server   
We can use command line util _**curl**_  for application testing.

Copy and paste the following lines for testing the application:

check first default account by account id=1 

    curl -H "Content-Type: application/json" -X GET  http://localhost:8080/account/1

The application server should respond with the following:

    {
      "balance": 10.7200,
      "description": "for testing only",
      "id": 1,
      "name": "main",
      "number": "N1"
    }
    
check second account default account by account id=2

    curl -H "Content-Type: application/json" -X GET  http://localhost:8080/account/2
    
respond:
    
    {
      "balance": 22.3300,
      "description": "",
      "id": 2,
      "name": "Mr. Foo",
      "number": "N2"
    }

Send POST request to server for move money( value=2.22) from account id=1 to account id=2

    curl -H "Content-type: application/json" -X POST -d '{"transactionId": "tx-2345", "fromAccountId": 1, "toAccountId":2,"value": 2.22, "description":"first test"}' http://localhost:8080/transfer 
respond:
    
    {
      "balance": 8.5000,
      "description": "Done and new balance=8.5000",
      "id": "d77238be-df80-4316-8b6a-340c08b8d8b7",
      "referenceId": 5,
      "request": {
        "description": "first test",
        "fromAccountId": 1,
        "toAccountId": 2,
        "transactionId": "tx-2345",
        "value": 2.22
      },
      "status": "COMPLETED"
    }


check new account balance values:
account id=1

    curl -H "Content-Type: application/json" -X GET  http://localhost:8080/account/1
respond:
    
    {
      "balance": 8.5000,
      "description": "for testing only",
      "id": 1,
      "name": "main",
      "number": "N1"
    }

account id=2

    curl -H "Content-Type: application/json" -X GET  http://localhost:8080/account/2
respond:

    {
      "balance": 24.5500,
      "description": "",
      "id": 2,
      "name": "Mr. Foo",
      "number": "N2"
    }
    

Send new POST request to server for move money( value=12.22) from account id=1 to account id=2

     curl -H "Content-type: application/json" -X POST -d '{"transactionId": "tx-2346", "fromAccountId": 1, "toAccountId":2,"value": 12.22, "description":"Second first test"}' http://localhost:8080/transfer
respond:

    {
      "balance": 8.5000,
      "description": "Don't have money on account for make operation. value=8.5000",
      "id": "6dd5d665-36b4-4115-b82c-86f859bed296",
      "referenceId": 6,
      "request": {
        "description": "Second first test",
        "fromAccountId": 1,
        "toAccountId": 2,
        "transactionId": "tx-2346",
        "value": 12.22
      },
      "status": "DECLINED"
    }


The lest  operation was declined for account id=1 and balance has value=8.5000

