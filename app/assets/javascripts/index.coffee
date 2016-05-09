$(document).ready ->
  message = messages()
  recommend = recommends(message)
  form = forms(recommend)
  location = locations(form, message)
  name = names(form, message)
  allClear = allClears(recommend)

recommends = (message) ->
  new Vue
    el: '#recommend'
    data:
      missions: []
    methods:
      getMission: (params) ->
        getting = $.getJSON '/api/missions', params
        getting.success (json) =>
          @missions = json
          if @missions.length == 0
            message.setError('Not found mission.')
          else
            message.removeError()
        getting.error (error) =>
          if error.status == 403
            location.href = '/session'
          message.setError(error.responseText)
      clear: (idx) ->
        $.post "/api/mission/#{@missions[idx].id}/clear"
        @missions[idx].isClear = true
      feedback: (idx, v) ->
        $.post "/api/mission/#{@missions[idx].id}/feedback", {feedback: v}
        @missions[idx].feedback += v
      allClear: ->
        console.log("recommend.allClear")
        @clear(i) for i in [0..@missions.length - 1]


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
        getting = $.getJSON "/api/location/#{@address}"
        getting.success (json) =>
          form.setLocation(json.latitude, json.longitude)
        getting.error (e) =>
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
