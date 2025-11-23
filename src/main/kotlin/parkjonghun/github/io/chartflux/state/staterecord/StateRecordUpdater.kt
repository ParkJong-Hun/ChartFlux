package parkjonghun.github.io.chartflux.state.staterecord

import parkjonghun.github.io.chartflux.action.Action
import parkjonghun.github.io.chartflux.state.staterecord.stateproperty.StateProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

internal fun <SR : StateRecord, A : Action> updateStateRecord(
    stateRecord: SR,
    action: A,
): StateRecord {
    val kClass = stateRecord::class
    val constructor = kClass.primaryConstructor
        ?: error("StateRecord ${kClass.simpleName} must have a primary constructor")

    val context = object : UpdateContext<SR, A> {
        override val current: SR = stateRecord
        override val action: A = action
        private val computed = mutableMapOf<String, Any?>()
        private val computationStack = mutableListOf<String>()

        @Suppress("UNCHECKED_CAST")
        override fun <T> compute(property: KProperty1<SR, StateProperty<SR, A, T, *>>): T {
            val propertyName = property.name

            if (propertyName in computationStack) throw CircularDependencyException(computationStack + propertyName)
            if (propertyName in computed) return computed[propertyName] as T

            computationStack.add(propertyName)

            try {
                val currentProperty = property.get(current)

                val newProperty = currentProperty.spec(this, action)
                val newValue = newProperty.value

                computed[propertyName] = newValue

                return newValue
            } finally {
                computationStack.removeAt(computationStack.lastIndex)
            }
        }
    }

    val updatedParams = constructor.parameters.associateWith { param ->
        val property = kClass.memberProperties.find { it.name == param.name }
            ?: error("Property ${param.name} not found in ${kClass.simpleName}")

        val currentValue = property.getter.call(stateRecord)

        if (currentValue is StateProperty<*, *, *, *>) {
            @Suppress("UNCHECKED_CAST")
            val stateProperty = currentValue as StateProperty<SR, A, *, *>
            stateProperty.spec(context, action)
        } else {
            currentValue
        }
    }

    return constructor.callBy(updatedParams)
}
