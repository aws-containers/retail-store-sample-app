namespace DotNetCart;

public class Item
{
    public Item(string itemId, int quantity, int unitPrice)
    {
        ItemId = itemId;
        this.Quantity = quantity;
        this.UnitPrice = unitPrice;
    }

    public string ItemId { get; set; }

    public int Quantity { get; set; }

    public int UnitPrice { get; set; }
}