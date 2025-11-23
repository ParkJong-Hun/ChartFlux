package parkjonghun.github.io.chartflux.state.staterecord.stateproperty

import parkjonghun.github.io.chartflux.action.Action
import parkjonghun.github.io.chartflux.state.staterecord.StateRecord

abstract class StateProperty<
        SR : StateRecord,
        A : Action,
        T,
        SELF : StateProperty<SR, A, T, SELF>,
        > : StatePropertySpec<SR, A, T, SELF> {
    abstract val value: T
}
