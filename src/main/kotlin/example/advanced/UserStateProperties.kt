package example.advanced

import parkjonghun.github.io.chartflux.state.staterecord.stateproperty.StateProperty

/**
 * Example: StateProperty with sealed class value type
 *
 * This demonstrates how to use complex types like sealed classes
 * with StateProperty for type-safe state representation
 */
data class UserDataProperty(
    override val value: LoadingState<String>
) : StateProperty<UserState, UserAction, LoadingState<String>, UserDataProperty>() {

    override fun initial(): UserDataProperty {
        return UserDataProperty(LoadingState.Loading)
    }

    override fun spec(stateRecord: UserState, action: UserAction): UserDataProperty {
        return when (action) {
            is UserAction.LoadUser ->
                UserDataProperty(LoadingState.Loading)
            is UserAction.UserLoaded ->
                UserDataProperty(LoadingState.Success(action.name))
            is UserAction.UserLoadFailed ->
                UserDataProperty(LoadingState.Error(action.error))
        }
    }
}

/**
 * Example: StateProperty with nullable value type
 *
 * Demonstrates using nullable types with StateProperty
 */
data class LastErrorProperty(
    override val value: String?
) : StateProperty<UserState, UserAction, String?, LastErrorProperty>() {

    override fun initial(): LastErrorProperty {
        return LastErrorProperty(null)
    }

    override fun spec(stateRecord: UserState, action: UserAction): LastErrorProperty {
        return when (action) {
            is UserAction.UserLoadFailed ->
                LastErrorProperty(action.error)
            is UserAction.LoadUser, is UserAction.UserLoaded ->
                LastErrorProperty(null)
        }
    }
}
