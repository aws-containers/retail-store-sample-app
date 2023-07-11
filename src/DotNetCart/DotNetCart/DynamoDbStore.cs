using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using System;

namespace DotNetCart;

public class DynamoDbStore
{
    private const string _tableName = "Cart";
    private const string useDynamoDB = "CARTS_DYNAMODB_CREATETABLE";
    
    public static string GetEnvironmentVariable(string name)
        => Environment.GetEnvironmentVariable(name) ?? "false";

    public void UpdateDataStore()
    {
        if (GetEnvironmentVariable(useDynamoDB).ToLower().Equals("true")) {
            varifyTable();
            syncData();
        }
    }

    public async Task<bool> DeleteCart(string customerId)
    {
        if (GetEnvironmentVariable(useDynamoDB).ToLower().Equals("true"))
        {
            ShoppingCart customerCart = Carts.GetCart(customerId);
            foreach (Item tempItem in customerCart.Items)
            {
                await RemoveItem(customerId, tempItem.ItemId);
            }
        }
        return true;
    }
    
    public async Task<bool> RemoveItem(string customerId, string itemId)
    {
        if (GetEnvironmentVariable(useDynamoDB).ToLower().Equals("true"))
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
        }

        return true;
        
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