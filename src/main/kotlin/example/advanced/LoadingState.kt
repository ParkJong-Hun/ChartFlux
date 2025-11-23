package example.advanced

/**
 * Example of using sealed classes with StateProperty
 *
 * This represents a typical loading/success/error state pattern
 */
sealed class LoadingState<out T> {
    data object Loading : LoadingState<Nothing>()
    data class Success<T>(val data: T) : LoadingState<T>()
    data class Error(val message: String) : LoadingState<Nothing>()
}
