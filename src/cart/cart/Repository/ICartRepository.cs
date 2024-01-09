using cart.Model;

namespace cart.Repository;

public interface ICartRepository
{
    Task<Item> AddItem(string customerId, string itemId, int quantity, int unitPrice);
    Task<bool> RemoveItem(string customerId, string item);
    Task<ShoppingCart> GetCart(string customerId);
    Task<bool> DeleteCart(string customerId);
}