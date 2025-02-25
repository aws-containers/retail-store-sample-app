output "result" {
  value = {
    catalog = merge({
      url = local.catalog_image
    }, zipmap(["repository", "tag"], split(":", local.catalog_image)))
    cart = merge({
      url = local.cart_image
    }, zipmap(["repository", "tag"], split(":", local.cart_image)))
    checkout = merge({
      url = local.checkout_image
    }, zipmap(["repository", "tag"], split(":", local.checkout_image)))
    orders = merge({
      url = local.orders_image
    }, zipmap(["repository", "tag"], split(":", local.orders_image)))
    ui = merge({
      url = local.ui_image
    }, zipmap(["repository", "tag"], split(":", local.ui_image)))
  }

  description = "Computed image results"
}
