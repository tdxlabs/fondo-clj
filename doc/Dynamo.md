# Setting up DynamoDB Local for Fondo

```bash
mkdir dynamodb
cd dynamodb
wget http://dynamodb-local.s3-website-us-west-2.amazonaws.com/dynamodb_local_latest.tar.gz
tar xvf dynamodb_local_latest.tar.gz
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar
```

# Setting up DynamoDB on AWS

Fondo will use `clj-aws-auth` to connect to your AWS Dynamo instance, which checks the following __sources__ in order:

* `:environment`       - `AWS_ACCESS_KEY_ID` and `AWS_SECRET_KEY`
* `:system-properties` - `aws.accessKeyId` and `aws.secretKey`
* `:instance-profile`  - via the Amazon EC2 metadata service
