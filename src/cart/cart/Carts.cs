namespace cart;

public static class Carts
{
    /*public Carts()
    {
        _cart = new List<ShoppingCart>();
    }*/
    private static List<ShoppingCart> _cart = new List<ShoppingCart>();

    public static List<ShoppingCart> Cart
    {
        get => _cart;
        set => _cart = value;
    }

    public static ShoppingCart GetCart(string customerId)
    {
        ShoppingCart newCart = new ShoppingCart(customerId); 
        foreach (ShoppingCart tempCart in Cart)
        {
            if (tempCart.CustomerId == customerId)
            {
                return tempCart;
            }
        }
        return newCart;
    }
    
    public static void AddItem(string customerId, Item newItem)
    {
        ShoppingCart customerCart = Carts.GetCart(customerId);
        int cartIndex = Cart.FindIndex(x => x.CustomerId.Equals(customerId));
        if (cartIndex == -1)
        {
            customerCart.Items.Add(newItem);
            Cart.Add(customerCart);
        }
        else
        {
            Cart[cartIndex].Items.Add(newItem);
        }
    }
    
    public static void UpdateItem(string customerId, Item updateItem)
    {
        ShoppingCart customerCart = GetCart(customerId);
        int cartIndex = Cart.FindIndex(x => x.CustomerId.Equals(customerId));
        if (cartIndex != -1)
        {
            int itemIndex = Cart[cartIndex].Items.FindIndex(y => y.ItemId.Equals(updateItem.ItemId));
            if (itemIndex != -1)
            {
                Cart[cartIndex].Items[itemIndex] = updateItem;
            }
        }
    }
    
    public static void RemoveItem(string customerId, Item newItem)
    {
        ShoppingCart customerCart = Carts.GetCart(customerId);
        int cartIndex = Cart.FindIndex(x => x.CustomerId.Equals(customerId));
        if (cartIndex == -1)
        {
            customerCart.Items.Remove(newItem);
            Cart.Add(customerCart);
        }
        else
        {
            Cart[cartIndex].Items.Remove(newItem);
        }
    }
    
    
    public static ShoppingCart DeleteCart(string customerId)
    {
        ShoppingCart customerCart = Carts.GetCart(customerId);
        int cartIndex = Cart.FindIndex(x => x.CustomerId.Equals(customerId));
        
        if (cartIndex != -1)
        {
            Cart.Remove(customerCart);
        }
        
        return customerCart;
    }
    
    public static Item? GetItem(string itemId, string customerId)
    {
        var customerItems = Carts.GetCart(customerId).Items;
        return customerItems.FirstOrDefault(tempItem => tempItem.ItemId == itemId);
    }
}