$(document).ready ->
  map = new MyMap('map')
  params = fromURLParameter(location.search.replace(/^\?/, ''))
  lat = params.lat ? 37.786941
  lng = params.lng ? 138.4089693
  zoom = params.zoom ? 7
  map.setView({latitude: lat, longitude: lng}, zoom)
  if params.points
    xs = JSON.parse(params.points)
    markers = (L.marker(x) for x in xs)
    map.addMarkers(markers)
  if params.name
    document.title = decodeURI(params.name)

class MyMap
  constructor: (@mapId) ->
    @markers = []
    @map = L.map(@mapId)
    tiles = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
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

fromURLParameter = (str) ->
  obj = {}
  for kv in str.split('&')
    ary = kv.split('=')
    key = ary.shift()
    obj[key] = ary.join('=')
  obj
