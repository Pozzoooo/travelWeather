package pozzo.apps.travelweather.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import pozzo.apps.travelweather.R;

/**
 * Our main menu.
 *
 * Created by sarge on 10/29/15.
 */
public class SideMenuFragment extends Fragment {
	private RadioGroup rgDaySelection;
	private int[] days = new int[]{ R.id.rToday, R.id.rTomorow, R.id.rAfterTomorow };

	@Nullable
	@Override
	public View onCreateView(
			LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_side_menu, container, false);

		rgDaySelection = (RadioGroup) contentView.findViewById(R.id.rgDaySelection);

		fillView(contentView);

		return contentView;
	}

	/**
	 * Fill view components with data.
	 */
	private void fillView(View contentView) {
		SharedPreferences preferences =
				PreferenceManager.getDefaultSharedPreferences(getActivity());
		int selectedDay = preferences.getInt("selectedDay", 0);
		RadioButton selectedRadio = (RadioButton) contentView.findViewById(days[selectedDay]);
		selectedRadio.setChecked(true);

		rgDaySelection.setOnCheckedChangeListener(onDaySelection);
	}

	/**
	 * When user changes the day selection.
	 */
	private RadioGroup.OnCheckedChangeListener onDaySelection =
			new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			saveSelection();
		}
	};

	/**
	 * Save new configuration.
	 */
	private void saveSelection() {
		SharedPreferences.Editor preferences =
				PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

		int selectedId = rgDaySelection.getCheckedRadioButtonId();
		int selectedDay = -1;
		for(int i=0; i<days.length; ++i)
			if(days[i] == selectedId)
				selectedDay = i;
		if(selectedDay >= 0)
			preferences.putInt("selectedDay", selectedDay).apply();
	}
}
