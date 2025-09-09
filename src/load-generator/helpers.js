let productIds = [
  "cc789f85-1476-452a-8100-9e74502198e0",
  "87e89b11-d319-446d-b9be-50adcca5224a",
  "4f18544b-70a5-4352-8e19-0d070f46745d",
  "79bce3f3-935f-4912-8c62-0d2f3e059405",
  "d27cf49f-b689-4a75-a249-d373e0330bb5",
  "1ca35e86-4b4c-4124-b6b5-076ba4134d0d",
  "631a3db5-ac07-492c-a994-8cd56923c112",
  "8757729a-c518-4356-8694-9e795a9b3237",
  "d4edfedb-dbe9-4dd9-aae8-009489394955",
];

function getAllProducts(context, ee, next) {
  context.vars.allProducts = productIds;

  next();
}

function setRandomProductId(req, context, ee, next) {
  const index = Math.floor(Math.random() * productIds.length);

  req.form.productId = productIds[index];

  next();
}

module.exports = {
  setRandomProductId,
  getAllProducts,
};
