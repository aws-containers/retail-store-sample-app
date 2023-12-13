namespace cart;

public interface ICartService
{
    void AddItem(string customerId, Item newItem);
    void UpdateItem(string customerId, Item updateItem);
    void RemoveItem(string customerId, Item newItem);
    ShoppingCart GetCart(string customerId);
    ShoppingCart DeleteCart(string customerId);
    Item? GetItem(string itemId, string customerId);
}