using Microsoft.EntityFrameworkCore;

namespace cart.data
{
    public class ApiContext : DbContext
    {
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseInMemoryDatabase(databaseName: "CartDb");
        }
        public DbSet<Carts>? Carts { get; set; }
    }
}