package pozzo.apps.travelweather.map.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.map.viewmodel.PreferencesViewModel

/**
 * Our main menu.
 */
class SideMenuFragment : Fragment() {
    private lateinit var rgDaySelection: RadioGroup
    private lateinit var viewModel: PreferencesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity).get(PreferencesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.fragment_side_menu, container, false)
        rgDaySelection = contentView.findViewById(R.id.rgDaySelection)
        setupView(contentView)
        return contentView
    }

    /**
     * Fill view components with data.
     */
    private fun setupView(contentView: View) {
        viewModel.selectedDay.observe(this, Observer { selectedDate ->
            val selectedRadio = contentView.findViewById<RadioButton>(selectedDate!!.resourceId)
            selectedRadio.isChecked = true
        })

        rgDaySelection.setOnCheckedChangeListener({ _, checkedId -> viewModel.setSelectedDay(checkedId) })
    }
}
