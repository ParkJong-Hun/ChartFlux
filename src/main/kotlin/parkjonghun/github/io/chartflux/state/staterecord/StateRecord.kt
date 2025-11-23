package parkjonghun.github.io.chartflux.state.staterecord

import parkjonghun.github.io.chartflux.action.Action
import parkjonghun.github.io.chartflux.state.State
import parkjonghun.github.io.chartflux.state.staterecord.stateproperty.StateProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

interface StateRecord : State {
    fun <A : Action> updateWithFindPropertySpecs(action: A): StateRecord {
        val kClass = this::class
        val constructor = kClass.primaryConstructor
            ?: error("StateRecord ${kClass.simpleName} must have a primary constructor")

        val updatedParams = constructor.parameters.associateWith { param ->
            val property = kClass.memberProperties.find { it.name == param.name }
                ?: error("Property ${param.name} not found in ${kClass.simpleName}")

            val currentValue = property.getter.call(this)

            if (currentValue is StateProperty<*, *, *, *>) {
                @Suppress("UNCHECKED_CAST")
                val stateProperty = currentValue as StateProperty<StateRecord, A, *, *>
                stateProperty.spec(this, action)
            } else {
                currentValue
            }
        }

        return constructor.callBy(updatedParams)
    }
}
