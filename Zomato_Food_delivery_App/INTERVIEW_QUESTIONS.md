# LLD Interview Questions — Food Ordering System

Questions are grouped easy → hard. Each answer is folded; read the question,
answer it out loud, *then* expand. The ones marked **[code]** are answerable
only from this repo, so they double as a check that you still remember the
design.

---

## A. Walk-through and patterns

### 1. Walk me through this design in two minutes. **[code]**

<details><summary>Answer</summary>

Four layers, dependencies pointing one way: a client (`Main`), a facade
(`Zomato`), then managers / factories / strategies / services, and models at the
bottom.

The use case is: search restaurants by location → select one → add menu items to
your cart → check out → pay → get notified.

Four patterns carry it:
- **Facade** (`Zomato`) — the client calls one object, never wires managers itself.
- **Singleton** (`RestoManager`, `OrderManager`) — one restaurant registry, one order book.
- **Factory Method** (`OrderFactory` → `NowOrderFactory` / `ScheduledOrderFactory`)
  — decides which `Order` subclass to build and what time to stamp on it.
- **Strategy** (`PaymentStrategy` → UPI / card) — the payment channel is chosen
  by the caller and injected into the order.

The two variation points of the whole system are "what kind of order" and "how
to pay", and both are behind an interface, so neither needs an `if` in business
code.
</details>

### 2. Why a facade here? What goes wrong without it?

<details><summary>Answer</summary>

Without it, `Main` would have to know: that restaurants come from a singleton
registry, that an order must be built by a factory, that the factory needs the
cart's restaurant/items/total unpacked, that the new order must be registered in
`OrderManager`, and that notification + cart clearing happen only after a
successful payment. That knowledge would be copy-pasted into every caller (web
controller, CLI, test), and any change to the sequence would mean changing all
of them.

The facade makes that ordering a single, named thing: `checkoutNow(...)` then
`payForOrder(...)`.

The honest cost: a facade accumulates methods, one per use case, and can drift
into a God object. The discipline that keeps it honest here is that `Zomato`
holds **no state** — no lists, no counters. It only sequences calls.
</details>

### 3. Why are `RestoManager` and `OrderManager` singletons? When is that the wrong call?

<details><summary>Answer</summary>

Because they model *the* registry and *the* order book. Two `OrderManager`
instances would mean an order placed through one is invisible to the other, and
nothing in the domain makes sense with two of them.

It is the wrong call when:
- you need per-tenant or per-region instances (one order book per city),
- you want to unit-test a consumer with a fake (the singleton is reached
  globally, so you cannot swap it),
- the "single instance" requirement is really a *lifetime* requirement, which a
  DI container expresses better as a single registered bean.

In a Spring/Guice codebase you would write a normal class and scope it
singleton; the `getInstance()` idiom is what you use when there is no container.
</details>

### 4. Your `getInstance()` is `synchronized`. What does that cost, and what are the alternatives? **[code]**

<details><summary>Answer</summary>

Every call takes a monitor, even though only the very first call mutates
anything. Cheap at this scale, measurable on a hot path.

Alternatives, roughly in order of preference:
1. **Eager static field** — `private static final OrderManager INSTANCE = new OrderManager();`
   Simplest and thread-safe by class-initialisation. Use it unless construction
   is expensive or must be deferred.
2. **Initialization-on-demand holder** — a private static nested class holding
   the instance. Lazy *and* lock-free, guaranteed by the JVM's class-init rules.
3. **Enum singleton** — `enum OrderManager { INSTANCE; ... }`. Serialization- and
   reflection-safe; awkward if it must implement a framework interface.
4. **Double-checked locking with a `volatile` field** — correct only with
   `volatile`; without it the other thread can observe a partially constructed
   object. Only worth the subtlety if you genuinely need lazy + lock-free.

Interview trap: "double-checked locking" without mentioning `volatile` is a
wrong answer, not a partial one.
</details>

### 5. Strategy and Factory both "choose an implementation". What is the actual difference?

<details><summary>Answer</summary>

