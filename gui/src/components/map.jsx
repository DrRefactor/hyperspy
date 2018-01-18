import React, { Component } from 'react'
import * as leaflet from 'leaflet'

export default class Map extends Component {
  static defaultProps = {
    mode: 'streets',
    autoFit: true,
    ctrlWheelZoom: true
  }

  constructor(props) {
    super(props)
    this.onRefChange = this.onRefChange.bind(this)
    this.onClick = this.onClick.bind(this)
    this.onMarkerClick = this.onMarkerClick.bind(this)
    this.adjustWeelZoom = this.adjustWeelZoom.bind(this)
    this.onLineClick = this.onLineClick.bind(this)
    this.onCircleClick = this.onCircleClick.bind(this)
    this.setMarkers = this.setMarkers.bind(this)

    this.markers = {}
    this.lines = {}
    this.circles = {}
  }

  componentDidUpdate(prevProps) {
    if (!this.map)
      return
    if (prevProps.mode != this.props.mode)
      this.setMapMode(this.props.mode)
    this.setMarkers(this.props.connections, this.props.locations, this.props.stations)
    if (this.props.autoFit)
      this.fitBounds()
    this.adjustWeelZoom()
  }

  render() {
    return (
      <div className='map'>
        <div className='map-box' ref={this.onRefChange} />
      </div>
    )
  }

  onRefChange(el) {
    if (!el) {
      this.setMarkers()
      this.setMapMode(null)
      this.map.removeEventListener('click', this.onClick)
      this.map = null

      document.removeEventListener('keydown', this.adjustWeelZoom)
      document.removeEventListener('keyup', this.adjustWeelZoom)
      return
    }

    this.map = leaflet.map(el)
    this.map.setZoom(13)
    this.map.addEventListener('click', this.onClick)
    this.setMapMode(this.props.mode)
    this.setMarkers(this.props.connections, this.props.locations, this.props.stations)
    this.fitBounds()

    document.addEventListener('keydown', this.adjustWeelZoom)
    document.addEventListener('keyup', this.adjustWeelZoom)
    this.adjustWeelZoom()
  }

  onClick(e) {
    if (this.props.onClick)
      this.props.onClick({ type: 'click', longitude: e.latlng.lng, latitude: e.latlng.lat })
  }

  onMarkerClick(e) {
    let capsuleId = Object.keys(this.markers).find(x => this.markers[x] === e.target)
    let child = (this.props.locations || []).find(x => x.capsuleId == capsuleId)
    if (child && this.props.onLocationClick)
      this.props.onLocationClick(child)
  }

  onLineClick(e) {
    let id = Object.keys(this.lines).find(x => this.lines[x] === e.target)
    let child = (this.props.connections || []).find(x => x.id == id)
    if (child && this.props.onConnectionClick)
      this.props.onConnectionClick(child)
  }

  onCircleClick(e) {
    let id = Object.keys(this.circles).find(x => this.circles[x] === e.target)
    let child = (this.props.stations || []).find(x => x.id == id)
    if (child && this.props.onStationClick)
      this.props.onStationClick(child)
  }

  fitBounds() {
    let coords = (this.props.stations || []).map(x => [_lat(x.coorY), _lng(x.coorX)])
    if (coords.length > 1) {
      let bounds = leaflet.latLngBounds(coords)
      this.map.fitBounds(bounds)
    }
    else if (coords.length === 1)
      this.map.setView(coords[0], this.map.getZoom())
    else
      this.map.setView([52.21667, 21.03333], this.map.getZoom())
  }

  adjustWeelZoom(e = null) {
    let wheelEnabled = !this.props.ctrlWheelZoom
    if (!wheelEnabled && e && e.type === 'keydown' && e.ctrlKey)
      wheelEnabled = true
    if (wheelEnabled)
      this.map.scrollWheelZoom.enable()
    else
      this.map.scrollWheelZoom.disable()
  }

