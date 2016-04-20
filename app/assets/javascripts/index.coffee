$(document).ready ->
  message = messages()
  recommend = recommends(message)
  form = forms(recommend)

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
    ready: ->
      navigator.geolocation.getCurrentPosition (pos) =>
        @pos.lat = pos.coords.latitude
        @pos.lng = pos.coords.longitude
        @change()

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
