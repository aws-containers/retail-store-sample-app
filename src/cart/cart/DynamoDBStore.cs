using Amazon.Runtime;
using Amazon.S3;
using Amazon.Runtime.CredentialManagement;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace cart;

public class DynamoDbStore
{
    private const string _tableName = "Cart";

    /*public async void CredCheck()
    {
        var chain = new CredentialProfileStoreChain();
        AWSCredentials awsCredentials;
        if (chain.TryGetAWSCredentials("default", out awsCredentials))
        {
            // Use awsCredentials to create an Amazon S3 service client
            using (var client = new AmazonS3Client(awsCredentials))
            {
                var response = await client.ListBucketsAsync();
                Console.WriteLine($"Number of buckets: {response.Buckets.Count}");
            }
        }
    }*/

    public void UpdateDataStore()
    {
        varifyTable();
        syncData();
    }

    public async Task<bool> DeleteCart(string customerId)
    {
        ShoppingCart customerCart = Carts.GetCart(customerId);
        foreach (Item tempItem in customerCart.Items)
        {
            await RemoveItem(customerId, tempItem.ItemId);
        }
        return true;
    }
    
    public async Task<bool> RemoveItem(string customerId, string itemId)
    {
        var client = new AmazonDynamoDBClient();
        string dynamoId = customerId + itemId;
        var key = new Dictionary<string, AttributeValue>
        {
            ["Id"] = new AttributeValue { N = dynamoId },
            ["CustomerId"] = new AttributeValue { S = customerId }
        };

        var request = new DeleteItemRequest
        {
            TableName = _tableName,
            Key = key,
        };

        var response = await client.DeleteItemAsync(request);
        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        
    }

    private void varifyTable()
    {
        var client = new AmazonDynamoDBClient();
        Task<ListTablesResponse> tableListTables = client.ListTablesAsync();
        List<string> currentTables = tableListTables.Result.TableNames;
        //List<string> currentTables = client.ListTables().TableNames;
        if (!currentTables.Contains(_tableName))
        {
            var request = new CreateTableRequest
            {
                TableName = _tableName,
                AttributeDefinitions = new List<AttributeDefinition>
                {
                    new AttributeDefinition
                    {
                        AttributeName = "Id",
                        // "S" = string, "N" = number, and so on.
                        AttributeType = "N"
                    },
                    new AttributeDefinition
                    {
                        AttributeName = "CustomerId",
                        AttributeType = "S"
                    }
                },
                KeySchema = new List<KeySchemaElement>
                {
                    new KeySchemaElement
                    {
                        AttributeName = "Id",
                        // "HASH" = hash key, "RANGE" = range key.
                        KeyType = "HASH"
                    },
                    new KeySchemaElement
                    {
                        AttributeName = "CustomerId",
                        KeyType = "RANGE"
                    },
                },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 10,
                    WriteCapacityUnits = 5
                },
            };

            var response = client.CreateTableAsync(request);
        }
    }

    private void syncData()
    {
        var client = new AmazonDynamoDBClient();
        
       // Dictionary<long, ShoppingCart> cartDictionary = ShoppingCart.ToDictionary(p => p.CustomerId);

        /*foreach (KeyValuePair<long, ShoppingCart> kvp in dictionary)
        {
            Console.WriteLine(
                "Key {0}: {1}, {2} pounds",
                kvp.Key,
                kvp.Value.Items);
        }*/
        
        foreach (ShoppingCart tempCart in Carts.Cart)
        {
            foreach (Item tempItem in tempCart.Items)
            {
                string dynamoDbId = tempCart.CustomerId + tempItem.ItemId;
                var request1 = new PutItemRequest
                {
                    TableName = _tableName,
                    Item = new Dictionary<string, AttributeValue>
                    {
                        { "Id", new AttributeValue { N = dynamoDbId} },
                        { "CustomerId", new AttributeValue { S = tempCart.CustomerId} },
                        { "ItemId", new AttributeValue { S = tempItem.ItemId } },
                        { "Quantity", new AttributeValue { S = tempItem.Quantity.ToString() } },
                        { "UnitPrice", new AttributeValue { S = tempItem.UnitPrice.ToString() } }
                    }
                };
                client.PutItemAsync(request1);
            }
        }
        

    }
    
}