A **factory** makes a decision **once, at creation time**, and then gets out of
the way — `NowOrderFactory` returns a `DeliveryOrder` and is never consulted
again. It encapsulates *construction*.

A **strategy** is **kept** and called repeatedly during the object's life —
`order.processPayment()` calls `strategy.pay(total)`. It encapsulates
*behaviour*, and it is swappable at runtime (`order.setStrategy(...)`).

Test: if the varying thing is "which class do I instantiate" → factory. If it is
"which algorithm do I run" → strategy. In this code both appear, which is why
it is a good exercise: the factory picks `DeliveryOrder` vs `PickUpOrder`, the
strategy picks UPI vs card.
</details>

### 6. Why is `Order` abstract instead of carrying a `type` field? **[code]**

<details><summary>Answer</summary>

Because the two types do not just *label* differently, they *hold* different
data: a delivery order needs the user's address, a pickup order needs the
restaurant's. With one concrete class you would carry both fields and leave one
null in every order — and every reader would need an `if` to know which to
trust.

With `getType()` abstract, every `switch`-on-type disappears: `Notifications`
and `OrderManager.listAllOrders()` just print `order.getType()`.

The counter-argument worth voicing: if the subclasses had *no* extra state, two
classes would be ceremony and a `type` enum field would be the lazier, better
answer. Inheritance is earned by differing data or differing behaviour, not by
differing names.
</details>

### 7. Why `OrderType` as an enum rather than a `String`? **[code]**

<details><summary>Answer</summary>

The reference implementation does `orderType.equals("Delivery")`. Pass
`"delivery"` and you silently get a pickup order — wrong behaviour, no error,
no test failure unless you asserted the type. With an enum, `OrderType.DELIVRY`
does not compile.

The enum also gives you exhaustiveness: a `switch` over an enum can be checked,
and `values()` gives you the list for a UI dropdown. Strings as domain
identifiers are only acceptable at the system's edge (JSON, DB), and you parse
them into the enum at that edge.
</details>

---

## B. Entity modelling

### 8. Why does `Cart` hold a `Resto`? Why not allow a multi-restaurant cart? **[code]**

<details><summary>Answer</summary>

Because one order maps to one kitchen: one bill, one preparation time, one
pickup point. Modelling that as a field on `Cart` turns a business rule into a
structural invariant — `addItem` can refuse items while no restaurant is set,
and `isEmpty()` is true if *either* the restaurant or the items are missing.

If the product wanted multi-restaurant carts, the right model is a cart holding
several `SubCart`s (one per restaurant), with checkout producing one `Order`
per sub-cart. That is a different design, not a tweak — which is exactly the
kind of thing to flag in an interview before coding.
</details>

### 9. `Cart.getItems()` returns `List.copyOf(items)`. Why? **[code]**

<details><summary>Answer</summary>

This is the real bug I fixed from the reference. The checkout flow is:

```java
order.setItems(cart.getItems());   // same list object, if getItems() leaked it
...
if (order.processPayment()) { notify(order); cart.clear(); }   // items.clear()
```

If `getItems()` returned the live list, `cart.clear()` would also empty the item
list of the order that was just placed. The reference prints its notification
*before* clearing, so the output looks fine and the data is already gone.

Returning a snapshot fixes it at the single point every caller routes through,
instead of asking each caller to copy defensively. It also makes the returned
list immutable, so nobody can add an item to a cart by going through its getter.

`Main` asserts exactly this:
```java
assert deliveryOrder.getItems().size() == 2 : "order items were aliased to the cart";
```

The general lesson: a getter that returns a mutable collection hands out write
access to your internals. Return a copy, an unmodifiable view, or a stream.
</details>

### 10. `User` *has a* `Cart`, but `DeliveryOrder` *is an* `Order`. Justify both. **[code]**

<details><summary>Answer</summary>

`User` has-a `Cart` because a cart is not a kind of user — it is a part the user
owns. The field is `final` and created in the constructor (composition): the
cart's lifetime is the user's, there is no `setCart`, and so no caller ever has
to null-check `user.getCart()`.

