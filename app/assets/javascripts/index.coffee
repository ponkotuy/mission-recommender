$(document).ready ->
  message = messages()
  mapModal = new MapController('#map')
  mapForm = mapForms(mapModal)
  recommend = recommends(message, mapModal)
  form = forms(recommend)
  location = locations(form, message)
  name = names(form, message)
  allClear = allClears(recommend)

recommends = (message, mapModal) ->
  new Vue
    el: '#recommend'
    data:
      missions: []
    methods:
      getMission: (params) ->
        $.getJSON('/api/missions', params)
        .done (json) =>
          @missions = json
          mapModal.setMissionHeader(@missions, params.meter)
          if @missions.length == 0
            message.setError('Not found mission.')
          else
            message.removeError()
        .fail (error) =>
          if error.status == 403
            location.href = '/session'
          message.setError(error.responseText)
      clear: (idx) ->
        $.post "/api/mission/#{@missions[idx].id}/clear"
        .done =>
          @missions[idx].isClear = true
      feedback: (idx, v) ->
        $.post "/api/mission/#{@missions[idx].id}/feedback", {feedback: v}
        .done =>
          @missions[idx].feedback += v
      notFound: (idx) ->
        $.post "/api/mission/#{@missions[idx].id}/feedback", {notFound: true}
        .done =>
          @missions[idx].notFound = true
      allClear: ->
        console.log("recommend.allClear")
        @clear(i) for i in [0..@missions.length - 1]
      openMap: (mission) ->
        mapModal.setMission(mission)

forms = (recommend) ->
  new Vue
    el: '#control'
    data:
      pos:
        lat: 0
        lng: 0
      meter: "200"
    methods:
      change: ->
        recommend.getMission({lat: @pos.lat, lng: @pos.lng, meter: parseInt(@meter)})
      gps: ->
        navigator.geolocation.getCurrentPosition (pos) =>
          @setLocation(pos.coords.latitude, pos.coords.longitude)
      setLocation: (lat, lng) ->
        @pos.lat = lat
        @pos.lng = lng
        @change()
      searchName: (name) ->
        recommend.getMission({lat: @pos.lat, lng: @pos.lng, q: name})
    ready: ->
      @gps()

messages = ->
  new Vue
    el: '#message'
    data:
      isError: false
      message: ""
    methods:
      setError: (mes) ->
        @message = mes
        @isError = true
      removeError: ->
        @isError = false

locations = (form, message) ->
  new Vue
    el: '#location'
    data:
      address: ""
    methods:
      search: ->
        $.getJSON "/api/location/#{@address}"
        .done (json) =>
          form.setLocation(json.latitude, json.longitude)
        .fail (e) =>
          message.setError(e.responseText)


names = (form) ->
  new Vue
    el: '#name'
    data:
      name: ""
    methods:
      search: ->
        form.searchName(@name)

allClears = (recommend) ->
  new Vue
    el: '#all_clear'
    methods:
      allClear: ->
        recommend.allClear()

mapForms = (mapModal) ->
  new Vue
    el: '#map_form'
    methods:
      clear: ->
        mapModal.clear()
