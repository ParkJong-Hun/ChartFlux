package parkjonghun.github.io.chartflux.state.staterecord.stateproperty

import parkjonghun.github.io.chartflux.action.Action
import parkjonghun.github.io.chartflux.state.staterecord.StateRecord
import parkjonghun.github.io.chartflux.state.staterecord.UpdateContext

interface StatePropertySpec<
        SR : StateRecord,
        A : Action,
        T,
        SP : StateProperty<SR, A, T, SP>,
        > {
    fun initial(): SP
    fun spec(context: UpdateContext<SR, A>, action: A): SP
}