`DeliveryOrder` is-an `Order` because it genuinely satisfies everything an
`Order` promises — it has an id, items, a total, and it can process payment —
and callers can hold it as an `Order` without knowing the difference
(Liskov). It adds one field rather than changing any inherited behaviour.

Rule of thumb: inheritance for "is substitutable for", composition for
"is made of". When in doubt, compose — it is reversible.
</details>

### 11. Where do coupons, taxes and delivery fees belong? Not `Cart.getTotalPrice()`?

<details><summary>Answer</summary>

Not in `Cart`. Today `getTotalPrice()` is a pure sum of item prices, which is
the cart's own business. A coupon is not: it needs the user (first-order
discount?), the restaurant (who funds it?), the time (happy hour), the order
type (free delivery over ₹X) and validity rules.

Design: pull pricing into its own collaborator.

```java
interface PricingRule { Money apply(OrderContext ctx, Money runningTotal); }
// ItemSubtotalRule, CouponRule, TaxRule, DeliveryFeeRule, SurgeRule
class PricingEngine { Money priceFor(OrderContext ctx); }   // folds the rules in order
```

Order matters (tax after discount, usually), so the engine owns an ordered list
— that is a **chain of rules**, close to Chain of Responsibility except every
link runs. `Zomato.checkout` then asks the engine for the total instead of the
cart, and `Cart.getTotalPrice()` stays an honest subtotal.

A `Decorator` over a `Priceable` is the other valid answer; rules are easier to
reorder and to test individually.
</details>

---

## C. Trade-offs in this code **[code]**

### 12. `Order` IDs come from a `static int` counter. What is wrong with that?

<details><summary>Answer</summary>

Three things:
1. **Not thread-safe.** `++nextOrderId` is read-modify-write; two concurrent
   checkouts can get the same id. Minimum fix: `AtomicInteger.incrementAndGet()`.
2. **Not unique beyond the JVM.** Restart and it starts at 1 again; run two
   instances and they collide. Real fix: a DB sequence, a UUID, or a Snowflake-style
   id if you need sortability.
3. **Static state is global state.** Tests cannot reset it, so assertions on
   specific ids are order-dependent.

Worth saying out loud: for an in-memory exercise the counter is fine, and I
would not pretend otherwise — but I would name the ceiling rather than let the
interviewer find it.
</details>

### 13. Money is a `double` here. Why is that wrong, and what instead?

<details><summary>Answer</summary>

`double` is binary floating point: `0.1 + 0.2 == 0.30000000000000004`. Summing
item prices accumulates error, and `==` comparisons on totals become unreliable
— exactly what a payment system must not do. (The asserts in `Main` only pass
because these prices are whole numbers.)

Options:
- **`BigDecimal`** with an explicit scale and `RoundingMode` — correct, verbose,
  and you must never use the `double` constructor (`new BigDecimal(0.1)` keeps
  the error; use `BigDecimal.valueOf(0.1)` or the `String` form).
- **`long` minor units** (paise/cents) — fast and exact for addition; you still
  decide rounding for percentages like tax.
- A **`Money` value type** wrapping one of those plus a `Currency`, so you cannot
  accidentally add ₹ to $.

For an interview: name the failure mode first, then pick minor units or
`BigDecimal`, and mention currency.
</details>

### 14. Two users check out at the same instant. What breaks? **[code]**

<details><summary>Answer</summary>

- `OrderManager.orders` and `RestoManager.restos` are plain `ArrayList`s;
  concurrent `add` can lose an element or corrupt the internal array, and
  iterating while another thread adds throws `ConcurrentModificationException`.
- `Order.nextOrderId` can hand out duplicate ids (previous question).
- `Cart` is not synchronised — only a problem if one user's session is handled
  by several threads, which a mobile client retrying can cause.
- `getInstance()` is already `synchronized`, so the singletons themselves are safe.

Fixes, cheapest first: `CopyOnWriteArrayList` (reads dominate here) or a
`ConcurrentHashMap` keyed by id if you need lookups; `AtomicInteger` for ids;
and keep a cart per session rather than sharing one.

The deeper answer: in a real system the order book is a database with a
transaction, and this in-memory manager is only standing in for it — so the
right boundary for concurrency is the transaction, not a Java lock.
</details>

