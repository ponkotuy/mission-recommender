class @MyMap
  constructor: (@mapId) ->
    @markers = []
    @map = L.map(@mapId)
    tiles = L.tileLayer(
      'http://{s}.maps.ponkotuy.com/maps/{z}/{x}/{y}.png',
      {attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'}
    )
    @map.addLayer(tiles)

  addMarkers: (ms) ->
    @markers = @markers.concat(ms)
    for m in ms
      m.addTo(@map)

  setView: (pos, zoom = 16) ->
    @map.setView([pos.latitude, pos.longitude], zoom)

  clearMarkers: ->
    for m in @markers
      @map.removeLayer(m)
    @markers = []

class @MapController
  constructor: (@mapId) ->
    width = $('div.container').offsetWidth
    $('#map').css('width', width)
    @map = new MyMap('map')
    @lastHeader = {missions: [], meter: 0}

  setMission: (mission) ->
    @map.clearMarkers()
    zoom = adjustZoomLevel(mission.around)
    first = mission.portals[0]
    @map.setView(first, zoom)
    markers = mission.portals.map portalMarker
    @map.addMarkers(markers)

  setMissionHeader: (missions, meter) ->
    @map.clearMarkers()
    zoom = adjustZoomLevel(meter)
    first = missions[0].portals[0]
    @map.setView(first, zoom)
    markers = missions.map (m) =>
      first = m.portals[0]
      marker = L.marker([first.latitude, first.longitude]).bindPopup(m.name)
      marker.on 'click', => @setMission(m)
      marker
    @map.addMarkers(markers)
    @lastHeader = {missions: missions, meter: meter}

  clear: ->
    @setMissionHeader(@lastHeader.missions, @lastHeader.meter)

portalMarker = (portal) ->
  L.marker([portal.latitude, portal.longitude]).bindPopup(portal.name)

Scales = [
  500000000, 250000000, 150000000, 70000000, 35000000, 15000000, 10000000, 4000000, 2000000, 1000000,
  500000, 250000, 150000, 70000, 35000, 15000, 8000, 4000, 2000, 1000
]
adjustZoomLevel = (meter) ->
  for rate, zoom in Scales
    if rate / 25 < meter
      return zoom
  return 19
