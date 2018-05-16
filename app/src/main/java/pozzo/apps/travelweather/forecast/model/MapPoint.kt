package pozzo.apps.travelweather.forecast.model

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng

/*
 * todo e agora vem a parada mais complexa
 *
 * Ainda nao tenho certeza qual approach utilizar
 * - Apenas mudar o icone para um composto?
 * - - O problema eh q terei dois tipos de interacao, um para a bandeira e outro para a previsao...
 * - Apenas mudar o icone para o bandeira e foda-se a previsao?
 * - - Provavelmente inviavel
 * - Utilizar heranca e criar algo mais complexo nos filhos?
 * - - Parece interessante
 * - Adicionar um campo tipo?
 * - - Nao parece muito ideal, jah q teriamos alguns campos inuteis...
 */
open class MapPoint(open val icon: BitmapDescriptor?,
                    open val title: String?,
                    open val position: LatLng,
                    open val onClickLoadUrl: String?)
