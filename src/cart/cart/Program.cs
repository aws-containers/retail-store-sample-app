using Prometheus;
using cart.Repository;

var builder = WebApplication.CreateBuilder(args);

var tableName = Environment.GetEnvironmentVariable("CARTS_DYNAMODB_TABLENAME") ?? "Items";
var createTable = Environment.GetEnvironmentVariable("CARTS_DYNAMODB_CREATETABLE") ?? "false";
var dynamodbEndpoint = Environment.GetEnvironmentVariable("CARTS_DYNAMODB_ENDPOINT") ?? "";
var cartsPort = Environment.GetEnvironmentVariable("PORT") ?? "8080";

var repository = new DynamoDbRepository(tableName, bool.Parse(createTable), dynamodbEndpoint);

builder.Services.AddSingleton<ICartRepository>(repository);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddHealthChecks().AddCheck("dynamodb", repository);

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseRouting();

app.UseHttpMetrics();

app.UseAuthorization();
app.MapControllers();

app.MapMetrics("/metrics");
app.MapHealthChecks("/health");

var devUrl = "http://localhost:" + cartsPort;
app.Run(devUrl);