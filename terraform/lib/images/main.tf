locals {
  default_tag = try(var.container_image_overrides.default_tag, local.published_tag)

  assets_default_image = "${local.published_repository}/retail-store-sample-assets:${local.default_tag}"
  assets_image         = try(var.container_image_overrides.assets, local.assets_default_image)

  catalog_default_image = "${local.published_repository}/retail-store-sample-catalog:${local.default_tag}"
  catalog_image         = try(var.container_image_overrides.catalog, local.catalog_default_image)

  cart_default_image = "${local.published_repository}/retail-store-sample-cart:${local.default_tag}"
  cart_image         = try(var.container_image_overrides.cart, local.cart_default_image)

  checkout_default_image = "${local.published_repository}/retail-store-sample-checkout:${local.default_tag}"
  checkout_image         = try(var.container_image_overrides.checkout, local.checkout_default_image)

  orders_default_image = "${local.published_repository}/retail-store-sample-orders:${local.default_tag}"
  orders_image         = try(var.container_image_overrides.orders, local.orders_default_image)

  ui_default_image = "${local.published_repository}/retail-store-sample-ui:${local.default_tag}"
  ui_image         = try(var.container_image_overrides.ui, local.ui_default_image)
}