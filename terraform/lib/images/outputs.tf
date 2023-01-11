output "result" {
  value = {
    assets   = local.assets_image
    catalog  = local.catalog_image
    cart     = local.cart_image
    checkout = local.checkout_image
    orders   = local.orders_image
    ui       = local.ui_image
  }
}