### 15. How would you unit-test `Zomato.checkout` today? What refactor would you do first?

<details><summary>Answer</summary>

Today you can only test it end-to-end: `Zomato`'s constructor seeds three real
restaurants, and `checkout` reaches `OrderManager.getInstance()` internally. The
singletons survive between tests, so tests leak state into each other and the
order ids keep climbing.

Refactor: inject the collaborators.

```java
public Zomato(RestoManager restoManager, OrderManager orderManager, Notifier notifier) { ... }
```

With that, a test can pass fakes, assert that exactly one order was registered,
and use a `Notifier` spy to check the notification fires **only** on a successful
payment. Also pull restaurant seeding out of the constructor — a constructor
that loads data is a constructor you cannot use in a test.

That is the Dependency Inversion fix: depend on an abstraction passed in, not on
a concrete class fetched statically.
</details>

### 16. Should `processPayment()` live on `Order` at all?

<details><summary>Answer</summary>

Defensible both ways, and saying so is the strong answer.

**For:** the order knows the amount and the chosen channel, so `processPayment()`
is a rich domain method; the alternative risks an anemic model where every model
is a bag of getters and all logic sits in services.

**Against:** payment is an external, failable, retryable side effect — it needs
timeouts, idempotency keys, a gateway client and a transaction log. None of that
belongs in a model whose other job is holding fields. A `PaymentService.pay(order)`
keeps `Order` persistable and testable.

Where I would land: keep the *decision* (`which strategy`, `how much`) on the
order, move the *execution* into a service as the system grows. For this
exercise, on the order is fine — and `processPayment()` returning `false` when no
strategy is set is already the guard that matters.
</details>

### 17. What would you delete from this design? **[code]**

<details><summary>Answer</summary>

A deliberately uncomfortable question — being able to answer it shows you are
not just adding patterns.

- `OrderManager.cancelOrder()` removes the order from the list, which is wrong
  domain behaviour: a cancelled order must stay auditable with a status. Either
  implement the status or drop the method.
- Several model setters (`Resto.setMenu`, `MenuItems.setItemId`, `User.setUserId`)
  exist only because "models have getters and setters" is a habit. Mutable ids
  are a bug waiting to happen; most of these should go.
- `Cart.removeItem` is unused by the demo — keep it (a cart genuinely needs it)
  but recognise it is untested.
- `Order.getStrategy()` is unused; a payment strategy is an input, not something
  callers should read back.

The general point: every setter is a promise that the field can change safely at
any time. Most domain fields cannot.
</details>

---

## D. Extension design

### 18. Add an order lifecycle: PLACED → PREPARING → OUT_FOR_DELIVERY → DELIVERED (and CANCELLED).

<details><summary>Answer</summary>

**State pattern**, because the legal transitions differ per state and the
behaviour differs with them (you can cancel a PLACED order, not a DELIVERED one).

```java
interface OrderState {
    OrderState next(Order o);          // advance
    OrderState cancel(Order o);        // may throw IllegalStateException
    String name();
}
// PlacedState, PreparingState, OutForDeliveryState, DeliveredState, CancelledState
```

`Order` holds `private OrderState state = new PlacedState();` and delegates:
`order.advance()` → `state = state.next(this)`.

Why not an enum with a `switch`? That works and is often enough — an enum with
an `allowedNext` set is the lazy version and I would start there. You graduate to
State when each state carries *behaviour* (different notifications, different
refund rules, different timeouts), because then the `switch` appears in five
places instead of one.

Worth adding either way: a `List<StatusChange>` audit trail with timestamps.
Status transitions are exactly the thing support teams ask about.
</details>

### 19. Notifications must go to SMS, email and push — and the restaurant needs one too.

<details><summary>Answer</summary>

**Observer.** Today `Notifications.notify(order)` is a static method that prints
— one hardcoded channel, and `Zomato.payForOrder` names it directly.

