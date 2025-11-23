package parkjonghun.github.io.chartflux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import parkjonghun.github.io.chartflux.staterecord.action.Action
import parkjonghun.github.io.chartflux.staterecord.state.staterecord.StateRecord
import parkjonghun.github.io.chartflux.staterecord.updater.Updater

class ChartStore<STATE_RECORD : StateRecord, ACTION : Action>(
    initialStateRecord: STATE_RECORD,
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : UDF<STATE_RECORD, ACTION> {
    private val mutableStateRecord: MutableStateFlow<STATE_RECORD> = MutableStateFlow(initialStateRecord)
    override val stateRecord: StateFlow<STATE_RECORD> = mutableStateRecord.asStateFlow()

    private val actions = MutableSharedFlow<ACTION>(
        replay = 0,
        extraBufferCapacity = 64,
    )

    private val updater: Updater<STATE_RECORD, ACTION> = Updater(
        mutableStateRecord = mutableStateRecord,
        actions = actions,
        coroutineScope = coroutineScope,
    )

    override fun dispatch(action: ACTION) {
        actions.tryEmit(action)
    }

    override fun close() = updater.cancel()
}
