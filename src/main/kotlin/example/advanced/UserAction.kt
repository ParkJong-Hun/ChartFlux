package example.advanced

import parkjonghun.github.io.chartflux.staterecord.action.Action

/**
 * Actions for user data fetching example
 */
sealed class UserAction : Action {
    data object LoadUser : UserAction()
    data class UserLoaded(val name: String) : UserAction()
    data class UserLoadFailed(val error: String) : UserAction()
}
