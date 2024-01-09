using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Infrastructure;
using cart.Repository;
using cart.Model;

namespace cart.Controllers;

[ApiController]
[Route("carts")]
public class CartsController : ControllerBase
{
    
    private readonly ILogger<CartsController> _logger;
    private readonly ICartRepository _repository;
    
    public CartsController(ICartRepository repository, ILogger<CartsController> logger)
    {
        _logger = logger;
        _repository = repository;
    }

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
    public async Task<ActionResult<ShoppingCart>> GetCart(string customerId)
    {
        ShoppingCart myCart = await _repository.GetCart(customerId);
        return new ActionResult<ShoppingCart>(myCart);
    }

    // Delete: /carts/{customerId}
    //Delete a cart
    [HttpDelete("{customerId}")]
    [DefaultStatusCode(202)]
    public async Task<ActionResult<ShoppingCart>> DeleteCart(string customerId)
    {
        await _repository.DeleteCart(customerId);
        return StatusCode(StatusCodes.Status202Accepted);
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
    [DefaultStatusCode(201)]
    public async Task<ActionResult<Item>> AddItemToCart(string customerId, [FromBody] Item content)
    {
        await _repository.AddItem(customerId, content.ItemId, content.Quantity, content.UnitPrice);
        return Created($"{customerId}", content);
    }
    
    // Delete: /carts/{customerId}/items/{itemId:.*}
    // Delete an item from a cart
    [HttpDelete("{customerId}/items/{itemId}")]
    public async Task<AcceptedResult> RemoveItem(string customerId, string itemId)
    {
        await _repository.RemoveItem(customerId, itemId);
        
        return Accepted();
    }
}