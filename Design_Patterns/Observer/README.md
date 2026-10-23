# Observer Pattern

**Intent:** Define a one-to-many dependency so that when one object changes state, all its
dependents are notified automatically — without the subject knowing their concrete types.

Also called: Publish–Subscribe, Listener.

---

## Mental model

A YouTube channel uploads a video. Every subscriber gets a notification. The channel
doesn't know or care who they are — only that they can be `update()`d.

> The subject holds the observers. The observers do **not** hold the subject.

---

## Architecture

```
            ┌──────────────────────────────┐                  ┌───────────────────────────┐
            │     Ichannel  «Subject»      │                  │  Isubscriber  «Observer»  │
            ├──────────────────────────────┤    0..* holds    ├───────────────────────────┤
            │ + subscribe(Isubscriber)     │ ────────────────▸│ + update(String message)  │
            │ + unsubscribe(Isubscriber)   │                  │ + getName() : String      │
            │ + notifySubscribers(String)  │                  │                           │
            └──────────────────────────────┘                  └───────────────────────────┘
                            △                                              △
                            ╎                                              ╎  implements
            ┌──────────────────────────────┐                  ┌───────────────────────────┐
            │     Channel  «Concrete»      │                  │   Subscriber  «Concrete»  │
            ├──────────────────────────────┤                  ├───────────────────────────┤
            │ - channelName : String       │                  │ - name : String           │
            │ - subscribers : List<Isub>   │                  │                           │
            │ + uploadVideo(String)        │                  │ + update(msg) → println   │
            └──────────────────────────────┘                  └───────────────────────────┘
```

`uploadVideo()` is the trigger: it changes state, then calls `notifySubscribers()`.

Note the asymmetry: `Channel` depends on `Isubscriber` (the interface).
`Subscriber` depends on **nothing**. That is the decoupling the pattern buys.

## Flow

```
client: channel.uploadVideo("Design Patterns in Java")
             │
             ├─ println("New video uploaded: ...")       // state change
             └─ notifySubscribers("Design Patterns...")  // fan-out
                      │
                      ├─ Alice.update(msg)  → "Alice received notification: ..."
                      └─ Bob.update(msg)    → "Bob received notification: ..."
```

---

## Roles → files

| Role              | File                                                   | Responsibility                       |
| ----------------- | ------------------------------------------------------ | ------------------------------------ |
| Subject (abstract)| [Interfaces/Ichannel.java](Interfaces/Ichannel.java)   | register / remove / notify contract  |
| Observer (abstract)| [Interfaces/Isubscriber.java](Interfaces/Isubscriber.java) | the `update()` callback         |
| Concrete Subject  | [ConcreteClasses/Channel.java](ConcreteClasses/Channel.java) | holds observer list + state    |
| Concrete Observer | [ConcreteClasses/Subscriber.java](ConcreteClasses/Subscriber.java) | reacts to notification   |
| Client            | [Observer.java](Observer.java)                         | wires it up, triggers the event      |

---

## Use it when

- One state change must fan out to **many** reactions, and the list is known only at runtime.
- You want to add/remove reactions **without touching** the class that produces the event.
- The producer must not depend on the consumers (compile-time decoupling).

## Don't use it when

- There is exactly one, always-present dependent → just call the method directly.
- You need a guaranteed notification **order** — the pattern makes no such promise.
- The callback chain can re-trigger the subject → cascading/cyclic updates, painful to debug.

---

## Gotchas (the ones that actually bite)

1. **`unsubscribe` must remove, not just log.** Forget `list.remove(o)` and the observer
   keeps getting notified forever. Silent bug — output looks almost right.
2. **Never let the observer hold the concrete subject.** `Subscriber` having a
   `private Channel channel` field inverts the dependency and kills the reuse.
3. **Lapsed listener = memory leak.** The subject's list keeps observers alive. Long-lived
   subject + short-lived observers → unsubscribe in teardown, or hold weak references.
4. **Push vs pull.** Here we *push* the payload (`update(String message)`). Alternative is
   *pull*: `update(subject)` and the observer queries what it needs. Push is simpler;
   pull avoids sending data nobody reads.
5. **Don't use `java.util.Observer` / `Observable`** — deprecated since Java 9
   (no type safety, no ordering, `Observable` forces inheritance). Roll your own, as here.

---

## Run

```bash
javac -d out Observer.java && java -cp out Observer
```

`javac` resolves `Channel` / `Subscriber` from the default source path, so only the client
needs to be named. `java Observer.java` alone **cannot** work on JDK 17 — single-file source
mode compiles exactly one file and the launcher has no `--source-path`. (Multi-file source
mode arrived in JDK 22.)

---

## Real-world sightings

Swing/Android `addXxxListener`, `EventEmitter` in Node, RxJS `Observable.subscribe`,
React `useEffect` dependency reactions, Kafka consumer groups, DOM `addEventListener`.
