package pozzo.apps.travelweather.map.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import pozzo.apps.travelweather.R
import pozzo.apps.travelweather.databinding.FragmentSideMenuBinding
import pozzo.apps.travelweather.map.viewmodel.MapViewModel
import pozzo.apps.travelweather.map.viewmodel.PreferencesViewModel

/**
 * Our main menu.
 */
class SideMenuFragment : Fragment() {
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var viewModel: MapViewModel

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    preferencesViewModel = ViewModelProviders.of(activity!!).get(PreferencesViewModel::class.java)
    viewModel = ViewModelProviders.of(activity!!).get(MapViewModel::class.java)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val contentView = DataBindingUtil.inflate<FragmentSideMenuBinding>(inflater, R.layout.fragment_side_menu, container, false)
    contentView.viewModel = viewModel
    setupView(contentView.root)
    return contentView.root
  }

  /**
   * Fill view components with data.
   */
  private fun setupView(contentView: View) {
    preferencesViewModel.selectedDay.observe(this, Observer { selectedDate ->
      val selectedRadio = contentView.findViewById<RadioButton>(selectedDate!!.resourceId)
      selectedRadio.isChecked = true
    })

    contentView.findViewById<RadioGroup>(R.id.rgDaySelection)
        .setOnCheckedChangeListener({ _, checkedId -> preferencesViewModel.setSelectedDay(checkedId) })
  }
}
