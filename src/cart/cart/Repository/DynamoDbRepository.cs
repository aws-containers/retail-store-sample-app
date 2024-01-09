using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.Runtime.Endpoints;
using cart.Model;
using Microsoft.Extensions.Diagnostics.HealthChecks;

namespace cart.Repository;

public class DynamoDbRepository : ICartRepository, IHealthCheck
{
    private readonly string _tableName;

    private readonly AmazonDynamoDBClient _client;

    public DynamoDbRepository(string tableName, bool createTable, string endpoint)
    {
        var clientConfig = new AmazonDynamoDBConfig();

        if (!string.IsNullOrEmpty(endpoint))
        {
            clientConfig.EndpointProvider = new StaticEndpointProvider(endpoint);
        }

        this._tableName = tableName;
        this._client = new AmazonDynamoDBClient(clientConfig);

        if (createTable)
        {
            this.CreateTable();
        }
    }
    
    public async Task<ShoppingCart> GetCart(string customerId)
    {
        var expressionAttributeValues = new Dictionary<string, AttributeValue>
        {
            {":v_customerId", new AttributeValue { S = customerId}}
        };
        
        QueryRequest queryRequest = new QueryRequest
        {
            TableName = _tableName,
            IndexName = "idx_global_customerId",
            KeyConditionExpression = "customerId = :v_customerId",
            ExpressionAttributeValues = expressionAttributeValues,
            ScanIndexForward = true
        };

        List<Item> resultItems = [];
        
        var result = await _client.QueryAsync(queryRequest);
        var items = result.Items;
        foreach (var currentItem in items)
        {
            resultItems.Add(new Item(currentItem["itemId"].S, Int32.Parse(currentItem["quantity"].N), Int32.Parse(currentItem["unitPrice"].N)));
        }

        return new ShoppingCart(customerId, resultItems);
    }

    public async Task<Item> AddItem(string customerId, string itemId, int quantity, int unitPrice)
    {
        var dynamoDbId = customerId + ":" + itemId;
        var request = new PutItemRequest
        {
            TableName = _tableName,
            Item = new Dictionary<string, AttributeValue>
            {
                { "id", new AttributeValue { S = dynamoDbId} },
                { "customerId", new AttributeValue { S = customerId} },
                { "itemId", new AttributeValue { S = itemId } },
                { "quantity", new AttributeValue { N = quantity.ToString() } },
                { "unitPrice", new AttributeValue { N = unitPrice.ToString() } }
            }
        };
        await _client.PutItemAsync(request);

        return new Item(itemId, quantity, unitPrice);
    }

    public async Task<bool> DeleteCart(string customerId)
    {
        var customerCart = await GetCart(customerId);
        foreach (var tempItem in customerCart.Items)
        {
            await RemoveItem(customerId, tempItem.ItemId);
        }

        return true;
    }
    
    public async Task<bool> RemoveItem(string customerId, string itemId)
    {
        var dynamoId = customerId + ":" + itemId;
        var key = new Dictionary<string, AttributeValue>
        {
            {"id", new AttributeValue { S = dynamoId }}
        };

        var request = new DeleteItemRequest
        {
            TableName = _tableName,
            Key = key,
        };

        await _client.DeleteItemAsync(request);

        return true;
    }

    private void CreateTable()
    {
        var indexKeySchema = new List<KeySchemaElement> {
            {new KeySchemaElement { AttributeName = "customerId", KeyType = "HASH"}}
        };
        
        var customerIndex = new GlobalSecondaryIndex
        {
            IndexName = "idx_global_customerId",
            ProvisionedThroughput = new ProvisionedThroughput
            {
                ReadCapacityUnits = 1,
                WriteCapacityUnits = 1
            },
            KeySchema = indexKeySchema,
            Projection = new Projection { ProjectionType = "ALL" }
        };
        
        var request = new CreateTableRequest
        {
            TableName = _tableName,
            AttributeDefinitions =
            {
                new AttributeDefinition
                {
                    AttributeName = "id",
                    AttributeType = "S"
                },
                new AttributeDefinition
                {
                    AttributeName = "customerId",
                    AttributeType = "S"
                }
            },
            KeySchema = 
            {
                new KeySchemaElement
                {
                    AttributeName = "id",
                    KeyType = "HASH"
                }
            },
            GlobalSecondaryIndexes = {customerIndex},
            ProvisionedThroughput = new ProvisionedThroughput
            {
                ReadCapacityUnits = 1,
                WriteCapacityUnits = 1
            },
        };

        _client.CreateTableAsync(request);
    }
    
    public async Task<HealthCheckResult> CheckHealthAsync(HealthCheckContext context, CancellationToken cancellationToken = default)
    {
        try
        {
            await _client.DescribeTableAsync(_tableName, cancellationToken);

            return HealthCheckResult.Healthy();
        }
        catch (Exception ex)
        {
            return HealthCheckResult.Unhealthy(ex.Message); 
        }                   
    }
}