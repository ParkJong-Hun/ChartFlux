package parkjonghun.github.io.chartflux.sideeffect

import parkjonghun.github.io.chartflux.action.Action
import parkjonghun.github.io.chartflux.state.staterecord.StateRecord

interface SideEffect<SR : StateRecord, A : Action> {
    suspend operator fun invoke(
        stateRecord: SR,
        action: A,
        dispatch: (A) -> Unit
    )
}