  setMarkers(connections = [], locations = [], stations = []) {
    const { details } = this.props
    let connectionId, locationId, stationId
    if (details) {
      if (details.type === 'connection')
        connectionId = details.node.id
      else if (details.type === 'location')
        locationId = details.node.capsuleId
      else if (details.type === 'station')
        stationId = details.node.id
    }
    let lines = connections.reduce((r, x) => {
      let line = this.lines[x.id]
      const startLongitude = _lng(x.startStation.coorX)
      const startLatitude = _lat(x.startStation.coorY)
      const endLongitude = _lng(x.endStation.coorX)
      const endLatitude = _lat(x.endStation.coorY)

      const latLngs = [[startLatitude, startLongitude], [endLatitude, endLongitude]]
      const weight = x.id == connectionId ? 4 : 2
      const opts = { weight, opacity: 0.25 }
      if (!line) {
        line = leaflet.polyline(latLngs)
        line.addEventListener('click', this.onLineClick)
        line.addTo(this.map)
      }
      else
        line.setLatLngs(latLngs)
      
      line.setStyle(opts)

      r[x.id] = line
      return r;
    }, {})

    for (let name of Object.keys(this.lines)) {
      if (!lines[name]) {
        this.lines[name].removeEventListener('click', this.onLineClick)
        this.lines[name].remove()
      }
    }

    let circles = stations.reduce((r, x) => {
      let circle = this.circles[x.id]
      const longitude = _lng(x.coorX)
      const latitude = _lat(x.coorY)
      const { radius, fillColor } = x.id == stationId ? { radius: 10, fillColor: '#dd0000' } : { radius: 5, fillColor: '#ff0000' }
      const opts = { radius, fillColor, fill: true, color: '#f00000' }
      if (!circle) {
        circle = leaflet.circleMarker([latitude, longitude])
        circle.addEventListener('click', this.onCircleClick)
        circle.addTo(this.map)
      }
      else
        circle.setLatLng([latitude, longitude])

      circle.setStyle(opts)
      r[x.id] = circle
      return r;
    }, {})

    for (let name of Object.keys(this.circles)) {
      if (!circles[name]) {
        this.circles[name].removeEventListener('click', this.onCircleClick)
        this.circles[name].remove()
      }
    }

    let markers = locations.reduce((r, x) => {
      let marker = this.markers[x.capsuleId]
      const longitude = _lng(x.coorX)
      const latitude = _lat(x.coorY)
      const radius = x.capsuleId == locationId ? 10 : 5
      const opts = { radius, fillColor: '#000000', fill: true, stroke: false, fillOpacity: 1 }
      if (!marker) {
        marker = leaflet.circleMarker([latitude, longitude])
        marker.addEventListener('click', this.onMarkerClick)
        marker.addTo(this.map)
      }
      else
        marker.setLatLng([latitude, longitude])

      marker.setStyle(opts)
      r[x.capsuleId] = marker
      return r;
    }, {})

    for (let name of Object.keys(this.markers)) {
      if (!markers[name]) {
        this.markers[name].removeEventListener('click', this.onMarkerClick)
        this.markers[name].remove()
      }
    }
    this.markers = markers
    this.lines = lines
    this.circles = circles
  }

  setMapMode(mode) {
    if (this.modeTile) {
      this.map.removeLayer(this.modeTile)
      this.modeTile = null
    }
    if (mode === 'hybrid')
      this.modeTile = _googleHybridTile()
    else if (mode === 'streets')
      this.modeTile = _googleStreetsTile()
    if (this.modeTile)
      this.map.addLayer(this.modeTile)
  }
}

function _lng(x) {
  return Number.parseFloat(x) - 180
}

function _lat(y) {
  return Number.parseFloat(y) - 90
}

function _googleStreetsTile() {
  return leaflet.tileLayer('//{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}', {
    maxZoom: 20,
    subdomains: ['mt0','mt1','mt2','mt3']
  })
}

function _googleHybridTile() {
  return leaflet.tileLayer('//{s}.google.com/vt/lyrs=s,h&x={x}&y={y}&z={z}', {
    maxZoom: 20,
    subdomains: ['mt0','mt1','mt2','mt3']
  })
}