```java
interface OrderListener { void onOrderPlaced(Order order); }
// SmsListener, EmailListener, PushListener, RestaurantDashboardListener, AnalyticsListener

class OrderEvents {
    private final List<OrderListener> listeners = new CopyOnWriteArrayList<>();
    void subscribe(OrderListener l);
    void publish(Order order);   // for each listener
}
```

`Zomato` publishes one event and stops knowing who listens — new channels need
no edit to the facade (Open/Closed).

Three things to raise unprompted, because they are what the follow-up will be:
1. One failing listener must not break the others or the payment — catch per
   listener, or publish asynchronously via a queue.
2. Notification must be **idempotent**; a retried payment should not send two
   SMS.
3. Each listener should decide its own template; the event carries data, not
   formatted text.
</details>

### 20. Assign a delivery partner to each delivery order.

<details><summary>Answer</summary>

New model plus a strategy for the choice:

```java
class DeliveryPartner { String id; String name; Location current; PartnerStatus status; }
interface AssignmentStrategy { Optional<DeliveryPartner> assign(DeliveryOrder o, List<DeliveryPartner> free); }
// NearestPartnerStrategy, LeastBusyStrategy, HighestRatedStrategy
class PartnerManager { /* registry of partners, like RestoManager */ }
```

`PickUpOrder` has no partner — which is the payoff for having separate
subclasses rather than one `Order` with a nullable partner field.

Points worth making: assignment must be **atomic** (two orders cannot claim one
partner — compare-and-set on partner status, or a queue per zone); it needs a
**timeout and reassignment** if the partner does not accept; and this is a
batch-matching problem at scale, not a per-order greedy pick, so the strategy
interface is what lets you swap greedy for batched later.
</details>

### 21. Add restaurant search by cuisine, rating and "open now".

<details><summary>Answer</summary>

Keep it in `RestoManager` — it owns the collection — and stop adding one method
per criterion (`searchByLocation`, `searchByCuisine`, `searchByLocationAndCuisine`…
is a combinatorial trap).

```java
List<Resto> search(Predicate<Resto> filter);   // callers compose with .and()
// or a small SearchCriteria/Specification object if you want it named and testable
```

That is the **Specification** pattern in its lightest form, and `Predicate`
means no new types at all.

Then flag the real-world gap: a linear scan over every restaurant is fine for
three rows and wrong for a million. Production needs an index — geo-sharded, in
a search engine — and the `search(...)` signature is exactly the seam that lets
you replace the implementation without touching callers.

`isOpenNow()` belongs on `Resto` (it owns its hours), not in the filter.
</details>

### 22. Scheduled orders exist in the model, but nothing fires them at the scheduled time. Design that.

<details><summary>Answer</summary>

Right now `ScheduledOrderFactory` only stamps a `String` on the order — worth
naming that honestly: it records an intent, it does not schedule anything. Two
fixes before anything else: the field should be an `Instant`/`LocalDateTime`,
not a `String`, and it should be validated as being in the future.

Then:
- **In-process:** a `ScheduledExecutorService` with a delayed task per order.
  Simple, and it loses every pending order on restart.
- **Durable:** persist the order with a `fireAt` column and run a poller that
  claims due rows (`SELECT ... WHERE fireAt <= now AND claimed = false FOR UPDATE
  SKIP LOCKED`). Survives restarts, scales to many workers.
- **Queue-native:** a delayed message / timer in SQS, RabbitMQ or a workflow
  engine (Temporal). Least code you own.

Either way: the fire action must be idempotent (keyed by order id), you need a
grace policy for "the restaurant closed before the slot", and the user must be
able to cancel before `fireAt`.
</details>

---

## E. Stepping back

### 23. Which SOLID principle does this code break, and how would you fix it? **[code]**

<details><summary>Answer</summary>

**Dependency Inversion.** `Zomato` depends on concrete singletons it fetches
itself:

```java
return RestoManager.getInstance().searchByLocation(loc);
OrderManager.getInstance().addOrder(order);
```

The facade's dependencies are invisible in its constructor and impossible to
replace, so it cannot be tested with fakes or reused with a different store.

Fix: define interfaces (`RestoRepository`, `OrderRepository`) and inject
implementations through the constructor. The singleton can still be the single
instance the app wires in — "one instance" is a lifetime decision for the
composition root, not something a consumer should reach out and grab.

