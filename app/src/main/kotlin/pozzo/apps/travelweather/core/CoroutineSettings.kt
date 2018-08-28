package pozzo.apps.travelweather.core

import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Unconfined

/*
todo I need to think about it
- Is it ok for this hidden dependency?
- Does it make sense to initialize as Unconfined and force it to be updated on the app?
- - Seems a bit missleading....
- Remember all the others that are following this same patter (Bug, BitmapFactory)
- Also I need a solution for the test, how do I make it transparent to cover the test cases?
 */
object CoroutineSettings {
    var background: CoroutineDispatcher = Unconfined
    var ui: CoroutineDispatcher = Unconfined
}
