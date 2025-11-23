package parkjonghun.github.io.chartflux

import kotlinx.coroutines.flow.StateFlow
import parkjonghun.github.io.chartflux.staterecord.action.Action
import parkjonghun.github.io.chartflux.staterecord.state.State
import java.io.Closeable

interface UDF<S : State, A : Action> : Closeable {
    val stateRecord: StateFlow<S>
    fun dispatch(action: A)
}