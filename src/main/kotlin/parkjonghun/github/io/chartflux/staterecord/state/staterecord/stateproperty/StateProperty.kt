package parkjonghun.github.io.chartflux.staterecord.state.staterecord.stateproperty

import parkjonghun.github.io.chartflux.staterecord.action.Action
import parkjonghun.github.io.chartflux.staterecord.state.staterecord.StateRecord

abstract class StateProperty<
        SR : StateRecord,
        A : Action,
        T,
        SELF : StateProperty<SR, A, T, SELF>,
        > : StatePropertySpec<SR, A, T, SELF> {
    abstract val value: T
}
