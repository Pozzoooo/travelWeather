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
 * Sera nosso menu lateral =D.
 *
 * Created by sarge on 10/29/15.
 */
public class SideMenuFragment extends Fragment {
	//TODO eh mesmo uma boa ideia salvar o id? Eu acho que nao...
	private RadioGroup rgDaySelection;

	@Nullable
	@Override
	public View onCreateView(
			LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_side_menu, container, false);

		rgDaySelection = (RadioGroup) contentView.findViewById(R.id.rgDaySelection);

		fillView(contentView);

		return contentView;
	}

	private void fillView(View contentView) {
		SharedPreferences preferences =
				PreferenceManager.getDefaultSharedPreferences(getActivity());
		int selectedDay = preferences.getInt("selectedDay", R.id.rToday);
		RadioButton selectedRadio = (RadioButton) contentView.findViewById(selectedDay);
		selectedRadio.setChecked(true);

		rgDaySelection.setOnCheckedChangeListener(onDaySelection);
	}

	private RadioGroup.OnCheckedChangeListener onDaySelection =
			new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			saveSelection();
		}
	};

	private void saveSelection() {
		SharedPreferences.Editor preferences =
				PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

		int selectedId = rgDaySelection.getCheckedRadioButtonId();
		if(selectedId > 0)
			preferences.putInt("selectedDay", selectedId).apply();
	}
}
