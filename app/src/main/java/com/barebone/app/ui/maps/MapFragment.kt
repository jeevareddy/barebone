package com.barebone.app.ui.maps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barebone.app.R
import com.barebone.app.datamodel.PlaceResultModel
import com.barebone.app.services.PlacesAPI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.gson.internal.LinkedTreeMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MapFragment : Fragment() {
    lateinit var mapsViewModel: MapsViewModel
    lateinit var lat: TextView
    lateinit var long: TextView
    lateinit var searchFiels: MaterialAutoCompleteTextView
    lateinit var recyclerView: RecyclerView
    lateinit var gMap: GoogleMap


    val placesAPIservice by lazy { PlacesAPI.create() }
    var disposable: Disposable? = null


    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap
        gMap.uiSettings.isMapToolbarEnabled = false
        gMap.setPadding(0, 200, 0, 0)
        var latLng = LatLng(24.7464371, 55.0002214)
        setMapView(latLng, "CBD")
        googleMap.setOnMapClickListener { it ->
            latLng = it
            setMapView(it, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapsViewModel =
            ViewModelProviders.of(this).get(MapsViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        lat = view.findViewById(R.id.lat)
        long = view.findViewById(R.id.log)
        searchFiels = view.findViewById(R.id.searchField)
        recyclerView = view.findViewById(R.id.searchResults)


        mapsViewModel.results.observe(this.viewLifecycleOwner, {


            if (it?.isEmpty()!!) {
                recyclerView.visibility = View.GONE

            } else {
                recyclerView.visibility = View.VISIBLE
                val adapter = PlaceResultAdapter(this)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(view.context)
            }
        })

        searchFiels.doAfterTextChanged {
            search(searchFiels.text.toString())
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


    }

    private fun search(srs: String) {
        Log.d("API RESULT", "begin fetch")

        disposable =
            placesAPIservice.search(srs, resources.getString(R.string.places_api_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: PlaceResultModel.Results? ->
                        run {
                            Log.d(
                                "API RESULT",
                                "Result Length: " + result?.results?.size.toString()
                            )
                            result?.results?.let {
                                if (it.isEmpty()) {
                                    recyclerView.visibility = View.GONE

                                } else {
                                    recyclerView.visibility = View.VISIBLE
                                    val adapter = PlaceResultAdapter(this)

                                    recyclerView.adapter = adapter
                                    recyclerView.layoutManager =
                                        LinearLayoutManager(this.requireContext())
                                    mapsViewModel.setResults(it)
                                }
                            }
                        }
                    },
                    { error -> Log.d("API ERROR", error.message) }
                )
    }

    fun setMapView(latLng: LatLng, title: String) {
        gMap.clear()
        gMap.addMarker(MarkerOptions().position(latLng).title(title))
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        gMap.animateCamera(CameraUpdateFactory.zoomTo(10F))

        lat.text = "Lat:" + latLng.latitude
        long.text = "Long:" + latLng.longitude
    }
}

class PlaceResultAdapter(private val appContext: MapFragment) :
    RecyclerView.Adapter<PlaceResultAdapter.ViewHolder>() {
    class ViewHolder(listView: View) : RecyclerView.ViewHolder(listView) {
        val mapsName = itemView.findViewById<TextView>(R.id.maps_name)
        val mapsAddress = itemView.findViewById<TextView>(R.id.maps_address)
        val card = itemView.findViewById<LinearLayout>(R.id.search_result_card)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.maps_result_card, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val ele: HashMap<Any, Any> =
            appContext.mapsViewModel.results.value?.get(position) ?: hashMapOf()
        // Set item views based on your views and data model
        val name = viewHolder.mapsName
        name.text = ele.get("name").toString()
        val address = viewHolder.mapsAddress
        address.text = ele.get("formatted_address").toString()
        val geometry = ele.get("geometry") as LinkedTreeMap<String, Any>
        val location = geometry.get("location") as LinkedTreeMap<String, Double>
        val ltlg: LatLng = LatLng(
            location["lat"]!!,
            location["lng"]!!
        )
        val card = viewHolder.card
        card.setOnClickListener {
            appContext.recyclerView.visibility = View.GONE
            appContext.setMapView(ltlg, name.text.toString())
        }


    }


    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return appContext.mapsViewModel.resultList.value?.size ?: 0
    }
}
