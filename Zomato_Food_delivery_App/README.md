# Zomato — LLD Exercise (Food Ordering System)

A small, runnable food-ordering system in plain Java 17. No frameworks, no build
tool, no database — the point is the **object design**, not the plumbing.

It is a from-scratch rewrite of the design taught in
[Lecture 11 "Tomato"](https://github.com/adityatandon15/Low-Level-Design-Course/tree/main/Lecture%2011/Java%20Code/Tomato),
using my own class names and with a few deliberate fixes (see
[Differences from the reference](#differences-from-the-reference)).

- **[ARCHITECTURE.md](ARCHITECTURE.md)** — entities, relationships, diagrams, request flow, SOLID mapping.
- **[INTERVIEW_QUESTIONS.md](INTERVIEW_QUESTIONS.md)** — 25 LLD questions with answers, graded easy → hard.

## Run it

```bash
./run.sh
```

or by hand:

```bash
javac -d out $(find . -name '*.java')
java -ea -cp out Main    # -ea turns on the asserts that act as the self-check
```

`Main` walks two complete flows and asserts the invariants after each one. If
anything in the design breaks, an `AssertionError` fires instead of the final
`All self-checks passed.`

## What the demo does

| Flow | Order type | Timing | Payment |
|---|---|---|---|
| 1 | Delivery | Now | UPI |
| 2 | Pickup | Scheduled | Credit card |

Plus three guard paths that are easy to get wrong and are asserted here:

- adding to the cart **before** picking a restaurant → rejected,
- ordering an item that is **not on that restaurant's menu** → rejected,
- checking out an **empty cart** → no order created,
- clearing the cart after payment must **not** empty the placed order's item list.

## File map

```
Zomato_Food_delivery_App/
├── Main.java                 # client / composition root — talks only to the facade
├── Zomato.java               # FACADE — the single entry point for all use cases
├── models/
│   ├── MenuItems.java        # one dish
│   ├── Resto.java            # restaurant + its menu
│   ├── User.java             # customer, owns one Cart
│   ├── Cart.java             # pre-checkout basket (one restaurant at a time)
│   ├── Order.java            # ABSTRACT order — common state + processPayment()
│   ├── DeliveryOrder.java    # delivered to the user's address
│   ├── PickUpOrder.java      # collected from the restaurant
│   └── OrderType.java        # DELIVERY | PICKUP
├── managers/
│   ├── RestoManager.java     # SINGLETON — restaurant registry + search
│   └── OrderManager.java     # SINGLETON — order book
├── strategies/
│   ├── PaymentStrategy.java  # STRATEGY interface
│   ├── UpiPaymentStrategy.java
│   └── CreditCardPaymentStrategy.java
├── factories/
│   ├── OrderFactory.java     # FACTORY METHOD interface
│   ├── NowOrderFactory.java  # order scheduled for "right now"
│   ├── ScheduledOrderFactory.java
│   └── OrderAssembler.java   # package-private wiring shared by both factories
├── services/
│   └── Notifications.java    # post-payment notification
└── utils/
    └── TimeUtils.java
```

## Patterns, in one line each

| Pattern | Where | Why it is there |
|---|---|---|
| **Facade** | `Zomato` | One coarse API (`searchRestos`, `addToCart`, `checkoutNow`, `payForOrder`) so the client never wires managers, factories and services itself. |
| **Singleton** | `RestoManager`, `OrderManager` | Exactly one restaurant registry and one order book per process; everyone reads the same data. |
| **Strategy** | `PaymentStrategy` + impls | Payment mode varies at runtime, chosen by the caller; `Order` never branches on it. |
| **Factory Method** | `OrderFactory` + impls | *Which* `Order` subclass to build, and *when* it is scheduled for, decided in one place. |
| **Template-ish inheritance** | `Order` → `DeliveryOrder` / `PickUpOrder` | Shared state and payment flow in the base, `getType()` and the address field in subclasses. |

## Differences from the reference

Each of these is a reasonable interview talking point — "what would you change
in this code?"

1. **`OrderType` enum instead of a `String`.** The reference compares
   `orderType.equals("Delivery")`; a typo silently falls through to the pickup
   branch. An enum makes it a compile-time error.
2. **`Cart.getItems()` returns a snapshot** (`List.copyOf`). In the reference,
   the order's item list *is* the cart's list, so `cart.clear()` after a
   successful payment also empties the item list of the order just placed. The
   notification is printed before the clear, which hides it. `Main` asserts
   against this.
3. **Unused `Cart` parameter dropped** from `OrderFactory.createOrder(...)` — the
   reference passes the cart and never reads it.
4. **`OrderAssembler`** holds the wiring the two factories had byte-for-byte
   identical; they now differ only in the timestamp they set.
5. **`Resto.findItemById(...)`** instead of looping over the menu inside the
   facade. The restaurant owns its menu, so it answers questions about it.
6. **`getInstance()` is `synchronized`.** The reference's lazy init is not
   thread-safe.
7. **Card numbers are masked** to the last four digits in the payment log.
8. **The facade keeps no collections.** The reference's `TomatoApp` holds none
   either, but my first draft kept `List<Resto>`, `List<User>` and `List<Order>`
   *next to* the managers — two sources of truth for the same data. Deleted.

## Name mapping (reference → here)

| Lecture 11 "Tomato" | This project |
|---|---|
| `TomatoApp` | `Zomato` |
| `Restaurant` | `Resto` |
| `MenuItem` | `MenuItems` |
| `RestaurantManager` | `RestoManager` |
| `NotificationService` | `Notifications` |
| `PickupOrder` | `PickUpOrder` |
| `getTotalCost()` | `getTotalPrice()` |
| `getTotal()` | `getTotalAmount()` |

## Known limits (deliberate)

Called out so they are answers in an interview, not oversights:

- `double` for money — fine for a demo, wrong for production (use `BigDecimal` or minor units).
- `Order` IDs come from a `static int` counter — not safe across threads or JVMs.
- Singletons are reached via `getInstance()` inside the facade, so the facade
  cannot be unit-tested with fakes. Constructor injection is the fix.
- Nothing is persisted; everything lives in memory for one run.
- No `OrderStatus` lifecycle (PLACED → PREPARING → OUT_FOR_DELIVERY → DELIVERED)
  and no delivery-partner assignment. Both are exercises in
  [INTERVIEW_QUESTIONS.md](INTERVIEW_QUESTIONS.md).
