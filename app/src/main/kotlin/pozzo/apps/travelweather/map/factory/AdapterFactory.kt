package pozzo.apps.travelweather.map.factory

import android.content.Context
import android.widget.ArrayAdapter

class AdapterFactory {

    //Items should not be nullable once this is fixed: https://github.com/nhaarman/mockito-kotlin/issues/310
    fun <T> createArrayAdapter(context: Context, items: Array<T>?): ArrayAdapter<T> {
        return ArrayAdapter(context, android.R.layout.simple_list_item_1, items!!)
    }
}
