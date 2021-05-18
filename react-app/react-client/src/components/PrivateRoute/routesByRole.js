export const managerPaths = [
  "/",
  "/help",
  "/orders",
  "/product/newProduct",
  "/products",
  "/reservations",
  "/checkout",
  "/registration",
  "/reservation",
  "/checkin",
  "/login",
  "/rates",
  "/checkout/:reservationId/expenses",
];

export const clientPaths = [
  "/",
  "/help/:id",
  "/products/:id",
  "/ratings/:reservationId/rate",
  "/expenses/:id",
  "/login"
];

export const CLIENT = "CLIENT";
export const MANAGER = "MANAGER";
