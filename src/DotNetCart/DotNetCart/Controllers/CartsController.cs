using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
//using Microsoft.AspNetCore.Mvc.Infrastructure;
using Microsoft.Extensions.Logging;

namespace DotNetCart.Controllers;

[ApiController]
[Route("carts")]
public class CartsController : ControllerBase
{
    
    private readonly ILogger<CartsController> _logger;
    
    DynamoDbStore _dDbStor = new DynamoDbStore();
    
    public CartsController(ILogger<CartsController> logger)
    {
        _logger = logger;
    }

    //private Carts Cart { get; set; }
    
    // Carts _cart = new Carts();
    /*Carts Cart
    {
        get
        {
            if (!_memoryCache.TryGetValue(CacheKey, out Carts? cartsValue))
            {
                cartsValue = new Carts();
                var cacheEntryOptions = new MemoryCacheEntryOptions()
                    .SetSlidingExpiration(TimeSpan.FromSeconds(600));
                _memoryCache.Set(CacheKey, cartsValue, cacheEntryOptions);
            }
            
            if (cartsValue is null)
            {
                cartsValue = new Carts();
            }

            return cartsValue;
        }
        set {
            var cacheEntryOptions = new MemoryCacheEntryOptions()
                .SetSlidingExpiration(TimeSpan.FromSeconds(600));
            _memoryCache.Set(CacheKey, value, cacheEntryOptions);
        }
    }*/

    // GET: /carts/{customerId}
    // Retrieve a cart
    /*
     returns: 
     {
      "customerId": "string",
      "items": [
        {
          "itemId": "string",
          "quantity": 0,
          "unitPrice": 0
        }
      ]
    }
     */
    [HttpGet("{customerId}")]
    public ActionResult<ShoppingCart> GetCart(string customerId)
    {
        ShoppingCart myCart = Carts.GetCart(customerId);
        _dDbStor.UpdateDataStore();
        return new ActionResult<ShoppingCart>(myCart);
    }

    // Delete: /carts/{customerId}
    //Delete a cart
    [HttpDelete("{customerId}")]
    public async Task<ActionResult<ShoppingCart>> DeleteCart(string customerId)
    {
        await _dDbStor.DeleteCart(customerId);
        ShoppingCart cartDeleted = Carts.DeleteCart(customerId);

        //Response.StatusCode = StatusCodes.Status202Accepted;
        return StatusCode(StatusCodes.Status202Accepted, cartDeleted);
    }


    // GET: /carts/{customerId}/merge
    // Merge two carts contents
    // Sample request urk: http://localhost:8080/carts/3333/merge?sessionId=4343
    [HttpGet("{customerId}/merge")]
    public void MergeCustomer(string customerId , string sessionId)
    {
        //This did nothing in the Java version.
    }
    
    // Get: /carts/{customerId}/items/{itemId:.*}
    // Retrieve an item from a cart
    [HttpGet("{customerId}/items/{itemId}")]
    public ActionResult<Item> GetSingleItem(string customerId, string itemId)
    {
        Item myCart = Carts.GetItem(itemId, customerId) ?? new Item("-1", 0, 0);
        return new ActionResult<Item>(myCart);
    }
    
    // GET: /carts/{customerId}/items
    // Retrieve all items from a cart
    [HttpGet("{customerId}/items")]
    public ActionResult<List<Item>> GetItems(string customerId)
    {
        ShoppingCart myCart = Carts.GetCart(customerId);
        return new ActionResult<List<Item>>(myCart.Items);
    }
    
    // POST: /carts/{customerId}/items
    // Add an item to a cart
    /* Sample input     
        {
          "itemId": "123456",
          "quantity": 1,
          "unitPrice": 23
        }
     */
    [HttpPost("{customerId}/items")]
    public ActionResult<Item> AddItemToCart(string customerId, [FromBody] Item content)
    {
        Carts.AddItem(customerId, content);
        _dDbStor.UpdateDataStore();
        Response.StatusCode = StatusCodes.Status201Created;
        return Created($"{customerId}", content);
    }
    
    
    // Delete: /carts/{customerId}/items/{itemId:.*}
    // Delete an item from a cart
    [HttpDelete("{customerId}/items/{itemId}")]
    public async Task<AcceptedResult> RemoveItem(string customerId, string itemId)
    {
        Item? itemToDelete = Carts.GetItem(itemId, customerId);
        if (itemToDelete is not null)
        {
            Carts.RemoveItem(customerId, itemToDelete);
            await _dDbStor.RemoveItem(customerId, itemId);
        }
        
        return Accepted();
    }
    
    // Patch: /carts/{customerId}/items
    // Update an item in a cart
    [HttpPatch("{customerId}/items")]
    public ActionResult UpdateItem(string customerId, [FromBody] Item content)
    {
        Carts.UpdateItem(customerId, content);
        _dDbStor.UpdateDataStore();
        return Accepted();
    }
}