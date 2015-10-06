package pozzo.apps.travelweather.network;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Apenas o tempo atual
 * http://www.webservicex.net/WS/WSDetails.aspx?CATID=12&WSID=56
 *
 * Br apenas
 * http://servicos.cptec.inpe.br/XML/
 *
 * Yahoo, mas tem limitacao de 2000 por dia
 * https://query.yahooapis.com/v1/public/yql?
 * 		q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20
 * 		(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)
 * 		&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys
 *
 * Created by soldier on 10/5/15.
 */
public interface YahooWeather {
	@GET("/v1/public/yql?format=json&env=store://datatables.org/alltableswithkeys")
	Response forecast(@Query("q") String q);
}
