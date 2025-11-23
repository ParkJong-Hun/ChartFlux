# ChartFlux

A Kotlin state management pattern where each state property manages its own update logic.

## Core Idea

Instead of one big reducer updating all state fields, each property knows how to update itself.

### StateProperty

A property that knows how to update itself:

```kotlin
data class CountProperty(
    override val value: Int
) : StateProperty<CounterState, CounterAction, Int, CountProperty>() {

    override fun spec(context: UpdateContext<CounterState, CounterAction>, action: CounterAction): CountProperty {
        return when (action) {
            is Increment -> CountProperty(value + 1)
            is Decrement -> CountProperty(value - 1)
            else -> this
        }
    }
}
```

### StateRecord

A container that groups properties together:

```kotlin
data class CounterState(
    val count: CountProperty,
    val message: MessageProperty
) : StateRecord
```

When an action is dispatched, each property's `spec()` is called automatically.

## Why?

**Traditional Flux:**

```kotlin
// One big reducer for everything
fun reducer(stateRecord: AppState, action: Action): AppState {
    return when (action) {
        is Increment -> state.copy(
            count = state.count + 1,
            message = "Count is ${state.count + 1}"  // Easy to forget!
        )
        // ... hundreds of cases
    }
}
```

**ChartFlux:**

```kotlin
// Each property manages itself
data class CountProperty(...) {
    override fun spec(context: UpdateContext<CounterState, CounterAction>, action: CounterAction) = when (action) {
        is Increment -> CountProperty(value + 1)
        else -> this
    }
}

data class MessageProperty(...) {
    override fun spec(context: UpdateContext<CounterState, CounterAction>, action: CounterAction) = when (action) {
        is Increment -> MessageProperty("Count is ${context.compute(CounterState::count) + 1}")
        else -> this
    }
}
```

## Key Benefits

1. **Distributed Logic**: Update logic lives with the property, not in a central reducer
2. **Less Boilerplate**: No need to copy entire state objects
3. **Lazy Evaluation**: Properties can access other properties' updated values via `context.compute()`
4. **Dependency Management**: Automatic detection of circular dependencies
5. **Type-Safe**: Compile-time safety for each property

## Advanced Features

### UpdateContext

Access other properties' computed values during updates:

```kotlin
data class MessageProperty(
    override val value: String
) : StateProperty<CounterState, CounterAction, String, MessageProperty>() {

    override fun spec(context: UpdateContext<CounterState, CounterAction>, action: CounterAction): MessageProperty {
        // Lazily compute the updated count value
        val newCount = context.compute(CounterState::count)

        return when (action) {
            is Increment -> MessageProperty("Incremented to $newCount")
            else -> this
        }
    }
}
```

### SideEffect

Handle async operations after actions (API calls, logging, etc.):

```kotlin
class AutoSaveEffect : SideEffect<CartState, CartAction> {
    override suspend operator fun invoke(
        stateRecord: CartState,
        action: CartAction,
        dispatch: (CartAction) -> Unit
    ) {
        when (action) {
            is CartAction.AddItem -> {
                val action = runCatching {
                    api.saveCart(stateRecord)
                }.onSucecss {
                    SaveCompleted
                }.onFailure { e ->
                    SaveFailed(e)
                }

                dispatch(action)
            }
        }
    }
}

val store = ChartStore(
    initialStateRecord = initialState,
    sideEffects = listOf(AutoSaveEffect())
)
```

## Examples

See working examples in the source code:

- **Basic Example**: [`example/CounterExample.kt`](src/main/kotlin/example/CounterExample.kt)
    - Simple counter with ChartStore
    - Demonstrates basic StateProperty usage

- **Advanced Examples**:
    - [`example/advanced/UserExample.kt`](src/main/kotlin/example/advanced/UserExample.kt) - Sealed classes and nullable types
    - [`example/advanced/MutualDependencyExample.kt`](src/main/kotlin/example/advanced/MutualDependencyExample.kt) - Property dependencies via UpdateContext
    - [`example/advanced/CircularDependencyExample.kt`](src/main/kotlin/example/advanced/CircularDependencyExample.kt) - Circular dependency detection

Run examples:

```bash
./gradlew run
```

## That's It

The core pattern is simple: properties update themselves instead of being updated by a central reducer.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
