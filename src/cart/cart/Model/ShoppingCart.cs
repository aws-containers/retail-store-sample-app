namespace cart.Model;

public class ShoppingCart
{
    public ShoppingCart(string customerId, List<Item> items)
    {
        CustomerId = customerId;
        Items = items;
    }
    
    public string CustomerId { get; set; }

    public List<Item> Items { get; set; }

}