package pozzo.apps.travelweather.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by sarge on 10/25/15.
 */
public class AndroidUtil {

    public static void openUrl(String url, Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }
}
