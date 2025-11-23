package parkjonghun.github.io.chartflux.updater

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import parkjonghun.github.io.chartflux.action.Action
import parkjonghun.github.io.chartflux.state.staterecord.StateRecord

internal open class Updater<SR : StateRecord, A : Action>(
    private val mutableStateRecord: MutableStateFlow<SR>,
    actions: SharedFlow<A>,
    coroutineScope: CoroutineScope,
) {
    private val mutex = Mutex()

    private val job: Job = actions
        .onEach(::updateStateRecord)
        .launchIn(coroutineScope)

    private suspend fun updateStateRecord(action: A) =
        mutex.withLock {
            val currentStateRecord = mutableStateRecord.value
            val newState = computeNextState(currentStateRecord, action)
            mutableStateRecord.value = newState
        }

    @Suppress("UNCHECKED_CAST")
    protected open fun computeNextState(currentStateRecord: SR, action: A): SR =
        currentStateRecord.updateWithFindPropertySpecs(action) as SR

    fun cancel() = job.cancel()
}