A secondary, smaller one: **Open/Closed** in `OrderAssembler.newOrderOfType`,
which switches on `OrderType`, so a third fulfilment type means editing existing
code. A `Map<OrderType, Supplier<Order>>` registry removes the switch — but with
two cases, the switch is the lazier and clearer choice, and I would not
pre-build the registry.
</details>

### 24. How does this map to a real food-delivery system? What is deliberately missing?

<details><summary>Answer</summary>

What this exercise is actually about — object boundaries, variation points,
invariants — is genuine and survives into production. What is missing is
everything that is *not* LLD:

- **Persistence:** repositories, transactions, optimistic locking on the order row.
- **Services and their boundaries:** restaurant catalogue, cart, order, payment,
  delivery-partner matching, notification — separate services, each owning its data.
- **Payments for real:** a gateway, idempotency keys, webhooks for async
  confirmation, reconciliation, refunds. `strategy.pay()` returning `void` and
  `processPayment()` always returning `true` is the biggest simplification in
  this repo — a real `pay` returns a result that can be PENDING.
- **Search at scale:** geo-indexed catalogue, caching, per-city sharding.
- **Consistency:** an order touching payment + inventory + assignment is a saga
  with compensations, not one transaction.

Saying "the strategy returns void, so failure is unrepresentable" before the
interviewer notices is worth more than any pattern name.
</details>

### 25. You have 45 minutes and a whiteboard: "design a food delivery app". What order do you work in?

<details><summary>Answer</summary>

1. **Clarify scope (3 min).** Delivery and pickup? Scheduled orders? One
   restaurant per order? Payments in scope? Partner assignment in scope?
   Single-city? Write the chosen scope down and move.
2. **Actors and core use cases (5 min).** Customer searches → selects → adds to
   cart → checks out → pays → tracks. Restaurant receives. Partner delivers.
3. **Entities and relationships (10 min).** `User`, `Resto`, `MenuItems`, `Cart`,
   `Order` (+ subtypes), `PaymentStrategy`. State the invariants out loud: one
   restaurant per cart, an order is immutable once placed, totals are computed not
   stored by the client.
4. **Name the variation points (5 min).** Payment channel, fulfilment type, order
   timing, notification channel, partner assignment. These are where the patterns
   go — and the only places they are justified.
5. **Class diagram + the one critical flow (10 min).** Draw checkout end to end.
6. **Code the core (8 min).** `Order` + one factory + the strategy interface and
   one implementation. Interfaces first, then one concrete path.
7. **Name the ceilings (4 min).** Money as double, id generation, concurrency on
   the order book, what breaks at 10k orders/minute.

Discipline that scores: do not open with patterns. Derive the entities, find the
things that vary, *then* name the pattern that absorbs the variation. An
interviewer who hears "I'll use Singleton, Factory, Strategy and Observer" in
minute one assumes you memorised a list.
</details>

---

## Rapid-fire self check

Cover the right column.

| Question | Answer |
|---|---|
| Pattern for "payment mode varies at runtime" | Strategy |
| Pattern for "which subclass to instantiate" | Factory Method |
| Pattern for "one coarse API over many subsystems" | Facade |
| Pattern for "behaviour changes with lifecycle status" | State |
| Pattern for "many parties react to one event" | Observer |
| Pattern for "stack optional charges onto a price" | Decorator |
| Pattern for "composable search filters" | Specification |
| Why does `Cart` know its `Resto`? | One order = one kitchen; makes the rule structural |
| Why does `getItems()` copy? | Stops `cart.clear()` from emptying a placed order |
| Why is `getType()` abstract? | Deletes every `instanceof` / type `switch` |
| Safest lazy singleton without locks | Initialization-on-demand holder class |
| What must `volatile` guard in DCL? | The instance field — otherwise a partially built object is visible |
| Why not `double` for money? | Binary floating point cannot represent decimal fractions exactly |
| One SOLID violation in this repo | DIP — facade calls `getInstance()` instead of taking injected repositories |
