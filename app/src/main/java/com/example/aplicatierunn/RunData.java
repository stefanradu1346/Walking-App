package com.example.aplicatierunn;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RunData {
    private List<LatLng> route;
    private List<Long> times;

    private Long grade;

    public RunData(List<LatLng> route, List<Long> times) {
        this.route = route;
        this.times = times;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public List<Long> getTimes() {
        return times;
    }
}
