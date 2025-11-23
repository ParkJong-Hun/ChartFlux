# ChartFlux

A Kotlin state management pattern where each state property manages its own update logic.

## Core Idea

Instead of one big reducer updating all state fields, each property knows how to update itself.

### StateProperty

A property that knows how to update itself:

```kotlin
data class CountProperty(
    override val value: Int
) : StateProperty<AppState, AppAction, Int, CountProperty>() {

    override fun spec(stateRecord: AppState, action: AppAction): CountProperty {
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
data class AppState(
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
    override fun spec(stateRecord: AppState, action: AppAction) = when (action) {
        is Increment -> CountProperty(value + 1)
        else -> this
    }
}

data class MessageProperty(...) {
    override fun spec(stateRecord: AppState, action: AppAction) = when (action) {
        is Increment -> MessageProperty("Count is ${state.count.value + 1}")
        else -> this
    }
}
```

## Key Benefits

1. **Distributed Logic**: Update logic lives with the property, not in a central reducer
2. **Less Boilerplate**: No need to copy entire state objects
3. **Automatic Updates**: Reflection-based orchestration calls each property's `spec()`
4. **Type-Safe**: Compile-time safety for each property

## Examples

See working examples in the source code:

- **Basic Example**: [`example/CounterExample.kt`](src/main/kotlin/example/CounterExample.kt)
  - Simple counter with automatic state updates
  - Demonstrates basic StateProperty usage

- **Advanced Example**: [`example/advanced/UserExample.kt`](src/main/kotlin/example/advanced/UserExample.kt)
  - Using sealed classes with StateProperty
  - Using nullable types
  - Pattern matching on state values

Run the basic example:
```bash
./gradlew run
```

## That's It

The core pattern is simple: properties update themselves instead of being updated by a central reducer.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
