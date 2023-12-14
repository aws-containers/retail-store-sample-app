using cart;

var builder = WebApplication.CreateBuilder(args);
// Add services to the container.
var CARTS_DYNAMODB_CREATETABLE = Environment.GetEnvironmentVariable("CARTS_DYNAMODB_CREATETABLE");
var CARTS_DYNAMODB_ENDPOINT = Environment.GetEnvironmentVariable("CARTS_DYNAMODB_ENDPOINT");

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();
app.MapControllers();

app.Run();