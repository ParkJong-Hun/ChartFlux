package example.advanced

import parkjonghun.github.io.chartflux.staterecord.state.staterecord.StateRecord

/**
 * Example: StateRecord with complex property types
 *
 * This demonstrates:
 * - Using sealed classes as property values
 * - Using nullable types as property values
 * - Multiple properties working together
 */
data class UserState(
    val userData: UserDataProperty,
    val lastError: LastErrorProperty
) : StateRecord
