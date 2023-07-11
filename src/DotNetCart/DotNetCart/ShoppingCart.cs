using System.Collections.Generic;

namespace DotNetCart;

public class ShoppingCart
{
    public ShoppingCart(string customerId)
    {
        CustomerId = customerId;
        Items = this._items;
    }

    private List<Item> _items = new List<Item>();
    public string CustomerId { get; set; }

    public List<Item> Items { get; set; }

}