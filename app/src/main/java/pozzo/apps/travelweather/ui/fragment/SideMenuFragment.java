package pozzo.apps.travelweather.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pozzo.apps.travelweather.R;

/**
 * Sera nosso menu lateral =D.
 *
 * Created by sarge on 10/29/15.
 */
public class SideMenuFragment extends Fragment {

	@Nullable
	@Override
	public View onCreateView(
			LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_side_menu, container, false);
		return contentView;
	}
}
