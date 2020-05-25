package com.pw.spacermaniak.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.MapSettings
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapRoute
import com.pw.spacermaniak.R
import java.io.File
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.RoutingError;
import com.pw.spacermaniak.MainActivity
import com.pw.spacermaniak.utilities.Toaster


class MapFragment : Fragment() {
    private var map: Map? = null
    private var mapFragment: AndroidXMapFragment? = null
    private var routeButton: Button? = null
    private var mapRoute: MapRoute? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.map_fragment, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        if (map == null)
            initMap()
        if (routeButton == null)
            initCreateRouteButton()
        }

    private fun initCreateRouteButton() {
        routeButton = activity?.findViewById(R.id.start_button) as Button
        routeButton!!.setOnClickListener(View.OnClickListener {
            /*
                     * Clear map if previous results are still on map,otherwise proceed to creating
                     * route
                     */
            if (map != null && mapRoute != null) {
                map!!.removeMapObject(mapRoute!!)
                mapRoute = null
            } else { /*
                         * The route calculation requires local map data.Unless there is pre-downloaded
                         * map data on device by utilizing MapLoader APIs, it's not recommended to
                         * trigger the route calculation immediately after the MapEngine is
                         * initialized.The INSUFFICIENT_MAP_DATA error code may be returned by
                         * CoreRouter in this case.
                         *
                         */
                createRoute()
            }
        })
    }
    private fun initMap(){
        mapFragment = childFragmentManager.findFragmentById(R.id.mapfragment) as AndroidXMapFragment?
        // Set up disk cache path for the map service for this application
// It is recommended to use a path under your application folder for storing the disk cache
        val a = activity as MainActivity
        if (!a.configInitialized){
            a.configInitialized =
                MapSettings.setIsolatedDiskCacheRootPath(
                    context?.getExternalFilesDir(null).toString() + File.separator.toString() + ".here-maps"
                )
        }

        if (!a.configInitialized) {
            Toaster.toast(context!!, "Unable to set isolated disk cache path.")
        } else {
            mapFragment!!.init { error ->
                if (error == OnEngineInitListener.Error.NONE) { // retrieve a reference of the map from the map fragment
                    map = mapFragment!!.map
                    // Set the map center to the Vancouver region (no animation)
                    map!!.setCenter(
                        GeoCoordinate(52.2202, 21.0107, 0.0),
                        Map.Animation.NONE
                    )
                    // Set the zoom level to the average between min and max
                    map!!.zoomLevel = (map!!.maxZoomLevel + map!!.minZoomLevel) / 2
                } else {
                    println("ERROR: Cannot initialize Map Fragment")
                }
            }
        }
    }

    private fun createRoute() { /* Initialize a CoreRouter */
        val coreRouter = CoreRouter()
        /* Initialize a RoutePlan */
        val routePlan = RoutePlan()
        /*
         * Initialize a RouteOption. HERE Mobile SDK allow users to define their own parameters for the
         * route calculation,including transport modes,route types and route restrictions etc.Please
         * refer to API doc for full list of APIs
         */
        val routeOptions = RouteOptions()
        /* Other transport modes are also available e.g Pedestrian */routeOptions.transportMode =
            RouteOptions.TransportMode.PEDESTRIAN
        /* Disable highway in this route. */routeOptions.setHighwaysAllowed(false)
        /* Calculate the shortest route available. */routeOptions.routeType =
            RouteOptions.Type.SHORTEST
        /* Calculate 1 route. */routeOptions.routeCount = 1
        /* Finally set the route option */routePlan.routeOptions = routeOptions
        /* Define waypoints for the route */ /* START: 4350 Still Creek Dr */
        val startPoint =
            RouteWaypoint(GeoCoordinate(52.2202, 21.0107))
        /* END: Langley BC */
        val destination =
            RouteWaypoint(GeoCoordinate(52.4373, 21.0232))
        /* Add both waypoints to the route plan */routePlan.addWaypoint(startPoint)
        routePlan.addWaypoint(destination)
        /* Trigger the route calculation,results will be called back via the listener */coreRouter.calculateRoute(
            routePlan,
            object: CoreRouter.Listener {
                override fun onProgress(i: Int) { /* The calculation progress can be retrieved in this callback. */
                }

                override fun onCalculateRouteFinished(
                    routeResults: List<RouteResult>,
                    routingError: RoutingError
                ) { /* Calculation is done. Let's handle the result */
                    if (routingError == RoutingError.NONE) {
                        if (routeResults[0].route != null) { /* Create a MapRoute so that it can be placed on the map */
                            mapRoute = MapRoute(routeResults[0].route)
                            /* Show the maneuver number on top of the route */mapRoute?.setManeuverNumberVisible(
                                true
                            )
                            /* Add the MapRoute to the map */map?.addMapObject(mapRoute!!)
                            /*
                                 * We may also want to make sure the map view is orientated properly
                                 * so the entire route can be easily seen.
                                 */
                            val gbb = routeResults[0].route
                                .boundingBox
                            if (gbb != null) {
                                map?.zoomTo(
                                    gbb, Map.Animation.NONE,
                                    Map.MOVE_PRESERVE_ORIENTATION
                                )
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "Error:route results returned is not valid",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            activity,
                            "Error:route calculation returned error code: $routingError",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
    }
}