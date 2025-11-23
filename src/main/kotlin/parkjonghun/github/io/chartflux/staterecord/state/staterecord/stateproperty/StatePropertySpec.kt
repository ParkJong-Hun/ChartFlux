package parkjonghun.github.io.chartflux.staterecord.state.staterecord.stateproperty

import parkjonghun.github.io.chartflux.staterecord.action.Action
import parkjonghun.github.io.chartflux.staterecord.state.staterecord.StateRecord

interface StatePropertySpec<
        SR : StateRecord,
        A : Action,
        T,
        SP : StateProperty<SR, A, T, SP>,
        > {
    fun initial(): SP
    fun spec(stateRecord: SR, action: A): SP
}
